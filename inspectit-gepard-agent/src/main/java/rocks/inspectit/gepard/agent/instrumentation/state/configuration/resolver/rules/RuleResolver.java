/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration.resolver.rules;

import static net.bytebuddy.matcher.ElementMatchers.none;

import java.util.*;
import java.util.stream.Collectors;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.matcher.MatcherChainBuilder;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.resolver.rules.scopes.ScopeResolver;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.InstrumentationRule;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.scopes.InstrumentationScope;
import rocks.inspectit.gepard.config.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

/** This class is used to resolve {@link RuleConfiguration}s to {@link InstrumentationRule}s. */
public class RuleResolver {
  private static final Logger log = LoggerFactory.getLogger(RuleResolver.class);

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
              if (rule.isEnabled()) {
                InstrumentationRule activeRule = getActiveRule(name, rule, type, scopes);
                if (Objects.nonNull(activeRule)) {
                  activeRules.add(activeRule);
                  log.debug("Added rule {} to type {}", name, type.getName());
                }
              }
            });

    return activeRules;
  }

  /**
   * Creates a matcher for all method names within a class. The matchers are chained using 'OR'.
   *
   * @param activeRules the rules, which hold method matchers
   * @return a matcher for the methods of the class
   */
  public ElementMatcher.Junction<MethodDescription> getClassMethodMatcher(
      Set<InstrumentationRule> activeRules) {
    MatcherChainBuilder<MethodDescription> matcherChainBuilder = new MatcherChainBuilder<>();
    activeRules.forEach(rule -> matcherChainBuilder.or(rule.methodMatcher()));

    if (matcherChainBuilder.isEmpty()) return none();
    return matcherChainBuilder.build();
  }

  /**
   * Creates a rule with only the relevant scopes and it's configuration.
   *
   * @param name the name of the rule
   * @param ruleConfig the configuration of the rule
   * @param type the type for which the rule applies
   * @param scopes the configured scopes
   * @return the created active rule or null, if no active scopes were found
   */
  private InstrumentationRule getActiveRule(
      String name,
      RuleConfiguration ruleConfig,
      TypeDescription type,
      Map<String, InstrumentationScope> scopes) {
    Set<InstrumentationScope> activeScopes = getActiveScopes(type, ruleConfig, scopes);
    if (activeScopes.isEmpty()) return null;

    ElementMatcher.Junction<MethodDescription> methodMatcher = getRuleMethodMatcher(activeScopes);
    RuleTracingConfiguration tracing = ruleConfig.getTracing();
    return new InstrumentationRule(name, activeScopes, methodMatcher, tracing);
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
   * Creates a matcher for all method names within a rule. The matchers are chained using 'OR'.
   *
   * @param scopes the scopes, which hold method matchers
   * @return a matcher for the methods of the rule
   */
  private ElementMatcher.Junction<MethodDescription> getRuleMethodMatcher(
      Set<InstrumentationScope> scopes) {
    MatcherChainBuilder<MethodDescription> matcherChainBuilder = new MatcherChainBuilder<>();
    scopes.forEach(scope -> matcherChainBuilder.or(scope.getMethodMatcher()));

    if (matcherChainBuilder.isEmpty()) return none();
    return matcherChainBuilder.build();
  }
}
