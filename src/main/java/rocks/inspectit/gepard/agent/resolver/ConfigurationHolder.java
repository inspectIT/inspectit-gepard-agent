package rocks.inspectit.gepard.agent.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;

public class ConfigurationHolder implements ConfigurationReceivedObserver {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationHolder.class);

  /** Current inspectit configuration */
  private InspectitConfiguration configuration;

  private ConfigurationHolder() {
    this.configuration = new InspectitConfiguration();
  }

  /**
   * Factory method to create an {@link ConfigurationHolder}
   *
   * @return the created holder
   */
  public static ConfigurationHolder create() {
    ConfigurationHolder holder = new ConfigurationHolder();
    holder.subscribeToConfigurationReceivedEvents();
    return holder;
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
