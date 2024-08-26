package rocks.inspectit.gepard.agent.configuration.persistence.file;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationMapper;
import rocks.inspectit.gepard.agent.internal.file.FileAccessor;

/** Reads the agent configuration persistence file. */
public class ConfigurationFileReader {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationFileReader.class);

  private final FileAccessor configFileAccessor;

  public ConfigurationFileReader(FileAccessor configFileAccessor) {
    this.configFileAccessor = configFileAccessor;
  }

  /**
   * Reads the configuration from the persistence file.
   *
   * @return the currently persisted configuration
   */
  public InspectitConfiguration readConfiguration() {
    try {
      String fileContent = configFileAccessor.readFile();
      return ConfigurationMapper.toObject(fileContent);
    } catch (IOException e) {
      log.error("Could not read local configuration", e);
      return null;
    }
  }
}
