package rocks.inspectit.gepard.agent.config.http;

import java.time.Duration;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.config.ConfigurationManager;
import rocks.inspectit.gepard.agent.config.http.registration.RegistrationManager;
import rocks.inspectit.gepard.agent.internal.PropertiesResolver;
import rocks.inspectit.gepard.agent.internal.schedule.ScheduleManager;

public class HttpConfigurationManager implements ConfigurationManager {

  private static final Logger log = LoggerFactory.getLogger(HttpConfigurationManager.class);
  private final RegistrationManager registrationManager = new RegistrationManager();
  private final HttpConfigurationPoller httpConfigurationPoller;
  private final ScheduleManager scheduleManager = ScheduleManager.getInstance();
  private final Duration interval = PropertiesResolver.getPollingInterval();

  public HttpConfigurationManager() {
    this.httpConfigurationPoller = new HttpConfigurationPoller();
  }

  @Override
  public void manageConfiguration() {
    boolean notificationSuccessful = registrationManager.sendStartNotification();

    if (!notificationSuccessful) {
      log.warn("Could not send start notification. Configuration polling will not be started.");
      return;
    }
    log.info("Successfully notified configuration server about start");
    scheduleManager.startRunnable(httpConfigurationPoller, "HttpConfigurationPoller", interval);
  }
}
