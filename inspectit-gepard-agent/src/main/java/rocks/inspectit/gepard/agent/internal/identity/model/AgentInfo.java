/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.identity.model;

import io.opentelemetry.javaagent.bootstrap.internal.InstrumentationConfig;
import io.opentelemetry.javaagent.tooling.AgentVersion;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import rocks.inspectit.gepard.agent.internal.identity.IdentityManager;
import rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver;
import rocks.inspectit.gepard.commons.model.agent.Agent;

/** Meta-information about the current agent */
public final class AgentInfo {

  /** Global instance of agent information */
  public static final AgentInfo INFO = new AgentInfo();

  private final Agent agent;

  private final String agentId;

  private AgentInfo() {
    IdentityManager identityManager = IdentityManager.getInstance();
    IdentityInfo identityInfo = identityManager.getIdentityInfo();

    this.agent = createAgent(identityInfo);
    this.agentId = identityInfo.agentId();
  }

  /**
   * Creates an agent model with the current meta-information.
   *
   * @param identityInfo the agent's identity info
   * @return the created agent model
   */
  private Agent createAgent(IdentityInfo identityInfo) {
    RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

    String serviceName = getServiceNameFromSdk();
    String gepardVersion = "0.0.1";
    String otelVersion = AgentVersion.VERSION;
    String javaVersion = System.getProperty("java.version");
    Instant startTime = Instant.ofEpochMilli(runtime.getStartTime());
    String vmId = identityInfo.vmId();
    Map<String, String> attributes = PropertiesResolver.getAttributes();

    return new Agent(
        serviceName, gepardVersion, otelVersion, javaVersion, startTime, vmId, attributes);
  }

  /**
   * @return the agent meta-information
   */
  public Agent getAgent() {
    return agent;
  }

  /**
   * @return the agent id
   */
  public String getAgentId() {
    return agentId;
  }

  /**
   * Returns current service name. If no service name was configured, or it's blank, a default
   * service name will be returned.
   *
   * @return Current service name
   */
  private String getServiceNameFromSdk() {
    InstrumentationConfig config = InstrumentationConfig.get();
    String configuredServiceName = config.getString("otel.service.name");
    return Objects.isNull(configuredServiceName) || configuredServiceName.isBlank()
        ? "inspectit-gepard-agent"
        : configuredServiceName;
  }
}
