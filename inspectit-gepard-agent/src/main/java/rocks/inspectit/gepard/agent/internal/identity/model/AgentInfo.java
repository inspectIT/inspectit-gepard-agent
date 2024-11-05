/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.identity.model;

import io.opentelemetry.javaagent.bootstrap.internal.InstrumentationConfig;
import io.opentelemetry.javaagent.tooling.AgentVersion;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;
import java.util.Objects;
import rocks.inspectit.gepard.agent.internal.identity.IdentityManager;
import rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver;

/** Meta-information about the current agent */
public final class AgentInfo {

  public static final AgentInfo INFO = new AgentInfo();

  private final String serviceName;

  private final String gepardVersion;

  private final String otelVersion;

  private final String javaVersion;

  private final long startTime;

  private final String vmId;

  private final String agentId;

  private final Map<String, String> attributes;

  private AgentInfo() {
    IdentityManager identityManager = IdentityManager.getInstance();
    IdentityInfo identityInfo = identityManager.getIdentityInfo();
    RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

    this.serviceName = getServiceNameFromSdk();
    this.gepardVersion = "0.0.1";
    this.otelVersion = AgentVersion.VERSION;
    this.javaVersion = System.getProperty("java.version");
    this.startTime = runtime.getStartTime();
    this.vmId = identityInfo.vmId();
    this.agentId = identityInfo.agentId();
    this.attributes = PropertiesResolver.getAttributes();
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
