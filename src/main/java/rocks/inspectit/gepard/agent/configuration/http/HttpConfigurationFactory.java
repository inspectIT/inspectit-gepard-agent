package rocks.inspectit.gepard.agent.configuration.http;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.core5.net.URIBuilder;

public class HttpConfigurationFactory {

  private HttpConfigurationFactory() {}

  /**
   * Create an HTTP post request to ask the configuration server for the agent configuration.
   *
   * @return the HTTP post request, containing agent information
   * @throws URISyntaxException invalid uri
   */
  public static SimpleHttpRequest createConfigurationRequest(String baseUrl)
      throws URISyntaxException {
    URI uri = new URIBuilder(baseUrl + "/agent-configuration").build();

    return SimpleRequestBuilder.get(uri).build();
  }
}
