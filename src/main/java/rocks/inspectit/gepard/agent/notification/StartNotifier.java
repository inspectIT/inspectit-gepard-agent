package rocks.inspectit.gepard.agent.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
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
  public boolean sendNotification(String serverUrl) {
    SimpleHttpRequest notification = null;
    // TODO try-catch in eigene Methode auslagern
    try {
      notification = NotificationFactory.createStartNotification(serverUrl);
    } catch (URISyntaxException e) {
      log.error("Error building HTTP URI for configuration server notification", e);
    } catch (JsonProcessingException e) {
      log.error("Could not process agent information for configuration server notification", e);
    }

    // TODO try-catch in eigene Methode auslagern
    try {
      return HttpRequestSender.send(notification, new NotificationCallback());
    } catch (ExecutionException e) {
      log.error("Error executing start notification for configuration server", e);
    } catch (InterruptedException e) {
      log.error("Start notification for configuration server was interrupted", e);
      Thread.currentThread().interrupt();
    }
    return false;
  }
}
