package rocks.inspectit.gepard.agent.configuration.persistence.file;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationMapper;

public class ConfigurationFileWriter implements ConfigurationReceivedObserver {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationFileWriter.class);

  private final ConfigurationFileAccessor fileAccessor;

  private ConfigurationFileWriter(ConfigurationFileAccessor fileAccessor) {
    this.fileAccessor = fileAccessor;
  }

  /**
   * Factory method to create a {@link ConfigurationFileWriter}
   *
   * @return the created writer
   */
  public static ConfigurationFileWriter create(ConfigurationFileAccessor fileAccessor) {
    ConfigurationFileWriter writer = new ConfigurationFileWriter(fileAccessor);
    writer.subscribeToConfigurationReceivedEvents();
    return writer;
  }

  @Override
  public void handleConfiguration(ConfigurationReceivedEvent event) {
    InspectitConfiguration configuration = event.getInspectitConfiguration();
    try {
      String configString = ConfigurationMapper.toString(configuration);
      fileAccessor.writeFile(configString);
      log.info("Local configuration was successfully updated");
    } catch (IOException e) {
      log.error("Could not write configuration file", e);
    }
  }
}
