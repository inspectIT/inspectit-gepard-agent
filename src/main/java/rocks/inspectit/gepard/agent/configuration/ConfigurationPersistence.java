package rocks.inspectit.gepard.agent.configuration;

import java.io.IOException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.file.ConfigurationFileReader;
import rocks.inspectit.gepard.agent.configuration.file.ConfigurationFileWriter;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedSubject;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationMapper;

public class ConfigurationPersistence {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationPersistence.class);

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
      log.info("Local configuration was successfully loaded");
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
    if (configurationIsSame(configuration)) log.info("Configuration has not changed");
    else configurationSubject.notifyObservers(configuration);
  }

  // TODO Remove this method and check for changes in the configuration server
  /**
   * Temporary method to check, whether the configuration has changed.
   *
   * @return true, if the new configuration differs from the current one
   */
  private boolean configurationIsSame(InspectitConfiguration configuration) {
    InspectitConfiguration currentConfig = reader.readConfiguration();
    try {
        String current = ConfigurationMapper.toString(currentConfig);
        String update = ConfigurationMapper.toString(configuration);
        return current.equals(update);
    } catch (IOException e) {
        log.error("Could not compare configurations", e);
        throw new RuntimeException(e);
    }
  }
}
