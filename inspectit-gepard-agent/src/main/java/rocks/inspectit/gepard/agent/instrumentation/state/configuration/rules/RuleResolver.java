/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration.rules;

import static net.bytebuddy.matcher.ElementMatchers.none;

import java.util.*;
import java.util.stream.Collectors;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.matcher.MatcherChainBuilder;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.rules.scopes.ScopeResolver;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.InstrumentationRule;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.scopes.InstrumentationScope;
import rocks.inspectit.gepard.config.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

/** This class is used to resolve {@link RuleConfiguration}s to {@link InstrumentationRule}s. */
public class RuleResolver {

  private final ScopeResolver scopeResolver;

  public RuleResolver() {
    this.scopeResolver = new ScopeResolver();
  }

  /**
   * Find all rules of the current instrumentation configuration, which apply for the provided type.
   *
   * @param type the current type
   * @param configuration the instrumentation configuration
   * @return the set of rules, which apply for the current type
   */
  public Set<InstrumentationRule> getActiveRules(
      TypeDescription type, InstrumentationConfiguration configuration) {
    Map<String, InstrumentationScope> scopes =
        scopeResolver.resolveScopes(configuration.getScopes());

    Set<InstrumentationRule> activeRules = new HashSet<>();
    configuration
        .getRules()
        .forEach(
            (name, rule) -> {
              Set<InstrumentationScope> activeScopes = getActiveScopes(type, rule, scopes);
              if (!activeScopes.isEmpty()) {
                RuleTracingConfiguration tracing = rule.getTracing();
                InstrumentationRule activeRule =
                    new InstrumentationRule(name, activeScopes, tracing);
                activeRules.add(activeRule);
              }
            });

    return activeRules;
  }

  /**
   * Creates a matcher for each method name within a scope inside a rule. The matchers are chained
   * using 'OR'.
   *
   * @param activeRules the rules containing scopes, which hold method matchers
   * @return a matcher for the methods
   */
  public ElementMatcher.Junction<MethodDescription> getMethodMatcher(
      Set<InstrumentationRule> activeRules) {
    Set<InstrumentationScope> activeScopes =
        activeRules.stream().flatMap(rule -> rule.scopes().stream()).collect(Collectors.toSet());
    if (containsAllMethodsScope(activeScopes)) return InstrumentationScope.ALL_METHODS;

    MatcherChainBuilder<MethodDescription> matcherChainBuilder = new MatcherChainBuilder<>();
    activeScopes.forEach(scope -> matcherChainBuilder.or(scope.getMethodMatcher()));

    if (matcherChainBuilder.isEmpty()) return none();
    return matcherChainBuilder.build();
  }

  /**
   * Finds all active InstrumentationScope objects of the provided rule for the current type. First
   * we filter for enabled scopes within the rule. Then we find scope objects based on the
   * references within the rule. At the end we filter out not found scopes and filter for scopes,
   * who match for the current type.
   *
   * @param type the current type
   * @param ruleConfig the current rule configuration
   * @param scopeConfigs the instrumentation scope configurations
   * @return the set of active InstrumentationScope objects
   */
  private Set<InstrumentationScope> getActiveScopes(
      TypeDescription type,
      RuleConfiguration ruleConfig,
      Map<String, InstrumentationScope> scopeConfigs) {
    return ruleConfig.getScopes().entrySet().stream()
        .filter(Map.Entry::getValue)
        .map(entry -> scopeConfigs.get(entry.getKey()))
        .filter(scope -> Objects.nonNull(scope) && scope.getTypeMatcher().matches(type))
        .collect(Collectors.toSet());
  }

  /**
   * Checks, if the provided set of scopes contains at least one whole class scope. Then we know,
   * that all methods of the type should be instrumented and don't have to check each scope
   * individually.
   *
   * @param scopes the list of scopes to check
   * @return true, if the set of scopes contains at least one whole class scope
   */
  private boolean containsAllMethodsScope(Set<InstrumentationScope> scopes) {
    return scopes.stream()
        .anyMatch(s -> s.getMethodMatcher().equals(InstrumentationScope.ALL_METHODS));
  }
}
