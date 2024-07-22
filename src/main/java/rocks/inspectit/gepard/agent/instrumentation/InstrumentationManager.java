package rocks.inspectit.gepard.agent.instrumentation;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.filling.ClassDiscoveryService;
import rocks.inspectit.gepard.agent.instrumentation.filling.ConfigurationReceiver;
import rocks.inspectit.gepard.agent.instrumentation.processing.BatchInstrumenter;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.schedule.InspectitScheduler;

/** Responsible component for setting up and executing instrumentation. */
public class InstrumentationManager {
  private static final Logger log = LoggerFactory.getLogger(InstrumentationManager.class);

  private final PendingClassesCache pendingClassesCache;

  private InstrumentationManager(PendingClassesCache pendingClassesCache) {
    this.pendingClassesCache = pendingClassesCache;
  }

  /**
   * Factory method to create an {@link InstrumentationManager}
   *
   * @return the created manager
   */
  public static InstrumentationManager create() {
    PendingClassesCache pendingClassesCache = new PendingClassesCache();
    return new InstrumentationManager(pendingClassesCache);
  }

  /**
   * Starts the scheduled discovery of new classes via {@link ClassDiscoveryService}. Currently, the
   * discovery interval is fixed to 30 s.
   */
  public void startClassDiscovery() {
    InspectitScheduler scheduler = InspectitScheduler.getInstance();
    ClassDiscoveryService discoveryService = new ClassDiscoveryService(pendingClassesCache);
    Duration discoveryInterval = Duration.ofSeconds(30);
    scheduler.startRunnable(discoveryService, discoveryInterval);
  }

  /**
   * Starts the scheduled instrumentation of pending class batched via {@link BatchInstrumenter}.
   * Currently, the instrumentation interval is fixed to 500 ms.
   */
  public void startBatchInstrumentation() {
    InspectitScheduler scheduler = InspectitScheduler.getInstance();
    BatchInstrumenter batchInstrumenter = new BatchInstrumenter(pendingClassesCache);
    Duration batchInterval = Duration.ofMillis(500);
    scheduler.startRunnable(batchInstrumenter, batchInterval);
  }

  /** Creates an observer, who listens to {@link ConfigurationReceivedEvent}s. */
  public void createConfigurationReceiver() {
    log.info("Creating ConfigurationReceiver...");
    ConfigurationReceiver configurationReceiver = new ConfigurationReceiver(pendingClassesCache);
    configurationReceiver.subscribeToConfigurationReceivedEvents();
  }
}
