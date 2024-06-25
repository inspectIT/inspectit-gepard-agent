package rocks.inspectit.gepard.agent.notify;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.notify.http.HttpClientHolder;
import rocks.inspectit.gepard.agent.notify.http.NotificationFactory;
import rocks.inspectit.gepard.agent.notify.http.NotificationResponseHandler;

/** This manager should notify the configuration server about the agent itself and its status. */
public class NotificationManager {
  private static final Logger log = LoggerFactory.getLogger(NotificationManager.class);

  private static final String SERVER_URL = "http://localhost:8080/api/v1/connections";

  private NotificationManager() {}

  public static boolean sendStartNotification() {
    HttpPost notification = null;
    try {
      notification = NotificationFactory.createStartNotification(SERVER_URL);
    } catch (URISyntaxException e) {
      log.error("Error building HTTP URI for configuration server notification", e);
    } catch (JsonProcessingException e) {
      log.error("Could not process agent information for configuration server notification", e);
    }

    try {
      return doSend(notification);
    } catch (IOException e) {
      log.error("Could not send start notification to configuration server", e);
      return false;
    }
  }

  /**
   * Executes the provided HTTP request.
   *
   * @param request the HTTP request
   * @return True, if the HTTP request could be executed successfully. False, if the request
   *     received an unexpected response or the HTTP request is null
   * @throws IOException In case of a problem or the connection was aborted
   */
  private static boolean doSend(ClassicHttpRequest request) throws IOException {
    if (Objects.isNull(request)) return false;

    CloseableHttpClient client = HttpClientHolder.getClient();
    NotificationResponseHandler responseHandler = new NotificationResponseHandler();
    return client.execute(request, responseHandler);
  }
}
