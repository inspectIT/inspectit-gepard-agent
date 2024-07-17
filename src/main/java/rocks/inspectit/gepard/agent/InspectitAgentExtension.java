package rocks.inspectit.gepard.agent;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.tooling.AgentExtension;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.internal.PropertiesResolver;
import rocks.inspectit.gepard.agent.internal.schedule.ScheduleManager;
import rocks.inspectit.gepard.agent.notify.NotificationManager;

@SuppressWarnings("unused")
@AutoService(AgentExtension.class)
public class InspectitAgentExtension implements AgentExtension {
  private static final Logger log = LoggerFactory.getLogger(InspectitAgentExtension.class);

  /** manager used for starting scheduled tasks */
  private final ScheduleManager scheduleManager = ScheduleManager.getInstance();

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

    String url = PropertiesResolver.getServerUrl();
    if (url.isEmpty()) log.info("No configuration server url was provided");
    else {
      log.info("Sending start notification to configuration server with url: {}", url);
      boolean successful = NotificationManager.sendStartNotification(url);

      if (successful) {
        log.info("Successfully notified configuration server about start");
        scheduleManager.startPolling(url);
      } else log.warn("Could not notify configuration server about start");
    }

    scheduleManager.startClassDiscovery();

    return agentBuilder;
  }

  @Override
  public String extensionName() {
    return "inspectit-gepard";
  }
}
