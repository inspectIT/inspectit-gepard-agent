/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.*;
import rocks.inspectit.gepard.agent.instrumentation.cache.process.BatchInstrumenter;

/** Stores and offers pending classes, which might need instrumentation updated. */
public class PendingClassesCache {

  /**
   * The set of classes, which might need instrumentation updates. The {@link BatchInstrumenter}
   * works through this set in batches.
   */
  private final Cache<Class<?>, Boolean> pendingClasses;

  public PendingClassesCache() {
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
