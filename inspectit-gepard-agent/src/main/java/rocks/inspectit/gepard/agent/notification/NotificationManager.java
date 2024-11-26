/* (C) 2024 */
package rocks.inspectit.gepard.agent.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver;
import rocks.inspectit.gepard.agent.internal.shutdown.ShutdownHookManager;

/**
 * Responsible component for notifying the configuration server about the agent itself and its
 * status.
 */
public class NotificationManager {
  private static final Logger log = LoggerFactory.getLogger(NotificationManager.class);

  private final String serverBaseUrl;

  private final ShutdownNotifier shutdownNotifier;

  private NotificationManager(String serverBaseUrl) {
    this.serverBaseUrl = serverBaseUrl;
    this.shutdownNotifier = new ShutdownNotifier();
  }

  /**
   * Factory method to create an {@link NotificationManager}
   *
   * @return the created manager
   */
  public static NotificationManager create() {
    String url = PropertiesResolver.getServerUrl();
    return new NotificationManager(url);
  }

  /**
   * Sets up a shutdown notification to the configuration server, if a configuration server url was
   * provided.
   */
  public void setUpShutdownNotification() {
    if (serverBaseUrl.isEmpty()) return;
    ShutdownHookManager.getInstance().addShutdownHook(this::sendShutdownNotification);
  }

  /** Sends a message to the configuration server, to notify it about this agent shutting down. */
  private void sendShutdownNotification() {
    log.info("Sending shutdown notification to configuration server with url: {}", serverBaseUrl);
    boolean successful = shutdownNotifier.sendNotification(serverBaseUrl);
    if (successful) log.info("Configuration server was notified about shutdown successfully");
    else log.info("Something went wrong while notifying the configuration server about shutdown");
  }
}
