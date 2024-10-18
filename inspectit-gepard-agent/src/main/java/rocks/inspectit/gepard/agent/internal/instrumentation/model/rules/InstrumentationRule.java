/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.instrumentation.model.rules;

import java.util.Objects;
import java.util.Set;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.scopes.InstrumentationScope;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

public record InstrumentationRule(
    String name, Set<InstrumentationScope> scopes, RuleTracingConfiguration tracing) {

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
