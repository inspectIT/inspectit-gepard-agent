/* (C) 2024 */
package rocks.inspectit.gepard.agent.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.URISyntaxException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.http.HttpRequestSender;
import rocks.inspectit.gepard.agent.notification.http.NotificationFactory;

/**
 * Executes the shutdown notification to the configuration server, if the application is shutdown
 * gracefully.
 */
public class ShutdownNotifier {
  private static final Logger log = LoggerFactory.getLogger(ShutdownNotifier.class);

  /** Sends a message to the configuration server, to notify it about this agent shutting down */
  public boolean sendNotification(String serverBaseUrl) {
    SimpleHttpRequest notification = createShutdownNotification(serverBaseUrl);
    return HttpRequestSender.send(notification, null);
  }

  /**
   * @param serverUrl the url of the configuration server
   * @return the created shutdown notification request
   */
  private SimpleHttpRequest createShutdownNotification(String serverUrl) {
    SimpleHttpRequest notification = null;
    try {
      notification = NotificationFactory.createShutdownNotification(serverUrl);
    } catch (URISyntaxException e) {
      log.error("Error building HTTP URI for configuration server notification", e);
    } catch (JsonProcessingException e) {
      log.error("Could not process agent information for configuration server notification", e);
    }
    return notification;
  }
}
