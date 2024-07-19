package rocks.inspectit.gepard.agent.internal.configuration;

import java.util.Objects;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;

public class ConfigurationHolder implements ConfigurationReceivedObserver {

  private static ConfigurationHolder instance;

  /** Single instance of the current inspectit configuration */
  private InspectitConfiguration configuration;

  private ConfigurationHolder() {}

  public static ConfigurationHolder getInstance() {
    if (Objects.isNull(instance)) {
      instance = new ConfigurationHolder();
      instance.subscribeToConfigurationReceivedEvents();
    }
    return instance;
  }

  public InspectitConfiguration getConfiguration() {
    return configuration;
  }

  @Override
  public void handleConfiguration(ConfigurationReceivedEvent event) {
    // Currently, we just overwrite the configuration
    configuration = event.getInstrumentationConfiguration();
  }
}
