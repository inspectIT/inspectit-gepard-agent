/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.instrumentation.model.rules;

import java.util.Objects;
import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.scopes.InstrumentationScope;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

/**
 * Internal model, which combines scopes with tracing configurations.
 *
 * @param name the name of the rule
 * @param scopes the set of active scopes of the rule
 * @param methodMatcher the matcher for all instrumented methods of the rule
 * @param tracing the configuration for tracing of the rule
 */
public record InstrumentationRule(
    String name,
    Set<InstrumentationScope> scopes,
    ElementMatcher.Junction<MethodDescription> methodMatcher,
    RuleTracingConfiguration tracing) {

  @Override
  public boolean equals(Object o) {
    if (o instanceof InstrumentationRule otherRule)
      return name.equals(otherRule.name)
          && scopes.equals(otherRule.scopes)
          && tracing.equals(otherRule.tracing);
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, scopes, tracing);
  }
}
