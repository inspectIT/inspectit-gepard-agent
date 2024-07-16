package rocks.inspectit.gepard.agent.internal;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ApplicationConfiguration {

  /** Single instance of the ScheduledExecutorService */
  private static ScheduledExecutorService executorService;

  private ApplicationConfiguration() {}

  /**
   * Get instance of the ScheduledThreadPool with 4 threads.
   *
   * @return the global ScheduledExecutorService
   */
  public static ScheduledExecutorService getScheduledExecutorService() {
    if (Objects.isNull(executorService)) {
      AtomicInteger threadCount = new AtomicInteger();
      executorService =
          Executors.newScheduledThreadPool(
              4,
              (runnable) -> {
                Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                thread.setDaemon(true);
                thread.setName("inspectit-thread-" + threadCount.getAndIncrement());
                return thread;
              });
    }

    return executorService;
  }
}
