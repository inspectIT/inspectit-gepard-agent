package rocks.inspectit.gepard.agent.instrumentation.filling;

import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.PendingClassesCache;
import rocks.inspectit.gepard.agent.internal.schedule.NamedRunnable;

/**
 * Constantly checks for newly loaded classes and fills them into the {@link PendingClassesCache}.
 */
public class ClassDiscoveryService implements NamedRunnable {
  private static final Logger log = LoggerFactory.getLogger(ClassDiscoveryService.class);

  private final Set<Class<?>> discoveredClasses;

  private final PendingClassesCache pendingClassesCache;

  private final Instrumentation instrumentation;

  public ClassDiscoveryService(
      PendingClassesCache pendingClassesCache, Instrumentation instrumentation) {
    this.discoveredClasses = Collections.newSetFromMap(new WeakHashMap<>());
    this.pendingClassesCache = pendingClassesCache;
    this.instrumentation = instrumentation;
  }

  @Override
  public void run() {
    log.debug("Discovering new classes...");
    try {
      discoverClasses();
    } catch (Exception e) {
      log.error("Error while discovering classes", e);
    }
  }

  /**
   * Checks all loaded classes of the Instrumentation API and adds newly discovered classes to our
   * {@code discoveredClasses}. Additionally, the {@link PendingClassesCache} will be filled with
   * the newly discovered classes.
   */
  void discoverClasses() {
    Set<Class<?>> newClasses = new HashSet<>();
    for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
      if (!discoveredClasses.contains(clazz)) {
        discoveredClasses.add(clazz);
        newClasses.add(clazz);
      }
    }
    log.debug("Discovered {} new classes", newClasses.size());
    pendingClassesCache.fill(newClasses);
  }

  @Override
  public String getName() {
    return "class-discovery";
  }
}
