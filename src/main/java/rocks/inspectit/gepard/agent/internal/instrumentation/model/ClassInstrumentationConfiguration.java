package rocks.inspectit.gepard.agent.internal.instrumentation.model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Stores the instrumentation configuration for a specific class. Currently, a class can only be
 * instrumented or not. Later, we could add a list of active rules for example.
 */
public record ClassInstrumentationConfiguration(Set<InstrumentationScope> activeScopes) {

  /** The configuration representing that no instrumentation of the class if performed. */
  public static final ClassInstrumentationConfiguration NO_INSTRUMENTATION =
      new ClassInstrumentationConfiguration(Collections.emptySet());

  @Override
  public boolean equals(Object o) {
    if (o instanceof ClassInstrumentationConfiguration otherConfig)
      return activeScopes.equals(otherConfig.activeScopes);
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(activeScopes);
  }

  /**
   * Checks, if this configuration induces bytecode changes to the target class.
   *
   * @return true, if this configuration expects instrumentation
   */
  public boolean isActive() {
    return !activeScopes.isEmpty();
  }
}
