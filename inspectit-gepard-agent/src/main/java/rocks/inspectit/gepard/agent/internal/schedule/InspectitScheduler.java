/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.schedule;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global manager, who starts scheduled task and keeps track of them. At shutdown all scheduled
 * tasks are cancelled.
 */
public class InspectitScheduler {
  private static final Logger log = LoggerFactory.getLogger(InspectitScheduler.class);

  private static InspectitScheduler instance;

  /** executor for runnables */
  private final ScheduledExecutorService executor;

  /** set of already scheduled futures */
  private final ConcurrentMap<String, ScheduledFuture<?>> scheduledFutures;

  private InspectitScheduler() {
    this.executor = ScheduledExecutorServiceFactory.create();
    this.scheduledFutures = new ConcurrentHashMap<>();
    addShutdownHook();
  }

  public static InspectitScheduler getInstance() {
    if (Objects.isNull(instance)) instance = new InspectitScheduler();
    return instance;
  }

  /**
   * Schedules the runnable with the provided interval.
   *
   * @param runnable the runnable, which should be scheduled. Mostly a class, which implements the
   *     {@link Runnable} interface.
   * @param interval the interval, in which the runnable should be executed in milliseconds
   * @return true, if the runnable was scheduled
   */
  public boolean startRunnable(NamedRunnable runnable, Duration interval) {
    String name = runnable.getName();
    if (Objects.isNull(name) || name.isBlank())
      throw new IllegalArgumentException("Illegal runnable name");

    if (isAlreadyScheduled(name)) {
      log.info("{} is already scheduled", name);
      return false;
    }

    log.info("Starting {} with interval of {} milliseconds", name, interval.toMillis());
    ScheduledFuture<?> future =
        executor.scheduleWithFixedDelay(runnable, 0, interval.toMillis(), TimeUnit.MILLISECONDS);
    scheduledFutures.put(name, future);
    return true;
  }

  /** Add hook, so every scheduled future will be cancelled at shutdown */
  private void addShutdownHook() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () ->
                    scheduledFutures.forEach(
                        (name, future) -> {
                          log.info("Shutting down {}...", name);
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

  /**
   * Returns the number of scheduled futures.
   *
   * @return the number of scheduled futures
   */
  public int getNumberOfScheduledFutures() {
    return scheduledFutures.size();
  }
}
