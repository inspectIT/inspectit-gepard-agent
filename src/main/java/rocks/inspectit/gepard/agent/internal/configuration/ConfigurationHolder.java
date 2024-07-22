package rocks.inspectit.gepard.agent.internal.configuration;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;

public class ConfigurationHolder implements ConfigurationReceivedObserver {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationHolder.class);

  private static ConfigurationHolder instance;

  /** Single instance of the current inspectit configuration */
  private InspectitConfiguration configuration;

  private ConfigurationHolder() {
    this.configuration = new InspectitConfiguration();
  }

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
    log.debug("Received new configuration. Updating current configuration...");
    // Currently, we just overwrite the configuration
    configuration = event.getInspectitConfiguration();
  }
}
