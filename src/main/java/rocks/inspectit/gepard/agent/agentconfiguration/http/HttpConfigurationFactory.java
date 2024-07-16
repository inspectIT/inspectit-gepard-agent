package rocks.inspectit.gepard.agent.agentconfiguration.http;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.core5.net.URIBuilder;
import rocks.inspectit.gepard.agent.config.ConfigurationResolver;
import rocks.inspectit.gepard.agent.notify.model.AgentInfo;

public class HttpConfigurationFactory {

  private HttpConfigurationFactory() {}

  /**
   * Create an HTTP post request to ask the configuration server for the agent configuration.
   *
   * @return the HTTP post request, containing agent information
   * @throws URISyntaxException invalid uri
   */
  public static SimpleHttpRequest createConfigurationRequest() throws URISyntaxException {
    long processId = AgentInfo.INFO.getPid();
    String url = ConfigurationResolver.getServerUrl();
    URI uri =
        new URIBuilder(url + "/agent-configuration")
            // Parameters could be added later for mappings
            // .addParameter("pid", String.valueOf(processId))
            .build();

    return SimpleRequestBuilder.get(uri).build();
  }
}
