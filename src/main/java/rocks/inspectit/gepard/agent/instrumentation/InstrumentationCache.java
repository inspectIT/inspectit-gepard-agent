package rocks.inspectit.gepard.agent.instrumentation;



import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.*;

//TODO: Make Class Queue Thread SAFE!
public class InstrumentationCache {
  private final Cache<Class<?>, Boolean> pendingClasses;

  public InstrumentationCache() {
    this.pendingClasses = Caffeine.newBuilder()
            .build();
  }

    public void addAll(Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            pendingClasses.put(clazz, true);
        }
    }

    public Iterator<Class<?>> getKeyIterator() {
        return pendingClasses.asMap().keySet().iterator();
    }

    public long getSize() {
        return pendingClasses.estimatedSize();
    }

    public void remove(Class<?> clazz) {
        pendingClasses.invalidate(clazz);
    }
}
