package rocks.inspectit.gepard.agent.internal.schedule;

import rocks.inspectit.gepard.agent.internal.ServiceLocator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Initializes and registers a ScheduledExecutorService instance with a Service Locator.
 */
public class ScheduledExecutorInitializer {

    private ScheduledExecutorInitializer() {}

    /**
     * Initializes and registers a ScheduledExecutorService with the Service Locator.
     * Should be called once during application startup.
     */
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
        ServiceLocator.getInstance().registerService(ScheduledExecutorService.class, executorService);
    }
}
