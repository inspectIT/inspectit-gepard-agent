package rocks.inspectit.gepard.agent.internal.schedule;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.config.http.HttpConfigurationPoller;
import rocks.inspectit.gepard.agent.config.internal.PropertiesResolver;
import rocks.inspectit.gepard.agent.internal.ApplicationConfiguration;

/**
 * Global manager, who starts scheduled task and keeps track of them. At shutdown all scheduled
 * tasks are cancelled.
 */
public class ScheduleManager {
  private static final Logger log = LoggerFactory.getLogger(ScheduleManager.class);

  /** singleton instance */
  private static ScheduleManager instance;

  /** executor for runnables */
  private static final ScheduledExecutorService executor =
      ApplicationConfiguration.getScheduledExecutorService();

  /** set of already scheduled futures */
  private static final Set<NamedScheduledFuture> scheduledFutures = new HashSet<>();

  private ScheduleManager() {}

  /**
   * @return the single instance of the ScheduleManager
   */
  public static ScheduleManager getInstance() {
    if (Objects.isNull(instance)) {
      instance = new ScheduleManager();
      addShutdownHook();
    }
    return instance;
  }

  /**
   * Start the polling of configuration via HTTP
   *
   * @param serverUrl the url of the configuration server
   */
  public void startPolling(String serverUrl) {
    String futureName = "config-polling";
    if (isAlreadyScheduled(futureName)) {
      log.info("Configuration polling is already scheduled");
      return;
    }

    Duration pollingInterval = PropertiesResolver.getPollingInterval();
    log.info(
        "Starting configuration polling with interval of {} seconds", pollingInterval.getSeconds());
    ScheduledFuture<?> future =
        executor.scheduleWithFixedDelay(
            new HttpConfigurationPoller(serverUrl),
            0,
            pollingInterval.toMillis(),
            TimeUnit.MILLISECONDS);
    NamedScheduledFuture namedFuture = new NamedScheduledFuture(future, futureName);
    scheduledFutures.add(namedFuture);
  }

  public void startClassDiscovery() {
    String futureName = "class-discovery";
    if (isAlreadyScheduled(futureName)) {
      log.info("Class discovery is already scheduled");
      return;
    }

    log.info("Starting class discovery...");
    // TODO
  }

  /** Add hook, so every scheduled future will be cancelled at shutdown */
  private static void addShutdownHook() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  scheduledFutures.forEach(
                      f -> {
                        log.info("Shutting down {}", f.getName());
                        f.cancel(true);
                      });
                }));
  }

  /**
   * Filters, if the specified future name is already scheduled
   *
   * @param futureName the name of the future, which should be filtered for
   * @return true, if the future is already scheduled
   */
  private boolean isAlreadyScheduled(String futureName) {
    Optional<NamedScheduledFuture> maybeFuture =
        scheduledFutures.stream().filter(f -> futureName.equals(f.getName())).findAny();
    return maybeFuture.isPresent();
  }
}
