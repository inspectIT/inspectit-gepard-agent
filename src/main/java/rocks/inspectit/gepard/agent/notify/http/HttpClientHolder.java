package rocks.inspectit.gepard.agent.notify.http;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.HttpsSupport;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class should hold one single instance of an HTTP client. The client should only be created
 * once, since it's a rather "expensive" object.
 */
public class HttpClientHolder {
  private static final Logger log = LoggerFactory.getLogger(HttpClientHolder.class);

  private static CloseableHttpClient client;

  private HttpClientHolder() {}

  /**
   * Get instance of an HTTP client. If an instance was already created earlier, the previous
   * instance will be returned.
   *
   * @return the HTTP client
   */
  public static CloseableHttpClient getClient() {
    if (client == null) {
      RequestConfig config = getRequestConfig();
      BasicHttpClientConnectionManager connectionManager;
      try {
        connectionManager = getConnectionManager();
      } catch (GeneralSecurityException | IOException e) {
        log.error("Could not create connection manager for HTTP client");
        throw new RuntimeException(e);
      }
      client =
          HttpClientBuilder.create()
              .setDefaultRequestConfig(config)
              .setConnectionManager(connectionManager)
              .build();
    }

    return client;
  }

  /**
   * @return the custom request configuration
   */
  private static RequestConfig getRequestConfig() {
    return RequestConfig.custom()
        .setResponseTimeout(Timeout.ofSeconds(30))
        .setConnectionRequestTimeout(Timeout.ofSeconds(30))
        .build();
  }

  /**
   * @return the connection manager to handle HTTPS as well as HTTP
   */
  private static BasicHttpClientConnectionManager getConnectionManager() throws GeneralSecurityException, IOException {
    SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build();
    HostnameVerifier hostnameVerifier = HttpsSupport.getDefaultHostnameVerifier();
    SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);

    Registry<ConnectionSocketFactory> socketFactoryRegistry =
        RegistryBuilder.<ConnectionSocketFactory>create()
            .register("https", sslFactory)
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .build();

    return new BasicHttpClientConnectionManager(socketFactoryRegistry);
  }
}
