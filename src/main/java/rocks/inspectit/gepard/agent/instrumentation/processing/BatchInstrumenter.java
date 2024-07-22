package rocks.inspectit.gepard.agent.instrumentation.processing;

import com.google.common.annotations.VisibleForTesting;
import io.opentelemetry.javaagent.bootstrap.InstrumentationHolder;
import java.lang.instrument.Instrumentation;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.PendingClassesCache;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationResolver;
import rocks.inspectit.gepard.agent.internal.schedule.NamedRunnable;

public class BatchInstrumenter implements NamedRunnable {
  private static final Logger log = LoggerFactory.getLogger(BatchInstrumenter.class);

  /** Hard coded batch size to transform classes */
  private static final int BATCH_SIZE = 1000;

  /**
   * The set of classes which might need instrumentation updates. This service works through this
   * set in batches.
   */
  private final Instrumentation instrumentation;

  private final PendingClassesCache pendingClassesCache;

  public BatchInstrumenter(PendingClassesCache pendingClassesCache) {
    this.pendingClassesCache = pendingClassesCache;
    this.instrumentation = InstrumentationHolder.getInstrumentation();
  }

  @Override
  public void run() {
    log.debug("Instrumenting next batch...");
    try {
      Set<Class<?>> nextBatch = getNextBatch(BATCH_SIZE);
      retransformBatch(nextBatch.iterator());
    } catch (Throwable e) {
      log.error("Error while retransforming classes", e);
    }
  }

  /**
   * Retrieves the next batch out of {@link PendingClassesCache}.
   * Currently, the batch size is fixed to 1000.
   *
   * @param batchSize the size of the next batch
   *
   * @return the batch of retrieved pending classes
   */
  @VisibleForTesting
  Set<Class<?>> getNextBatch(int batchSize) {
    Set<Class<?>> batch = new HashSet<>();
    int checkedClassesCount = 0;
    Iterator<Class<?>> queueIterator = pendingClassesCache.getKeyIterator();

    while (queueIterator.hasNext()) {
      Class<?> clazz = queueIterator.next();
      queueIterator.remove();
      checkedClassesCount++;

      if (ConfigurationResolver.shouldRetransform(clazz)) batch.add(clazz);

      if (checkedClassesCount >= batchSize) break;
    }

    log.debug(
        "Checked configuration of {} classes, {} classes left to check",
        checkedClassesCount,
        pendingClassesCache.getSize());

    return batch;
  }

  /**
   * Retransforms all classes of the provided iterator.
   *
   * @param batchIterator the batch of classes as iterator.
   */
  @VisibleForTesting
  void retransformBatch(Iterator<Class<?>> batchIterator) {
    while (batchIterator.hasNext()) {
      Class<?> clazz = batchIterator.next();
      batchIterator.remove();

      log.debug("Retransforming class: {}", clazz.getName());
      try {
        instrumentation.retransformClasses(clazz);
      } catch (Throwable e) {
        log.error("Error while retransforming class: {}", clazz.getName(), e);
      }
    }
  }

  @Override
  public String getName() {
    return "batch-instrumentation";
  }
}
