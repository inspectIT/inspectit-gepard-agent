package rocks.inspectit.gepard.agent.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.URISyntaxException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.http.HttpRequestSender;
import rocks.inspectit.gepard.agent.notification.http.NotificationCallback;
import rocks.inspectit.gepard.agent.notification.http.NotificationFactory;

/** Executes the start notification to the configuration server. */
public class StartNotifier {
  private static final Logger log = LoggerFactory.getLogger(StartNotifier.class);

  /**
   * Sends a message to the configuration server, to notify it about this agent starting
   *
   * @return true, if the notification was executed successfully
   */
  public boolean sendNotification(String serverBaseUrl) {
    SimpleHttpRequest notification = createStartNotification(serverBaseUrl);
    return HttpRequestSender.send(notification, new NotificationCallback(), 201);
  }

  /**
   * @param serverUrl the url of the configuration server
   * @return the created start notification request
   */
  private SimpleHttpRequest createStartNotification(String serverUrl) {
    SimpleHttpRequest notification = null;
    try {
      notification = NotificationFactory.createStartNotification(serverUrl);
    } catch (URISyntaxException e) {
      log.error("Error building HTTP URI for configuration server notification", e);
    } catch (JsonProcessingException e) {
      log.error("Could not process agent information for configuration server notification", e);
    }
    return notification;
  }
}
