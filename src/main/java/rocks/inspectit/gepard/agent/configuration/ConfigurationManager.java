package rocks.inspectit.gepard.agent.configuration;

import java.nio.file.Path;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.http.HttpConfigurationPoller;
import rocks.inspectit.gepard.agent.configuration.persistence.ConfigurationPersistence;
import rocks.inspectit.gepard.agent.configuration.persistence.file.ConfigurationFileReader;
import rocks.inspectit.gepard.agent.configuration.persistence.file.ConfigurationFileWriter;
import rocks.inspectit.gepard.agent.internal.file.FileAccessor;
import rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver;
import rocks.inspectit.gepard.agent.internal.schedule.InspectitScheduler;

/** Responsible component for loading the configuration. */
public class ConfigurationManager {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationManager.class);

  private ConfigurationManager() {}

  /**
   * Factory method to create a {@link ConfigurationManager}
   *
   * @return the created manager
   */
  public static ConfigurationManager create() {
    return new ConfigurationManager();
  }

  /** Initializes the loading of the agent configuration */
  public void loadConfiguration() {
    String url = PropertiesResolver.getServerUrl();
    ConfigurationPersistence configPersistence = createConfigurationPersistence();
    if (url.isEmpty()) {
      log.info("No configuration server url was provided - Trying to load local configuration...");
      configPersistence.loadLocalConfiguration();
    } else {
      startHttpPolling(url, configPersistence);
    }
  }

  /**
   * Starts the polling of the HTTP configuration via {@link HttpConfigurationPoller}, if a
   * configuration server url was set up.
   */
  private void startHttpPolling(String serverUrl, ConfigurationPersistence persistence) {
    InspectitScheduler scheduler = InspectitScheduler.getInstance();
    HttpConfigurationPoller poller = new HttpConfigurationPoller(serverUrl, persistence);
    Duration pollingInterval = PropertiesResolver.getPollingInterval();
    scheduler.startRunnable(poller, pollingInterval);
  }

  /**
   * @return the new instance of {@link ConfigurationPersistence}
   */
  private ConfigurationPersistence createConfigurationPersistence() {
    String persistenceFile = PropertiesResolver.getPersistenceFile();
    Path persistenceFilePath = Path.of(persistenceFile);
    FileAccessor configFileAccessor = FileAccessor.create(persistenceFilePath);

    ConfigurationFileReader reader = new ConfigurationFileReader(configFileAccessor);
    ConfigurationFileWriter writer = new ConfigurationFileWriter(configFileAccessor);
    return ConfigurationPersistence.create(reader, writer);
  }
}
