package rocks.inspectit.gepard.agent.notify.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;
import rocks.inspectit.gepard.agent.notify.model.AgentInfo;

/** This factory should create different HTTP requests for the configuration server */
public class NotificationFactory {

  private NotificationFactory() {}

  /**
   * Create an HTTP post request to notify the configuration server about the starting agent and
   * it's information
   *
   * @return the HTTP post request, containing agent information
   * @throws URISyntaxException invalid uri
   * @throws JsonProcessingException corrupted agent information
   */
  public static HttpPost createStartNotification(String url)
      throws URISyntaxException, JsonProcessingException {
    URI uri = new URI(url);
    HttpPost httpPost = new HttpPost(uri);

    String agentInfoString = AgentInfo.getAsString();
    httpPost.setEntity(new StringEntity(agentInfoString));
    httpPost.setHeader("content-type", "application/json");

    return httpPost;
  }
}
