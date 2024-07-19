package rocks.inspectit.gepard.agent.configuration;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.http.HttpConfigurationPoller;
import rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver;
import rocks.inspectit.gepard.agent.internal.schedule.InspectitScheduler;

public class ConfigurationManager {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationManager.class);

  private ConfigurationManager() {}

  public static ConfigurationManager create() {
    return new ConfigurationManager();
  }

  /** */
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
