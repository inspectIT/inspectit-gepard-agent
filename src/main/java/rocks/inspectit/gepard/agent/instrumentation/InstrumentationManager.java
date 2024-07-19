package rocks.inspectit.gepard.agent.instrumentation;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.discovery.ClassDiscoveryService;
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

  public void startClassDiscovery() {
    InspectitScheduler scheduler = InspectitScheduler.getInstance();
    ClassDiscoveryService discoveryService = new ClassDiscoveryService(instrumentationCache);
    Duration discoveryInterval = Duration.ofSeconds(10);
    scheduler.startRunnable(discoveryService, discoveryInterval);
  }

  public void startBatchInstrumentation() {
    InspectitScheduler scheduler = InspectitScheduler.getInstance();
    BatchInstrumenter batchInstrumenter = BatchInstrumenter.create(instrumentationCache);
    Duration batchInterval = Duration.ofMillis(500);
    scheduler.startRunnable(batchInstrumenter, batchInterval);
  }

  public void createConfigurationReceiver() {
    ConfigurationReceiver configurationReceiver = new ConfigurationReceiver(instrumentationCache);
    configurationReceiver.subscribeToConfigurationReceivedEvents();
  }
}
