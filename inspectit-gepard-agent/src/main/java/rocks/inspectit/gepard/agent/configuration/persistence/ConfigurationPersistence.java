/* (C) 2024 */
package rocks.inspectit.gepard.agent.configuration.persistence;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.persistence.file.ConfigurationFileReader;
import rocks.inspectit.gepard.agent.configuration.persistence.file.ConfigurationFileWriter;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedSubject;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;

/**
 * Responsible for accessing the persisted agent configuration as well as keeping the configuration
 * file up to date.
 */
public class ConfigurationPersistence implements ConfigurationReceivedObserver {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationPersistence.class);

  private final ConfigurationFileReader reader;

  private final ConfigurationFileWriter writer;

  private ConfigurationPersistence(ConfigurationFileReader reader, ConfigurationFileWriter writer) {
    this.reader = reader;
    this.writer = writer;
  }

  /**
   * Factory method to create a {@link ConfigurationPersistence}
   *
   * @param reader the file reader
   * @param writer the file writer
   * @return the created persistence object
   */
  public static ConfigurationPersistence create(
      ConfigurationFileReader reader, ConfigurationFileWriter writer) {
    ConfigurationPersistence persistence = new ConfigurationPersistence(reader, writer);
    persistence.subscribeToConfigurationReceivedEvents();
    return persistence;
  }

  /** Tries to load the locally persisted configuration into the agent. */
  public void loadLocalConfiguration() {
    InspectitConfiguration configuration = reader.readConfiguration();

    if (Objects.nonNull(configuration)) {
      log.info("Local configuration was successfully loaded");
      // Temporary remove this as observer, to prevent unnecessary write operation
      ConfigurationReceivedSubject configurationSubject =
          ConfigurationReceivedSubject.getInstance();
      configurationSubject.removeObserver(this);
      configurationSubject.notifyObservers(configuration);
      configurationSubject.addObserver(this);
    }
  }

  /**
   * Updates the configuration persistence file with new configuration.
   *
   * @param event the event with new configuration
   */
  @Override
  public void handleConfiguration(ConfigurationReceivedEvent event) {
    InspectitConfiguration configuration = event.getInspectitConfiguration();
    writer.writeConfiguration(configuration);
  }
}
