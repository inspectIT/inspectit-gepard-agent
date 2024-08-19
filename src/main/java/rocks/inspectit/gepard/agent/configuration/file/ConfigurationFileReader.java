package rocks.inspectit.gepard.agent.configuration.file;

import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.file.ConfigurationFileAccessor;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationMapper;
import rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver;

public class ConfigurationFileReader {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationFileReader.class);

  private final Path filePath;

  private final ConfigurationFileAccessor fileAccessor;

  private ConfigurationFileReader(Path filePath) {
    this.filePath = filePath;
    this.fileAccessor = ConfigurationFileAccessor.getInstance();
  }

  /**
   * Factory method to create a {@link ConfigurationFileReader}
   *
   * @return the created reader
   */
  public static ConfigurationFileReader create() {
    String fileName = PropertiesResolver.getPersistenceFile();
    Path filePath = Path.of(fileName);
    return new ConfigurationFileReader(filePath);
  }

  public InspectitConfiguration readConfiguration() {
    try {
      byte[] rawFileContent = fileAccessor.readFile(filePath);
      String fileContent = new String(rawFileContent); // TODO Add encoding?
      return ConfigurationMapper.toObject(fileContent);
    } catch (IOException e) {
      log.error("Could not read configuration file", e);
      return null;
    }
  }
}
