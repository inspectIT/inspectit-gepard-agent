package rocks.inspectit.gepard.agent.instrumentation;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.discovery.ClassDiscoveryService;
import rocks.inspectit.gepard.agent.internal.schedule.InspectitScheduler;

public class InstrumentationManager {
  private static final Logger log = LoggerFactory.getLogger(InstrumentationManager.class);

  private final ClassQueue classQueue;

  private InstrumentationManager(ClassQueue classQueue) {
    this.classQueue = classQueue;
  }

  public static InstrumentationManager create() {
    ClassQueue classQueue = new ClassQueue();
    return new InstrumentationManager(classQueue);
  }

  public void startClassDiscovery() {
    InspectitScheduler scheduler = InspectitScheduler.getInstance();
    ClassDiscoveryService discoveryService = new ClassDiscoveryService(classQueue);
    Duration discoveryInterval = Duration.ofSeconds(10);
    scheduler.startRunnable(discoveryService, discoveryInterval);
  }

  public void startBatchInstrumentation() {
    InspectitScheduler scheduler = InspectitScheduler.getInstance();
    BatchInstrumenter batchInstrumenter = BatchInstrumenter.create(classQueue);
    Duration batchInterval = Duration.ofMillis(500);
    scheduler.startRunnable(batchInstrumenter, batchInterval);
  }

  public void createConfigurationReceiver() {
    ConfigurationReceiver configurationReceiver = new ConfigurationReceiver(classQueue);
    configurationReceiver.subscribeToConfigurationReceivedEvents();
  }
}
