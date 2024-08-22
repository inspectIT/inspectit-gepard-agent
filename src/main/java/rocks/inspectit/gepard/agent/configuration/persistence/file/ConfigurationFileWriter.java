package rocks.inspectit.gepard.agent.configuration.persistence.file;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationMapper;

public class ConfigurationFileWriter {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationFileWriter.class);

  private final ConfigurationFileAccessor fileAccessor;

  public ConfigurationFileWriter(ConfigurationFileAccessor fileAccessor) {
    this.fileAccessor = fileAccessor;
  }

  /**
   * Writes new configuration into the persistence file.
   *
   * @param configuration the new configuration
   */
  public void writeConfiguration(InspectitConfiguration configuration) {
    try {
      String configString = ConfigurationMapper.toString(configuration);
      fileAccessor.writeFile(configString);
      log.info("Local configuration was successfully updated");
    } catch (IOException e) {
      log.error("Could not write configuration file", e);
    }
  }
}
