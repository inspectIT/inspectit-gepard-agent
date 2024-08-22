package rocks.inspectit.gepard.agent.configuration.persistence.file;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationMapper;

public class ConfigurationFileReader {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationFileReader.class);

  private final ConfigurationFileAccessor fileAccessor;

  public ConfigurationFileReader(ConfigurationFileAccessor fileAccessor) {
    this.fileAccessor = fileAccessor;
  }

  public InspectitConfiguration readConfiguration() {
    try {
      String fileContent = fileAccessor.readFile();
      return ConfigurationMapper.toObject(fileContent);
    } catch (IOException e) {
      log.error("Could not read local configuration", e);
      return null;
    }
  }
}
