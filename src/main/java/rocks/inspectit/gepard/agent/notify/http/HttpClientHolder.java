package rocks.inspectit.gepard.agent.notify.http;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Objects;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.apache.hc.client5.http.ssl.*;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.notify.http.exception.ClientConfigurationException;

/**
 * This class should hold one single instance of an asynchronous HTTP client. The client should only
 * be created once, since it's a rather "expensive" object.
 */
public class HttpClientHolder {
  private static final Logger log = LoggerFactory.getLogger(HttpClientHolder.class);

  /** Single instance of the HTTP client */
  private static CloseableHttpAsyncClient client;

  private HttpClientHolder() {}

  /**
   * Get instance of an HTTP client. If an instance was already created earlier, the previous
   * instance will be returned.
   *
   * @return the HTTP client
   */
  public static CloseableHttpAsyncClient getClient() {
    if (client == null) {
      RequestConfig config = getRequestConfig();
      AsyncClientConnectionManager connectionManager;
      try {
        connectionManager = getConnectionManager();
      } catch (GeneralSecurityException | IOException e) {
        throw new ClientConfigurationException("Could not create connection manager", e);
      }
      client =
          HttpAsyncClientBuilder.create()
              .setDefaultRequestConfig(config)
              .setConnectionManager(connectionManager)
              .build();
      client.start();
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
   * Creates a connection manager to handle HTTPS communication. Currently, only server certificates
   * with the common name "localhost" are accepted.
   *
   * @return the connection manager to handle HTTPS
   */
  private static AsyncClientConnectionManager getConnectionManager()
      throws GeneralSecurityException, IOException {
    String keystorePath = System.getProperty("javax.net.ssl.trustStore");
    String keystorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
    SSLContext sslContext;

    if (Objects.nonNull(keystorePath) || Objects.nonNull(keystorePassword)) {
      KeyStore keyStore =
          KeyStore.getInstance(new File(keystorePath), keystorePassword.toCharArray());
      sslContext = SSLContexts.custom().loadTrustMaterial(keyStore, null).build();
    } else {
      log.info("No keystore found. Using default SSL context");
      sslContext = SSLContexts.custom().loadTrustMaterial(TrustAllStrategy.INSTANCE).build();
    }

    HostnameVerifier hostnameVerifier = HttpsSupport.getDefaultHostnameVerifier();
    TlsStrategy tlsStrategy =
        ClientTlsStrategyBuilder.create()
            .setSslContext(sslContext)
            .setHostnameVerifier(hostnameVerifier)
            .build();

    return PoolingAsyncClientConnectionManagerBuilder.create().setTlsStrategy(tlsStrategy).build();
  }
}
