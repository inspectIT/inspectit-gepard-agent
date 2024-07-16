package rocks.inspectit.gepard.agent.agentconfiguration.http;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.config.ConfigurationResolver;

public class AgentConfigurationPoller implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(AgentConfigurationPoller.class);

  private final ScheduledExecutorService executor;

  /** The scheduled task. */
  private ScheduledFuture<?> configurationPollingFuture;

  public AgentConfigurationPoller(ScheduledExecutorService executor) {
    this.executor = executor;
  }

  public void start() {
    log.info("Starting HTTP configuration polling service...");
    long pollingInterval = ConfigurationResolver.getPollingInterval();
    try {
      configurationPollingFuture =
          executor.scheduleWithFixedDelay(this, pollingInterval, pollingInterval, TimeUnit.SECONDS);

    } catch (RuntimeException e) {
      log.error("Error starting HTTP configuration polling service", e);
    }
  }

  public void run() {
    log.info("Poller is polling...");
    int status_code = HttpAgentConfigurer.fetchConfiguration();
    if (status_code == 200) {
      // Do whatever you want to do with the configuration
      log.info("Configuration fetched successfully");
    } else {
      log.error("Configuration fetching failed");
    }
  }

  public void stop() {
    configurationPollingFuture.cancel(true);
  }
}
