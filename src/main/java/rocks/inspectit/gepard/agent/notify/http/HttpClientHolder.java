package rocks.inspectit.gepard.agent.notify.http;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.util.Timeout;

/**
 * This class should hold one single instance of an HTTP client. The client should only be created
 * once, since it's a rather "expensive" object.
 */
public class HttpClientHolder {

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
      RequestConfig config =
          RequestConfig.custom()
              .setResponseTimeout(Timeout.ofSeconds(30))
              .setConnectionRequestTimeout(Timeout.ofSeconds(30))
              .build();
      client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    return client;
  }
}
