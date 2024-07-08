package rocks.inspectit.gepard.agent.notify.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.javaagent.bootstrap.internal.InstrumentationConfig;
import io.opentelemetry.javaagent.tooling.AgentVersion;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Objects;

/** Meta-information about the current agent */
public class AgentInfo {

  public static final AgentInfo INFO = new AgentInfo();

  private static final ObjectMapper mapper =
      new ObjectMapper().setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

  private final String serviceName;

  private final String gepardVersion;

  private final String otelVersion;

  private final String javaVersion;

  private final long startTime;

  private final long pid;

  private AgentInfo() {
    this.serviceName = getServiceName();
    this.gepardVersion = "0.0.1";
    this.otelVersion = AgentVersion.VERSION;
    this.javaVersion = System.getProperty("java.version");
    RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    this.startTime = runtime.getStartTime();
    this.pid = runtime.getPid();
  }

  /**
   * @return The agent information as JSON string
   * @throws JsonProcessingException corrupted agent information
   */
  public static String getAsString() throws JsonProcessingException {
    return mapper.writeValueAsString(INFO);
  }

  /**
   * Returns current service name. If no service name was configured, or it's blank, a default
   * service name will be returned.
   *
   * @return Current service name
   */
  private String getServiceName() {
    InstrumentationConfig config = InstrumentationConfig.get();
    String configuredServiceName = config.getString("otel.service.name");
    return Objects.isNull(configuredServiceName) || configuredServiceName.isBlank()
        ? "inspectit-gepard-agent"
        : configuredServiceName;
  }
}
