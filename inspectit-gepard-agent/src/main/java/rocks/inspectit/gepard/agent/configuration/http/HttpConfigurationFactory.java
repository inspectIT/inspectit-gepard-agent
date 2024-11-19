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

  public static final String X_GEPARD_SERVICE_NAME = "x-gepard-service-name";
  public static final String X_GEPARD_VM_ID = "x-gepard-vm-id";
  public static final String X_GEPARD_GEPARD_VERSION = "x-gepard-gepard-version";
  public static final String X_GEPARD_OTEL_VERSION = "x-gepard-otel-version";
  public static final String X_GEPARD_JAVA_VERSION = "x-gepard-java-version";
  public static final String X_GEPARD_START_TIME = "x-gepard-start-time";
  public static final String X_GEPARD_ATTRIBUTE = "x-gepard-attribute-";

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
            X_GEPARD_SERVICE_NAME, agent.getServiceName(),
            X_GEPARD_VM_ID, agent.getVmId(),
            X_GEPARD_GEPARD_VERSION, agent.getGepardVersion(),
            X_GEPARD_OTEL_VERSION, agent.getOtelVersion(),
            X_GEPARD_JAVA_VERSION, agent.getJavaVersion(),
            X_GEPARD_START_TIME, agent.getStartTime().toString());

    headers.forEach(requestBuilder::addHeader);
    agent
        .getAttributes()
        .forEach((key, value) -> requestBuilder.addHeader(X_GEPARD_ATTRIBUTE + key, value));

    return requestBuilder;
  }
}
