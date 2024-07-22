package rocks.inspectit.gepard.agent.instrumentation;

import io.opentelemetry.javaagent.bootstrap.InstrumentationHolder;
import java.lang.instrument.Instrumentation;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.discovery.ClassDiscoveryListener;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationResolver;
import rocks.inspectit.gepard.agent.internal.schedule.NamedRunnable;

public class BatchInstrumenter implements ClassDiscoveryListener, NamedRunnable {
  private static final Logger log = LoggerFactory.getLogger(BatchInstrumenter.class);

  /** Hard coded batch size to transform classes */
  private final int BATCH_SIZE = 1000;

  /**
   * The set of classes which might need instrumentation updates. This service works through this
   * set in batches.
   */
  private final Instrumentation instrumentation;

  private final InstrumentationCache instrumentationCache;

  private BatchInstrumenter(InstrumentationCache instrumentationCache) {
    this.instrumentationCache = instrumentationCache;
    this.instrumentation = InstrumentationHolder.getInstrumentation();
  }

  public static BatchInstrumenter create(InstrumentationCache instrumentationCache) {
    BatchInstrumenter instrumenter = new BatchInstrumenter(instrumentationCache);
    return instrumenter;
  }

  @Override
  public void onNewClassesDiscovered(Set<Class<?>> newClasses) {
    instrumentationCache.addAll(newClasses);
  }

  @Override
  public void run() {
    // log.info("Retransforming classes...");
    try{
      Set<Class<?>> batch = getBatch(BATCH_SIZE);
      Iterator<Class<?>> batchIterator = batch.iterator();
      retransformBatch(batchIterator);
    } catch(Throwable e) {
        log.error("Error while retransforming classes", e);
    }

  }

  private void retransformBatch(Iterator<Class<?>> batchIterator) {
    while (batchIterator.hasNext()) {
      Class<?> clazz = batchIterator.next();
      batchIterator.remove();
      try {
        log.info("Retransforming class {}", clazz.getName());
        instrumentation.retransformClasses(clazz);
      } catch (Throwable e) {
        log.error("Error while retransforming class {}", clazz.getName(), e);
      }
    }
  }

  @Override
  public String getName() {
    return "batch-instrumentation";
  }

  public Set<Class<?>> getBatch(int batchSize) {
    Set<Class<?>> batch = new HashSet<>();;
    int checkedClassesCount = 0;
    Iterator<Class<?>> queueIterator = instrumentationCache.getKeyIterator();

    while (queueIterator.hasNext()) {
      Class<?> clazz = queueIterator.next();
      queueIterator.remove();
      instrumentationCache.remove(clazz);
      checkedClassesCount++;
      if (ConfigurationResolver.shouldRetransform(clazz)) {
        batch.add(clazz);
      }

      if (checkedClassesCount >= batchSize) {
        break;
      }
    }



      log.debug(
          "Checked configuration of {} classes, {} classes left to check",
          checkedClassesCount,
          instrumentationCache.getSize());

    return batch;
  }
}
