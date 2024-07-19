package rocks.inspectit.gepard.agent.internal.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/** Initializes and registers a ScheduledExecutorService instance with {@code ServiceLocator}. */
public class ScheduledExecutorServiceFactory {

  private ScheduledExecutorServiceFactory() {}

  /**
   * Create a new instance of an ScheduledExecutorService with a pool size of 4.
   *
   * @return new instance of ScheduledExecutorService
   */
  public static ScheduledExecutorService create() {
    AtomicInteger threadCount = new AtomicInteger();
    return Executors.newScheduledThreadPool(
        4,
        (runnable) -> {
          Thread thread = Executors.defaultThreadFactory().newThread(runnable);
          thread.setDaemon(true);
          thread.setName("inspectit-thread-" + threadCount.getAndIncrement());
          return thread;
        });
  }
}
