package rocks.inspectit.gepard.agent.internal.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import rocks.inspectit.gepard.agent.internal.ServiceLocator;

/** Initializes and registers a ScheduledExecutorService instance with {@code ServiceLocator}. */
public class ScheduledExecutorServiceInitializer {

  ScheduledExecutorServiceInitializer() {}

  public static void initialize() {
    AtomicInteger threadCount = new AtomicInteger();
    ScheduledExecutorService executorService =
        Executors.newScheduledThreadPool(
            4,
            (runnable) -> {
              Thread thread = Executors.defaultThreadFactory().newThread(runnable);
              thread.setDaemon(true);
              thread.setName("inspectit-thread-" + threadCount.getAndIncrement());
              return thread;
            });
    ServiceLocator.registerService(ScheduledExecutorService.class, executorService);
  }
}
