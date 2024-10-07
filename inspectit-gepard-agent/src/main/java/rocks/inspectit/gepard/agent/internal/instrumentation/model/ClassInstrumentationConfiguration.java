/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.instrumentation.model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Stores the instrumentation configuration for a specific class. Currently, a class can only be
 * instrumented or not.
 */
public record ClassInstrumentationConfiguration(
    Set<InstrumentationScope> activeScopes,
    ElementMatcher.Junction<MethodDescription> methodMatcher) {

  /** The configuration representing that no instrumentation of the class if performed. */
  public static final ClassInstrumentationConfiguration NO_INSTRUMENTATION =
      new ClassInstrumentationConfiguration(Collections.emptySet(), ElementMatchers.none());

  /**
   * Checks, if this configuration induces bytecode changes to the target class.
   *
   * @return true, if this configuration expects instrumentation
   */
  public boolean isActive() {
    return !activeScopes.isEmpty();
  }

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
}
