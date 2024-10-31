/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.identity.model;

public final class IdentityInfo {

  private final long pid;
  private final String hostname;
  private final String agentId;

  public IdentityInfo(long pid, String hostname, String agentId) {
    this.pid = pid;
    this.hostname = hostname;
    this.agentId = agentId;
  }

  public long pid() {
    return pid;
  }

  public String hostname() {
    return hostname;
  }

  public String agentId() {
    return agentId;
  }
}
