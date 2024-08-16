package rocks.inspectit.gepard.agent.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;

/**
 * When the agent receives a new Configuration from the Configuration Server, it will notify the
 * ConfigurationHolder about the new Configuration. The ConfigurationHolder will then update its
 * current Configuration. The ConfigurationHolder is used by the ConfigurationResolver to determine
 * if a class should be retransformed or instrumented.
 */
public class ConfigurationHolder implements ConfigurationReceivedObserver {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationHolder.class);

  /** Current inspectit configuration */
  private volatile InspectitConfiguration configuration;

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
