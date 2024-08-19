package rocks.inspectit.gepard.agent.configuration;

import java.util.Objects;
import rocks.inspectit.gepard.agent.configuration.file.ConfigurationFileReader;
import rocks.inspectit.gepard.agent.configuration.file.ConfigurationFileWriter;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedSubject;

public class ConfigurationPersistence {

  private final ConfigurationFileReader reader;

  private final ConfigurationFileWriter writer;

  private final ConfigurationReceivedSubject configurationSubject;

  private ConfigurationPersistence(
      ConfigurationFileReader reader,
      ConfigurationFileWriter writer,
      ConfigurationReceivedSubject configurationSubject) {
    this.reader = reader;
    this.writer = writer;
    this.configurationSubject = configurationSubject;
  }

  /**
   * Factory method to create a {@link ConfigurationPersistence}
   *
   * @return the created instance
   */
  public static ConfigurationPersistence create() {
    ConfigurationFileReader reader = ConfigurationFileReader.create();
    ConfigurationFileWriter writer = ConfigurationFileWriter.create();
    ConfigurationReceivedSubject subject = ConfigurationReceivedSubject.getInstance();
    return new ConfigurationPersistence(reader, writer, subject);
  }

  /** Tries to load the locally persisted configuration. */
  public void loadLocalConfiguration() {
    InspectitConfiguration configuration = reader.readConfiguration();

    if (Objects.nonNull(configuration)) {
      // Temporary remove writer as observer, to prevent unnecessary write operation
      configurationSubject.removeObserver(writer);
      configurationSubject.notifyObservers(configuration);
      configurationSubject.addObserver(writer);
    }
  }

  /**
   * Processes new configuration by notifying all observers about new configuration.
   *
   * @param configuration the new configuration
   */
  public void processConfiguration(InspectitConfiguration configuration) {
    configurationSubject.notifyObservers(configuration);
  }
}
