package rocks.inspectit.gepard.agent.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ApplicationConfiguration {

  // Creating one ScheduledThreadPool with 5 threads.
  // This will hold runnable commands like the configuration polling and be injected via DI.
  public static ScheduledExecutorService getScheduledThreadPool() {
    AtomicInteger threadCount = new AtomicInteger();
    return Executors.newScheduledThreadPool(
        5,
        (runnable) -> {
          Thread thread = Executors.defaultThreadFactory().newThread(runnable);
          thread.setDaemon(true);
          thread.setName("inspectit-thread-" + threadCount.getAndIncrement());
          return thread;
        });
  }
}
