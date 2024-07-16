package rocks.inspectit.gepard.agent;

import com.google.auto.service.AutoService;
import com.google.common.eventbus.EventBus;
import io.opentelemetry.javaagent.tooling.AgentExtension;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.config.ConfigurationManager;
import rocks.inspectit.gepard.agent.config.ConfigurationManagerFactory;
import rocks.inspectit.gepard.agent.config.ConfigurationSource;
import rocks.inspectit.gepard.agent.instrumentation.ClassDiscoveryService;
import rocks.inspectit.gepard.agent.internal.PropertiesResolver;

@SuppressWarnings("unused")
@AutoService(AgentExtension.class)
public class InspectitAgentExtension implements AgentExtension {

  private static final Logger log = LoggerFactory.getLogger(InspectitAgentExtension.class);

  EventBus eventBus = new EventBus();

  // Basically what we want to do here is to register some modules.
  // Currently We´ve got the following functionality:
  // Agent Configuration
  // Instrumentation
  // Let´s introduce a agent configuration service that will be responsible for handling the agent
  // configuration.

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

    ConfigurationSource configurationSource = PropertiesResolver.getConfigurationSource();

    // All we need to know here is, that we want to configure the agent with the configuration
    // service.
    ConfigurationManager configurationManager =
        ConfigurationManagerFactory.create(configurationSource, eventBus);

    ClassDiscoveryService classDiscoveryService = new ClassDiscoveryService();

    eventBus.register(classDiscoveryService);


    // This was moved into the resolver.
    /*String url = PropertiesResolver.getServerUrl();
    if (url.isEmpty()) log.info("No configuration server url was provided");
    else {

      log.info("Sending start notification to configuration server with url: {}", url);
    // This will be handled by the configuration manager.
      boolean successful = NotificationManager.sendStartNotification(url);

      if (successful) {
        log.info("Successfully notified configuration server about start");
        ScheduleManager.getInstance().startPolling(url);
      } else log.warn("Could not notify configuration server about start");
    }*/

    return agentBuilder;
  }

  @Override
  public String extensionName() {
    return "inspectit-gepard";
  }
}
