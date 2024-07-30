package rocks.inspectit.gepard.agent.configuration;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.http.HttpConfigurationPoller;
import rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver;
import rocks.inspectit.gepard.agent.internal.schedule.InspectitScheduler;

/** Responsible component for loading the configuration. */
public class ConfigurationManager {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationManager.class);

  private ConfigurationManager() {}

  /**
   * Factory method to create an {@link ConfigurationManager}
   *
   * @return the created manager
   */
  public static ConfigurationManager create() {
    return new ConfigurationManager();
  }

  /**
   * Starts the polling of the HTTP configuration via {@link HttpConfigurationPoller}, if a
   * configuration server url was set up.
   */
  public void startHttpPolling() {
    String url = PropertiesResolver.getServerUrl();
    if (url.isEmpty()) log.info("No configuration server url was provided");
    else {
      log.info("Starting configuration polling from configuration server with url: {}", url);
      InspectitScheduler scheduler = InspectitScheduler.getInstance();
      HttpConfigurationPoller poller = new HttpConfigurationPoller(url);
      Duration pollingInterval = PropertiesResolver.getPollingInterval();
      scheduler.startRunnable(poller, pollingInterval);
    }
  }
}
