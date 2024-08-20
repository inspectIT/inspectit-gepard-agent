package rocks.inspectit.gepard.agent.configuration.file;

import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.file.ConfigurationFileAccessor;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationMapper;
import rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver;

public class ConfigurationFileWriter implements ConfigurationReceivedObserver {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationFileWriter.class);

  private final Path filePath;

  private final ConfigurationFileAccessor fileAccessor;

  private ConfigurationFileWriter(Path filePath) {
    this.filePath = filePath;
    this.fileAccessor = ConfigurationFileAccessor.getInstance();
  }

  /**
   * Factory method to create a {@link ConfigurationFileWriter}
   *
   * @return the created writer
   */
  public static ConfigurationFileWriter create() {
    String fileName = PropertiesResolver.getPersistenceFile();
    Path filePath = Path.of(fileName);
    ConfigurationFileWriter writer = new ConfigurationFileWriter(filePath);
    writer.subscribeToConfigurationReceivedEvents();
    return writer;
  }

  @Override
  public void handleConfiguration(ConfigurationReceivedEvent event) {
    InspectitConfiguration configuration = event.getInspectitConfiguration();
    try {
      String configString = ConfigurationMapper.toString(configuration);
      fileAccessor.writeFile(filePath, configString);
      log.info("Local configuration was successfully updated");
    } catch (IOException e) {
      log.error("Could not write configuration file", e);
    }
  }
}
