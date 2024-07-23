package rocks.inspectit.gepard.agent;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.tooling.AgentExtension;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.ConfigurationManager;
import rocks.inspectit.gepard.agent.instrumentation.InstrumentationManager;
import rocks.inspectit.gepard.agent.notification.NotificationManager;
import rocks.inspectit.gepard.agent.resolver.ConfigurationHolder;
import rocks.inspectit.gepard.agent.resolver.ConfigurationResolver;
import rocks.inspectit.gepard.agent.transformation.TransformationManager;

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

    // Notify configuration server about this agent
    NotificationManager notificationManager = NotificationManager.create();
    notificationManager.sendStartNotification();

    // Create resolver to apply our configuration to classes, types etc.
    ConfigurationHolder configurationHolder = ConfigurationHolder.create();
    ConfigurationResolver configurationResolver = ConfigurationResolver.create(configurationHolder);

    // Modify the OTel AgentBuilder with our transformer
    TransformationManager transformationManager =
        TransformationManager.create(configurationResolver);
    agentBuilder = transformationManager.modify(agentBuilder);

    // Set up instrumentation
    InstrumentationManager instrumentationManager = InstrumentationManager.create();
    instrumentationManager.createConfigurationReceiver();
    instrumentationManager.startClassDiscovery();
    instrumentationManager.startBatchInstrumentation(configurationResolver);

    // Start polling of the inspectit configuration
    ConfigurationManager configurationManager = ConfigurationManager.create();
    configurationManager.startHttpPolling();

    addShutdownHook();

    return agentBuilder;
  }

  @Override
  public String extensionName() {
    return "inspectit-gepard";
  }

  private void addShutdownHook() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  log.info("Shutting down inspectIT Gepard agent extension...");
                }));
  }
}
