package rocks.inspectit.gepard.agent.notify.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.javaagent.bootstrap.internal.InstrumentationConfig;
import io.opentelemetry.javaagent.tooling.AgentVersion;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/** Meta-information about the current agent */
public class AgentInfo {

  public static final AgentInfo INFO = new AgentInfo();

  private static final ObjectMapper mapper =
      new ObjectMapper().setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

  private final String serviceName;

  private final String gepardVersion;

  private final String otelVersion;

  private final String javaVersion;

  private final String startTime;

  private final long pid;

  private AgentInfo() {
    InstrumentationConfig config = InstrumentationConfig.get();
    this.serviceName = config.getString("otel.service.name");
    this.gepardVersion = "0.0.1";
    this.otelVersion = AgentVersion.VERSION;
    this.javaVersion = config.getString("java.version");
    RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    this.startTime = String.valueOf(runtime.getStartTime());
    this.pid = runtime.getPid();
  }

  /**
   * @return The agent information as JSON string
   * @throws JsonProcessingException
   */
  public static String getAsString() throws JsonProcessingException {
    return mapper.writeValueAsString(INFO);
  }
}
