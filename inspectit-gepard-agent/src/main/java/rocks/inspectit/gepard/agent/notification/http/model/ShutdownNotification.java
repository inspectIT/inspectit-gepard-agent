/* (C) 2024 */
package rocks.inspectit.gepard.agent.notification.http.model;

/** Request body for sending a shutdown notification. */
public final class ShutdownNotification {

  public static final ShutdownNotification INSTANCE = new ShutdownNotification();

  private final String connectionStatus = "DISCONNECTED";
}
