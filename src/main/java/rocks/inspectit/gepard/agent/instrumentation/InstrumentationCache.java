package rocks.inspectit.gepard.agent.instrumentation;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.*;
import rocks.inspectit.gepard.agent.instrumentation.processing.BatchInstrumenter;

public class InstrumentationCache {

  /**
   * The set of classes, which might need instrumentation updates. The {@link BatchInstrumenter}
   * works through this set in batches.
   */
  private final Cache<Class<?>, Boolean> pendingClasses;

  public InstrumentationCache() {
    this.pendingClasses = Caffeine.newBuilder().weakKeys().build();
  }

  /**
   * Puts all classes of the collections into the {@code pendingClasses}.
   *
   * @param classes the collection of classes
   */
  public void fill(Collection<Class<?>> classes) {
    for (Class<?> clazz : classes) pendingClasses.put(clazz, Boolean.TRUE);
  }

  /**
   * @return the pending classes as {@link Iterator}
   */
  public Iterator<Class<?>> getKeyIterator() {
    return pendingClasses.asMap().keySet().iterator();
  }

  /**
   * @return the current size of the cache
   */
  public long getSize() {
    return pendingClasses.estimatedSize();
  }
}
