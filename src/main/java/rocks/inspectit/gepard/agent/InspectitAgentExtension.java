package rocks.inspectit.gepard.agent;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.tooling.AgentExtension;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.notify.NotificationManager;

@SuppressWarnings("unused")
@AutoService(AgentExtension.class)
public class InspectitAgentExtension implements AgentExtension {
  private static final Logger log = LoggerFactory.getLogger(InspectitAgentExtension.class);

  /** Hard coded server url, which should configurable be in the future */
  private static final String SERVER_URL = "https://localhost:8080/api/v1/connections";

  /**
   * Entrypoint for the inspectIT gepard extension
   *
   * @param agentBuilder the configuration builder for the instrumentation agent provided by
   *     OpenTelemetry
   * @param config the properties used for OpenTelemetry autoconfiguration
   * @return the extended AgentBuilder, which will be used by OpenTelemetry
   */
  @Override
  public AgentBuilder extend(AgentBuilder agentBuilder, ConfigProperties config) {
    log.info("Starting inspectIT Gepard agent extension ...");

    boolean successful = NotificationManager.sendStartNotification(SERVER_URL);
    if (successful) log.info("Successfully notified configuration server about start");

    return agentBuilder;
  }

  @Override
  public String extensionName() {
    return "inspectit-gepard";
  }
}
