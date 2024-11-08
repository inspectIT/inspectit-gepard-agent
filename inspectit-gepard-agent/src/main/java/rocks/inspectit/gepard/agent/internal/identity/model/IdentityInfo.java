/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.identity.model;

/** Information about the identity of this agent. */
public final class IdentityInfo {

  private final String vmId;

  private final String agentId;

  public IdentityInfo(String vmId, String agentId) {
    this.vmId = vmId;
    this.agentId = agentId;
  }

  /**
   * @return the VM id, created by the JVM
   */
  public String vmId() {
    return vmId;
  }

  /**
   * @return the agent id, created by us
   */
  public String agentId() {
    return agentId;
  }
}
