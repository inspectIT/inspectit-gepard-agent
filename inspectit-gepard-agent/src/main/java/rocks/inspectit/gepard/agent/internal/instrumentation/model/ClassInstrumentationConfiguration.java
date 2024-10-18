/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.instrumentation.model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import rocks.inspectit.gepard.agent.instrumentation.state.InstrumentationState;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.InstrumentationRule;

/**
 * Stores the instrumentation configuration for a specific class. The configuration is linked to a
 * class via the cache in {@link InstrumentationState}.
 *
 * @param activeRules all active rules of the class
 * @param methodMatcher the matcher for all instrumented methods of the class
 */
public record ClassInstrumentationConfiguration(
    Set<InstrumentationRule> activeRules,
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
    return !activeRules.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ClassInstrumentationConfiguration otherConfig)
      return activeRules.equals(otherConfig.activeRules)
          && methodMatcher.equals(otherConfig.methodMatcher);
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(activeRules, methodMatcher);
  }
}
