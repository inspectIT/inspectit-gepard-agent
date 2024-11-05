/* (C) 2024 */
package rocks.inspectit.gepard.agent.notification.http.model;

import rocks.inspectit.gepard.agent.internal.identity.model.AgentInfo;

/** Request body for sending a shutdown notification. */
public final class ShutdownNotification {

  public static final ShutdownNotification INSTANCE = new ShutdownNotification();

  private final String agentId = AgentInfo.INFO.getAgentId();

  private static final String connectionStatus = "DISCONNECTED";
}
