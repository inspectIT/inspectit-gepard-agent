package rocks.inspectit.gepard.agent.configuration;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.http.HttpConfigurationPoller;
import rocks.inspectit.gepard.agent.internal.ServiceLocator;
import rocks.inspectit.gepard.agent.internal.configuration.PropertiesResolver;
import rocks.inspectit.gepard.agent.internal.schedule.ScheduleManager;

public class ConfigurationManager {
  private static final Logger log = LoggerFactory.getLogger(ConfigurationManager.class);

  ConfigurationManager() {}

  public static void initialize() {
    // Currently, not necessary
    // ServiceLocator.registerService(ConfigurationManager.class, new ConfigurationManager());

    String url = PropertiesResolver.getServerUrl();
    if (url.isEmpty()) log.info("No configuration server url was provided.");
    else {
      log.info("Starting configuration polling from configuration server with url: {}", url);
      ScheduleManager scheduleManager = ServiceLocator.getService(ScheduleManager.class);
      HttpConfigurationPoller poller = new HttpConfigurationPoller(url);
      Duration pollingInterval = PropertiesResolver.getPollingInterval();

      scheduleManager.startRunnable(poller, pollingInterval);
    }
  }
}
