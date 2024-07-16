package rocks.inspectit.gepard.agent.notify.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.core5.http.ContentType;
import rocks.inspectit.gepard.agent.notify.model.AgentInfo;

/** This factory should create different HTTP requests for the configuration server */
public class NotificationFactory {

  private NotificationFactory() {}

  /**
   * Create an HTTP post request to notify the configuration server about the starting agent and
   * it's information
   *
   * @param baseUrl the base url of the configuration server
   * @return the HTTP post request, containing agent information
   * @throws URISyntaxException invalid uri
   * @throws JsonProcessingException corrupted agent information
   */
  public static SimpleHttpRequest createStartNotification(String baseUrl)
      throws URISyntaxException, JsonProcessingException {
    URI uri = new URI(baseUrl + "/connections");
    String agentInfoString = AgentInfo.getAsString();

    return SimpleRequestBuilder.post(uri)
        .setBody(agentInfoString, ContentType.APPLICATION_JSON)
        .setHeader("content-type", "application/json")
        .build();
  }
}
