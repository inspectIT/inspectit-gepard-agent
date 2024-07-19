package rocks.inspectit.gepard.agent.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver;

/** This manager should notify the configuration server about the agent itself and its status. */
public class NotificationManager {
  private static final Logger log = LoggerFactory.getLogger(NotificationManager.class);

  private final String serverBaseUrl;

  private final StartNotifier startNotifier;

  private NotificationManager(String serverBaseUrl) {
    this.serverBaseUrl = serverBaseUrl;
    this.startNotifier = new StartNotifier();
  }

  public static NotificationManager create() {
    String url = PropertiesResolver.getServerUrl();
    return new NotificationManager(url);
  }

  /**
   * Sends a message to the configuration server, to notify it about this agent starting
   *
   * @return True, if the notification was executed successfully
   */
  public boolean sendStartNotification() {
    boolean successful = false;
    if (serverBaseUrl.isEmpty()) log.info("No configuration server url was provided");
    else {
      log.info("Sending start notification to configuration server with url: {}", serverBaseUrl);
      successful = startNotifier.sendNotification(serverBaseUrl);

      if (successful) log.info("Successfully notified configuration server about start");
      else log.warn("Could not notify configuration server about start");
    }
    return successful;
  }
}
