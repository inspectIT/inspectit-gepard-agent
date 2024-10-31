/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.identity.model;

public final class IdentityInfo {

  private final String vmId;
  private final String agentId;

  public IdentityInfo(String vmId, String agentId) {
    this.vmId = vmId;
    this.agentId = agentId;
  }

  public String vmId() {
    return vmId;
  }

  public String agentId() {
    return agentId;
  }
}
