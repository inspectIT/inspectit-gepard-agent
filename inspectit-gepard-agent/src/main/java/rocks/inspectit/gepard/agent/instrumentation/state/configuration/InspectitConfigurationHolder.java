/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;

/**
 * When the agent receives a new configuration, it will notify this holder about the new
 * configuration. The InspectitConfigurationHolder will then update its current configuration. The
 * InspectitConfigurationHolder is used by the ConfigurationResolver to determine if a class should
 * be retransformed or instrumented.
 */
public class InspectitConfigurationHolder implements ConfigurationReceivedObserver {
  private static final Logger log = LoggerFactory.getLogger(InspectitConfigurationHolder.class);

  /** Current inspectit configuration */
  private volatile InspectitConfiguration configuration;

  private InspectitConfigurationHolder() {
    this.configuration = new InspectitConfiguration();
  }

  /**
   * Factory method to create an {@link InspectitConfigurationHolder}
   *
   * @return the created holder
   */
  public static InspectitConfigurationHolder create() {
    InspectitConfigurationHolder holder = new InspectitConfigurationHolder();
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
