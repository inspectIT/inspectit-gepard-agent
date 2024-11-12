/* (C) 2024 */
package rocks.inspectit.gepard.agent.configuration.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.core5.net.URIBuilder;
import rocks.inspectit.gepard.agent.internal.identity.model.AgentInfo;
import rocks.inspectit.gepard.commons.model.agent.Agent;

/**
 * This factory should create different HTTP requests for the configuration server to fetch
 * inspectit configurations.
 */
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

    AgentInfo agentInfo = AgentInfo.INFO;

    URI uri = new URIBuilder(baseUrl + "/agent-configuration/" + agentInfo.getAgentId()).build();

    SimpleRequestBuilder requestBuilder = SimpleRequestBuilder.get(uri);

    return buildRequestWithHeaders(requestBuilder, agentInfo.getAgent()).build();
  }

  private static SimpleRequestBuilder buildRequestWithHeaders(
      SimpleRequestBuilder requestBuilder, Agent agent) {
    Map<String, String> headers =
        Map.of(
            "x-gepard-service-Name", agent.getServiceName(),
            "x-gepard-vm-id", agent.getVmId(),
            "x-gepard-gepard-version", agent.getGepardVersion(),
            "x-gepard-otel-version", agent.getOtelVersion(),
            "x-gepard-java-version", agent.getJavaVersion(),
            "x-gepard-start-time", agent.getStartTime().toString());

    headers.forEach(requestBuilder::addHeader);
    agent
        .getAttributes()
        .forEach((key, value) -> requestBuilder.addHeader("X-Gepard-Attribute-" + key, value));

    return requestBuilder;
  }
}
