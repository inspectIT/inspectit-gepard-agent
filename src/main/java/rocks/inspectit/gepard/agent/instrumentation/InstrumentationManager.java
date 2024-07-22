package rocks.inspectit.gepard.agent.instrumentation;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.filling.ClassDiscoveryService;
import rocks.inspectit.gepard.agent.instrumentation.filling.ConfigurationReceiver;
import rocks.inspectit.gepard.agent.instrumentation.processing.BatchInstrumenter;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.schedule.InspectitScheduler;

public class InstrumentationManager {
  private static final Logger log = LoggerFactory.getLogger(InstrumentationManager.class);

  private final InstrumentationCache instrumentationCache;

  private InstrumentationManager(InstrumentationCache instrumentationCache) {
    this.instrumentationCache = instrumentationCache;
  }

  public static InstrumentationManager create() {
    InstrumentationCache instrumentationCache = new InstrumentationCache();
    return new InstrumentationManager(instrumentationCache);
  }

  /**
   * Starts the scheduled discovery of new classes via {@link ClassDiscoveryService}. Currently, the
   * discovery interval is fixed to 10 s.
   */
  public void startClassDiscovery() {
    InspectitScheduler scheduler = InspectitScheduler.getInstance();
    ClassDiscoveryService discoveryService = new ClassDiscoveryService(instrumentationCache);
    Duration discoveryInterval = Duration.ofSeconds(10);
    scheduler.startRunnable(discoveryService, discoveryInterval);
  }

  /**
   * Starts the scheduled instrumentation of pending class batched via {@link BatchInstrumenter}.
   * Currently, the instrumentation interval is fixed to 500 ms.
   */
  public void startBatchInstrumentation() {
    InspectitScheduler scheduler = InspectitScheduler.getInstance();
    BatchInstrumenter batchInstrumenter = BatchInstrumenter.create(instrumentationCache);
    Duration batchInterval = Duration.ofMillis(500);
    scheduler.startRunnable(batchInstrumenter, batchInterval);
  }

  /** Creates an observer, who listens to {@link ConfigurationReceivedEvent}s. */
  public void createConfigurationReceiver() {
    log.info("Creating ConfigurationReceiver...");
    ConfigurationReceiver configurationReceiver = new ConfigurationReceiver(instrumentationCache);
    configurationReceiver.subscribeToConfigurationReceivedEvents();
  }
}
