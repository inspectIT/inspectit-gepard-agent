package rocks.inspectit.gepard.agent.configuration.persistence;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.persistence.file.ConfigurationFileReader;
import rocks.inspectit.gepard.agent.configuration.persistence.file.ConfigurationFileWriter;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedSubject;

public class ConfigurationPersistence implements ConfigurationReceivedObserver {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationPersistence.class);

  private final ConfigurationFileReader reader;

  private final ConfigurationFileWriter writer;

  private final ConfigurationReceivedSubject configurationSubject;

  private ConfigurationPersistence(ConfigurationFileReader reader, ConfigurationFileWriter writer) {
    this.reader = reader;
    this.writer = writer;
    this.configurationSubject = ConfigurationReceivedSubject.getInstance();
  }

  /**
   * Factory method to create a {@link ConfigurationPersistence}
   *
   * @param reader the file reader
   * @param writer the file writer
   * @return the created persistence object
   */
  public static ConfigurationPersistence create(ConfigurationFileReader reader, ConfigurationFileWriter writer) {
    ConfigurationPersistence persistence = new ConfigurationPersistence(reader, writer);
    persistence.subscribeToConfigurationReceivedEvents();
    return persistence;
  }

  /** Tries to load the locally persisted configuration. */
  public void loadLocalConfiguration() {
    InspectitConfiguration configuration = reader.readConfiguration();

    if (Objects.nonNull(configuration)) {
      log.info("Local configuration was successfully loaded");
      // Temporary remove this as observer, to prevent unnecessary write operation
      configurationSubject.removeObserver(this);
      configurationSubject.notifyObservers(configuration);
      configurationSubject.addObserver(this);
    }
  }

  @Override
  public void handleConfiguration(ConfigurationReceivedEvent event) {
    InspectitConfiguration configuration = event.getInspectitConfiguration();
    writer.writeConfiguration(configuration);
  }
}
