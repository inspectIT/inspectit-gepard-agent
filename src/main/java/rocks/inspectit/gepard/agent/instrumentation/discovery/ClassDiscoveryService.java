package rocks.inspectit.gepard.agent.instrumentation.discovery;

import io.opentelemetry.javaagent.bootstrap.InstrumentationHolder;
import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.InstrumentationCache;
import rocks.inspectit.gepard.agent.internal.schedule.NamedRunnable;

public class ClassDiscoveryService implements NamedRunnable {
  private static final Logger log = LoggerFactory.getLogger(ClassDiscoveryService.class);

  private final Set<Class<?>> discoveredClasses = Collections.newSetFromMap(new WeakHashMap<>());

  private final Instrumentation instrumentation;

  private final InstrumentationCache instrumentationCache;

  public ClassDiscoveryService(InstrumentationCache instrumentationCache) {
    this.instrumentation = InstrumentationHolder.getInstrumentation();
    this.instrumentationCache = instrumentationCache;
  }

  @Override
  public void run() {
    log.info("Discovering new classes...");
    try {
      discoverClasses();
    } catch (Throwable e) {
      log.error("Error while discovering classes", e);
    }
  }

  /**
   * Checks all loaded classes of the Instrumentation API and adds newly discovered classes to our
   * {@code discoveredClasses}. Additionally, the {@code listener} will be informed about the newly
   * discovered classes.
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
    instrumentationCache.addAll(newClasses);
  }

  @Override
  public String getName() {
    return "class-discovery";
  }
}
