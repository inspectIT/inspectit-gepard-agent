/* (C) 2024 */
package rocks.inspectit.gepard.agent.configuration.persistence.file;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationMapper;
import rocks.inspectit.gepard.agent.internal.file.FileAccessor;

/**
 * Writes into the agent configuration persistence file. After every configuration update, the
 * persistence file should be updated as well.
 */
public class ConfigurationFileWriter {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationFileWriter.class);

  private final FileAccessor configFileAccessor;

  public ConfigurationFileWriter(FileAccessor configFileAccessor) {
    this.configFileAccessor = configFileAccessor;
  }

  /**
   * Writes new configuration into the persistence file.
   *
   * @param configuration the new configuration
   */
  public void writeConfiguration(InspectitConfiguration configuration) {
    try {
      String configString = ConfigurationMapper.toString(configuration);
      configFileAccessor.writeFile(configString);
      log.info("Local configuration was successfully updated");
    } catch (IOException e) {
      log.error("Could not write configuration file", e);
    }
  }
}
