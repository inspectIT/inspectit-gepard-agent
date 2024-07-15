package rocks.inspectit.gepard.agent.config;



import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ApplicationConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ApplicationConfiguration.class);

    public static ScheduledExecutorService getScheduledExecutorService() {
        AtomicInteger threadCount = new AtomicInteger();
        ScheduledExecutorService activeExecutor = Executors.newScheduledThreadPool(5, (runnable) -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            thread.setName("inspectit-thread-" + threadCount.getAndIncrement());
            return thread;
        });
        return activeExecutor;
    }
}
