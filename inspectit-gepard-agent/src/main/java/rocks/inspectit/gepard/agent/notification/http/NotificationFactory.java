/* (C) 2024 */
package rocks.inspectit.gepard.agent.notification.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.core5.http.ContentType;
import rocks.inspectit.gepard.agent.internal.identity.model.AgentInfo;
import rocks.inspectit.gepard.agent.notification.http.model.ShutdownNotification;

/** This factory should create different HTTP requests for the configuration server */
public class NotificationFactory {

  private static final ObjectMapper mapper =
      new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

  private NotificationFactory() {}

  /**
   * Create an HTTP post request to notify the configuration server about the starting agent and
   * it's information.
   *
   * @param baseUrl the base url of the configuration server
   * @return the HTTP post request, containing agent information
   * @throws URISyntaxException invalid uri
   * @throws JsonProcessingException corrupted agent information
   */
  public static SimpleHttpRequest createStartNotification(String baseUrl)
      throws URISyntaxException, JsonProcessingException {
    String agentId = AgentInfo.INFO.getAgentId();
    URI uri = new URI(baseUrl + "/connections/" + agentId);
    String agentInfoString = mapper.writeValueAsString(AgentInfo.INFO);

    return SimpleRequestBuilder.post(uri)
        .setBody(agentInfoString, ContentType.APPLICATION_JSON)
        .setHeader("content-type", "application/json")
        .build();
  }

  /**
   * Create an HTTP put request to notify the configuration server about the shutting down agent.
   *
   * @param baseUrl the base url of the configuration server
   * @return the HTTP post request, containing agent information
   * @throws URISyntaxException invalid uri
   * @throws JsonProcessingException corrupted agent information
   */
  public static SimpleHttpRequest createShutdownNotification(String baseUrl)
      throws URISyntaxException, JsonProcessingException {
    String agentId = AgentInfo.INFO.getAgentId();
    URI uri = new URI(baseUrl + "/connections/" + agentId);
    String notificationBody = mapper.writeValueAsString(ShutdownNotification.INSTANCE);

    return SimpleRequestBuilder.patch(uri)
        .setBody(notificationBody, ContentType.APPLICATION_JSON)
        .setHeader("content-type", "application/json")
        .build();
  }
}
