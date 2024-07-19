package rocks.inspectit.gepard.agent.instrumentation;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.discovery.ClassDiscoveryService;
import rocks.inspectit.gepard.agent.internal.schedule.InspectitScheduler;

public class InstrumentationManager {
  private static final Logger log = LoggerFactory.getLogger(InstrumentationManager.class);

  private InstrumentationManager() {}

  public static InstrumentationManager create() {
    return new InstrumentationManager();
  }

  public void startClassDiscovery() {
    InspectitScheduler scheduler = InspectitScheduler.getInstance();
    BatchInstrumenter batchInstrumenter = new BatchInstrumenter();
    ClassDiscoveryService discoveryService = new ClassDiscoveryService(batchInstrumenter);
    Duration discoveryInterval = Duration.ofSeconds(30);

    scheduler.startRunnable(discoveryService, discoveryInterval);
  }
}
