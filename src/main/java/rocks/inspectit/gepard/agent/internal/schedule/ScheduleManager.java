package rocks.inspectit.gepard.agent.internal.schedule;

import java.time.Duration;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.ServiceLocator;

/**
 * Global manager, who starts scheduled task and keeps track of them. At shutdown all scheduled
 * tasks are cancelled.
 */
public class ScheduleManager {
  private static final Logger log = LoggerFactory.getLogger(ScheduleManager.class);

  /** executor for runnables */
  private final ScheduledExecutorService executor;

  /** set of already scheduled futures */
  private final ConcurrentMap<String, ScheduledFuture<?>> scheduledFutures;

  ScheduleManager() {
    this.executor = ServiceLocator.getService(ScheduledExecutorService.class);
    this.scheduledFutures = new ConcurrentHashMap<>();
    addShutdownHook();
  }

  /**
   * Schedules the runnable with the provided interval.
   *
   * @param runnable the runnable, which should be scheduled. Mostly a class, which implements the
   *     {@link Runnable} interface.
   * @param interval the interval, in which the runnable should be executed in milliseconds
   */
  public void startRunnable(NamedRunnable runnable, Duration interval) {
    String name = runnable.getName();
    if (isAlreadyScheduled(name)) {
      log.info("{} is already scheduled", name);
      return;
    }

    log.info("Starting {} with interval of {} seconds", name, interval.getSeconds());
    ScheduledFuture<?> future =
        executor.scheduleWithFixedDelay(runnable, 0, interval.toMillis(), TimeUnit.MILLISECONDS);
    scheduledFutures.put(name, future);
  }

  /** Add hook, so every scheduled future will be cancelled at shutdown */
  private void addShutdownHook() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () ->
                    scheduledFutures.forEach(
                        (name, future) -> {
                          log.info("Shutting down {}", name);
                          future.cancel(true);
                        })));
  }

  /**
   * Filters, if the specified runnable name is already scheduled
   *
   * @param runnableName the name of the runnable, which should be filtered for
   * @return true, if the runnable is already scheduled
   */
  private boolean isAlreadyScheduled(String runnableName) {
    return scheduledFutures.containsKey(runnableName);
  }

  public static void initialize() {
    ServiceLocator.registerService(ScheduleManager.class, new ScheduleManager());
  }
}
