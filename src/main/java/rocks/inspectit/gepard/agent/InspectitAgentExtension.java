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
import rocks.inspectit.gepard.agent.internal.ServiceLocator;
import rocks.inspectit.gepard.agent.internal.eventbus.EventBusInitializer;
import rocks.inspectit.gepard.agent.internal.schedule.ScheduledExecutorInitializer;

@SuppressWarnings("unused")
@AutoService(AgentExtension.class)
public class InspectitAgentExtension implements AgentExtension {

  private static final Logger log = LoggerFactory.getLogger(InspectitAgentExtension.class);

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

    ScheduledExecutorInitializer.initialize();
    EventBusInitializer.initialize();

    ConfigurationSource configurationSource = PropertiesResolver.getConfigurationSource();

    ConfigurationManager configurationManager =
        ConfigurationManagerFactory.create(configurationSource);

    ClassDiscoveryService classDiscoveryService = new ClassDiscoveryService();
    registerDiscoveryService(classDiscoveryService);

    return agentBuilder;
  }

  private void registerDiscoveryService(ClassDiscoveryService classDiscoveryService) {
    EventBus eventBus = ServiceLocator.getInstance().getService(EventBus.class);
    eventBus.register(classDiscoveryService);
  }


  @Override
  public String extensionName() {
    return "inspectit-gepard";
  }
}
