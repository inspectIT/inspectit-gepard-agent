package rocks.inspectit.gepard.agent.instrumentation;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.discovery.ClassDiscoveryService;
import rocks.inspectit.gepard.agent.internal.ServiceLocator;
import rocks.inspectit.gepard.agent.internal.schedule.ScheduleManager;

public class InstrumentationManager {
  private static final Logger log = LoggerFactory.getLogger(InstrumentationManager.class);

  InstrumentationManager() {}

  public static void initialize() {
    // Currently, not necessary
    // ServiceLocator.registerService(InstrumentationManager.class, new InstrumentationManager());

    ScheduleManager scheduleManager = ServiceLocator.getService(ScheduleManager.class);
    BatchInstrumenter batchInstrumenter = new BatchInstrumenter();
    ClassDiscoveryService discoveryService = new ClassDiscoveryService(batchInstrumenter);
    Duration discoveryInterval = Duration.ofSeconds(30);

    scheduleManager.startRunnable(discoveryService, discoveryInterval);
  }
}
