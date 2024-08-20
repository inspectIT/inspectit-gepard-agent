package rocks.inspectit.gepard.agent.instrumentation;

import io.opentelemetry.javaagent.bootstrap.InstrumentationHolder;
import java.lang.instrument.Instrumentation;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.cache.PendingClassesCache;
import rocks.inspectit.gepard.agent.instrumentation.cache.input.ClassDiscoveryService;
import rocks.inspectit.gepard.agent.instrumentation.cache.input.ConfigurationReceiver;
import rocks.inspectit.gepard.agent.instrumentation.cache.process.BatchInstrumenter;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentationState;
import rocks.inspectit.gepard.agent.internal.schedule.InspectitScheduler;
import rocks.inspectit.gepard.agent.resolver.ConfigurationResolver;

/** Responsible component for setting up and executing instrumentation. */
public class InstrumentationManager {
  private static final Logger log = LoggerFactory.getLogger(InstrumentationManager.class);

  private final PendingClassesCache pendingClassesCache;

  private final Instrumentation instrumentation;

  private InstrumentationManager(
      PendingClassesCache pendingClassesCache, Instrumentation instrumentation) {
    this.pendingClassesCache = pendingClassesCache;
    this.instrumentation = instrumentation;
  }

  /**
   * Factory method to create an {@link InstrumentationManager}
   *
   * @return the created manager
   */
  public static InstrumentationManager create() {
    PendingClassesCache pendingClassesCache = new PendingClassesCache();
    Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();
    return new InstrumentationManager(pendingClassesCache, instrumentation);
  }

  /**
   * Starts the scheduled discovery of new classes via {@link ClassDiscoveryService}. Currently, the
   * discovery interval is fixed to 30 s.
   */
  public void startClassDiscovery() {
    InspectitScheduler scheduler = InspectitScheduler.getInstance();
    ClassDiscoveryService discoveryService =
        new ClassDiscoveryService(pendingClassesCache, instrumentation);
    Duration discoveryInterval = Duration.ofSeconds(30);
    scheduler.startRunnable(discoveryService, discoveryInterval);
  }

  /**
   * Starts the scheduled instrumentation of pending class batched via {@link BatchInstrumenter}.
   * Currently, the instrumentation interval is fixed to 500 ms.
   */
  public void startBatchInstrumentation(
      ConfigurationResolver configurationResolver, InstrumentationState instrumentationState) {
    InspectitScheduler scheduler = InspectitScheduler.getInstance();
    BatchInstrumenter batchInstrumenter =
        new BatchInstrumenter(
            pendingClassesCache, instrumentation, configurationResolver, instrumentationState);
    Duration batchInterval = Duration.ofMillis(500);
    scheduler.startRunnable(batchInstrumenter, batchInterval);
  }

  /** Creates an observer, who listens to {@link ConfigurationReceivedEvent}s. */
  public void createConfigurationReceiver() {
    log.info("Creating ConfigurationReceiver");
    ConfigurationReceiver.create(pendingClassesCache, instrumentation);
  }
}
