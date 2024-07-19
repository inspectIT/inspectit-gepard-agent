package rocks.inspectit.gepard.agent.instrumentation;

import io.opentelemetry.javaagent.bootstrap.InstrumentationHolder;
import java.lang.instrument.Instrumentation;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.discovery.ClassDiscoveryListener;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;
import rocks.inspectit.gepard.agent.internal.schedule.NamedRunnable;

public class BatchInstrumenter implements ClassDiscoveryListener, ConfigurationReceivedObserver, NamedRunnable {
  private static final Logger log = LoggerFactory.getLogger(BatchInstrumenter.class);

  /** Hard coded batch size to transform classes */
  private final int BATCH_SIZE = 1000;

  /**
   * The set of classes which might need instrumentation updates. This service works through this
   * set in batches.
   */
  // TODO Evaluate set data type, inspectit ocelot stores classes in the Google cache
  private final Set<Class<?>> pendingClasses;

  private final Instrumentation instrumentation;

  private BatchInstrumenter() {
    this.pendingClasses = new HashSet<>();
    this.instrumentation = InstrumentationHolder.getInstrumentation();
  }

  public static BatchInstrumenter create() {
     BatchInstrumenter instrumenter = new BatchInstrumenter();
     instrumenter.subscribeToConfigurationReceivedEvents();
     return instrumenter;
  }

  @Override
  public void onNewClassesDiscovered(Set<Class<?>> newClasses) {
    pendingClasses.addAll(newClasses);
  }

  @Override
  public void handleConfiguration(ConfigurationReceivedEvent event) {
    Collections.addAll(pendingClasses, instrumentation.getAllLoadedClasses());
  }

  @Override
  public void run() {
    // call instrumentBatch()
  }

  @Override
  public String getName() {
    return "batch-instrumentation";
  }
}
