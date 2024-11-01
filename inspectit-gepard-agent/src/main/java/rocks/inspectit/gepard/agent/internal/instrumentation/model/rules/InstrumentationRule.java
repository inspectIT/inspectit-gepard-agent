/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.instrumentation.model.rules;

import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.scopes.InstrumentationScope;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

/** Internal model, which combines scopes with tracing configurations. */
public final class InstrumentationRule {

  private final String name;

  private final Set<InstrumentationScope> scopes;

  private final ElementMatcher.Junction<MethodDescription> methodMatcher;

  private final RuleTracingConfiguration tracing;

  public InstrumentationRule(
      @Nonnull String name,
      @Nonnull Set<InstrumentationScope> scopes,
      @Nonnull ElementMatcher.Junction<MethodDescription> methodMatcher,
      @Nonnull RuleTracingConfiguration tracing) {
    this.name = name;
    this.scopes = scopes;
    this.methodMatcher = methodMatcher;
    this.tracing = tracing;
  }

  /**
   * @return the name of the rule
   */
  public String getName() {
    return name;
  }

  /**
   * @return the set of active scopes of the rule
   */
  public Set<InstrumentationScope> getScopes() {
    return scopes;
  }

  /**
   * @return the matcher for all instrumented methods of the rule
   */
  public ElementMatcher.Junction<MethodDescription> getMethodMatcher() {
    return methodMatcher;
  }

  /**
   * @return the configuration for tracing of the rule
   */
  public RuleTracingConfiguration getTracing() {
    return tracing;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof InstrumentationRule otherRule)
      return name.equals(otherRule.name)
          && scopes.equals(otherRule.scopes)
          && methodMatcher.equals(otherRule.methodMatcher)
          && tracing.equals(otherRule.tracing);
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, scopes, methodMatcher, tracing);
  }

  @Override
  public String toString() {
    return "InstrumentationRule {"
        + "name = '"
        + name
        + '\''
        + ", scopes = "
        + scopes
        + ", methodMatcher = "
        + methodMatcher
        + ", tracing = "
        + tracing
        + '}';
  }
}
