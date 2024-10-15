/* (C) 2024 */
package rocks.inspectit.gepard.agent;

import com.google.auto.service.AutoService;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.javaagent.tooling.AgentExtension;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.bootstrap.BootstrapManager;
import rocks.inspectit.gepard.agent.configuration.ConfigurationManager;
import rocks.inspectit.gepard.agent.instrumentation.InstrumentationManager;
import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHookManager;
import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHookState;
import rocks.inspectit.gepard.agent.instrumentation.state.InstrumentationState;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.ConfigurationResolver;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.InspectitConfigurationHolder;
import rocks.inspectit.gepard.agent.internal.otel.OpenTelemetryAccessor;
import rocks.inspectit.gepard.agent.notification.NotificationManager;
import rocks.inspectit.gepard.agent.transformation.TransformationManager;

@SuppressWarnings("unused")
@AutoService(AgentExtension.class)
public class InspectitAgentExtension implements AgentExtension {
  private static final Logger log = LoggerFactory.getLogger(InspectitAgentExtension.class);

  /**
   * Entrypoint for the inspectIT gepard extension.
   *
   * @param agentBuilder the configuration builder for the instrumentation agent provided by
   *     OpenTelemetry
   * @param config the properties used for OpenTelemetry autoconfiguration
   * @return the extended AgentBuilder, which will be used by OpenTelemetry
   */
  @Override
  public AgentBuilder extend(AgentBuilder agentBuilder, ConfigProperties config) {
    log.info("Starting inspectIT Gepard agent extension ...");



    // Append the bootstrap classloader with inspectIT interfaces
    BootstrapManager bootstrapManager = BootstrapManager.create();
    bootstrapManager.appendToBootstrapClassLoader();

    // Notify configuration server about this agent
    NotificationManager notificationManager = NotificationManager.create();
    notificationManager.sendStartNotification();

    // Set our global OpenTelemetry instance. For now, we use the Agent SDK
    OpenTelemetryAccessor.setOpenTelemetry(GlobalOpenTelemetry.get());

    // Set up methods hooks to execute inspectIT code inside target applications
    MethodHookManager methodHookManager = MethodHookManager.create(new MethodHookState());

    // Prepare instrumentation state tracking
    InspectitConfigurationHolder configurationHolder = InspectitConfigurationHolder.create();
    ConfigurationResolver configurationResolver = ConfigurationResolver.create(configurationHolder);
    InstrumentationState instrumentationState =
        InstrumentationState.create(configurationResolver, methodHookManager);

    // Modify the OTel AgentBuilder with our transformer
    TransformationManager transformationManager =
        TransformationManager.create(instrumentationState);
    agentBuilder = transformationManager.modify(agentBuilder);

    // Set up dynamic instrumentation
    InstrumentationManager instrumentationManager = InstrumentationManager.create();
    instrumentationManager.createConfigurationReceiver();
    instrumentationManager.startClassDiscovery();
    instrumentationManager.startBatchInstrumentation(instrumentationState);

    // Start loading the inspectIT configuration
    ConfigurationManager configurationManager = ConfigurationManager.create();
    configurationManager.loadConfiguration();

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
