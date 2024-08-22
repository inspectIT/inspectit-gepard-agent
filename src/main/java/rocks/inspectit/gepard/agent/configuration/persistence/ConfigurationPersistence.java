package rocks.inspectit.gepard.agent.configuration.persistence;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.persistence.file.ConfigurationFileReader;
import rocks.inspectit.gepard.agent.configuration.persistence.file.ConfigurationFileWriter;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedSubject;

public class ConfigurationPersistence {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationPersistence.class);

  private final ConfigurationFileReader reader;

  private final ConfigurationFileWriter writer;

  private final ConfigurationReceivedSubject configurationSubject;

  public ConfigurationPersistence(ConfigurationFileReader reader, ConfigurationFileWriter writer) {
    this.reader = reader;
    this.writer = writer;
    this.configurationSubject = ConfigurationReceivedSubject.getInstance();
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
    configurationSubject.notifyObservers(configuration);
  }
}
