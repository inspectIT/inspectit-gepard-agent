package rocks.inspectit.gepard.agent.instrumentation.discovery;

import java.util.Set;

/** Classes, which should handle newly discovered classes */
public interface ClassDiscoveryListener {

  /**
   * Handles the newly discovered classes
   *
   * @param newClasses the newly discovered classes
   */
  void onNewClassesDiscovered(Set<Class<?>> newClasses);
}
