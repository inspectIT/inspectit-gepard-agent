package rocks.inspectit.gepard.agent;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.tooling.AgentExtension;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.ConfigurationManager;
import rocks.inspectit.gepard.agent.configuration.ConfigurationSubject;
import rocks.inspectit.gepard.agent.instrumentation.InstrumentationManager;
import rocks.inspectit.gepard.agent.internal.schedule.ScheduleManager;
import rocks.inspectit.gepard.agent.internal.schedule.ScheduledExecutorServiceInitializer;
import rocks.inspectit.gepard.agent.notification.NotificationManager;

import static net.bytebuddy.matcher.ElementMatchers.any;

@SuppressWarnings("unused")
@AutoService(AgentExtension.class)
public class InspectitAgentExtension implements AgentExtension {
  private static final Logger log = LoggerFactory.getLogger(InspectitAgentExtension.class);

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

    ScheduledExecutorServiceInitializer.initialize();
    ScheduleManager.initialize();
    NotificationManager.initialize();
    ConfigurationManager.initialize();
    InstrumentationManager.initialize();

    ConfigurationSubject configurationSubject = new ConfigurationSubject();

    return agentBuilder;
  }

  @Override
  public String extensionName() {
    return "inspectit-gepard";
  }
}
