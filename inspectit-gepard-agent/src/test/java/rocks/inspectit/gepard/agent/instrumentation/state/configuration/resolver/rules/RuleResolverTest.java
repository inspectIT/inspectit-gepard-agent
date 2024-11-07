/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration.resolver.rules;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.none;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.resolver.rules.scopes.ScopeResolver;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.InstrumentationRule;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.scopes.InstrumentationScope;
import rocks.inspectit.gepard.config.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

@ExtendWith(MockitoExtension.class)
class RuleResolverTest {

  @Mock private ScopeResolver scopeResolver;

  @InjectMocks private RuleResolver ruleResolver;

  private static final TypeDescription TEST_TYPE =
      TypeDescription.ForLoadedType.of(RuleResolverTest.class);

  @Nested
  class ActiveRules {

    @Mock private InstrumentationConfiguration configuration;

    @Mock private ElementMatcher.Junction<TypeDescription> typeMatcher;

    @Mock private ElementMatcher.Junction<MethodDescription> methodMatcher;

    @Mock private InstrumentationScope scope1;

    @Mock private InstrumentationScope scope2;

    @Mock private RuleConfiguration ruleConfig1;

    @Mock private RuleConfiguration ruleConfig2;

    @Test
    void shouldCreateRuleWithOneScopeAndTracingEnabled() {
      Map<String, RuleConfiguration> rules = Map.of("r_rule", ruleConfig1);
      Map<String, InstrumentationScope> activeScopes =
          Map.of("s_scope1", scope1, "s_scope2", scope2);
      RuleTracingConfiguration tracing = new RuleTracingConfiguration(true);

      // set up scope
      when(scope1.getTypeMatcher()).thenReturn(typeMatcher);
      when(scope1.getMethodMatcher()).thenReturn(methodMatcher);
      when(typeMatcher.matches(TEST_TYPE)).thenReturn(true);
      // set up rule
      when(ruleConfig1.isEnabled()).thenReturn(true);
      when(ruleConfig1.getScopes()).thenReturn(Map.of("s_scope1", true));
      when(ruleConfig1.getTracing()).thenReturn(tracing);
      when(configuration.getRules()).thenReturn(rules);
      // set up scope resolving
      when(scopeResolver.resolveScopes(any())).thenReturn(activeScopes);

      Set<InstrumentationRule> activeRules = ruleResolver.getActiveRules(TEST_TYPE, configuration);
      Optional<InstrumentationRule> activeRule = activeRules.stream().findFirst();

      assertEquals(1, activeRules.size());
      assertTrue(activeRule.isPresent());
      assertEquals(1, activeRule.get().getScopes().size());
      assertTrue(activeRule.get().getScopes().contains(scope1));
      assertTrue(activeRule.get().getTracing().isStartSpan());
      assertEquals(methodMatcher, activeRule.get().getMethodMatcher());
    }

    @Test
    void shouldCreateRuleWithTwoScopesAndTracingDisabled() {
      Map<String, RuleConfiguration> rules = Map.of("r_rule", ruleConfig1);
      Map<String, InstrumentationScope> activeScopes =
          Map.of("s_scope1", scope1, "s_scope2", scope2);
      RuleTracingConfiguration tracing = new RuleTracingConfiguration(false);

      // set up scopes
      when(scope1.getTypeMatcher()).thenReturn(typeMatcher);
      when(scope1.getMethodMatcher()).thenReturn(methodMatcher);
      when(scope2.getTypeMatcher()).thenReturn(typeMatcher);
      when(scope2.getMethodMatcher()).thenReturn(methodMatcher);
      when(typeMatcher.matches(TEST_TYPE)).thenReturn(true);
      // set up rule
      when(ruleConfig1.isEnabled()).thenReturn(true);
      when(ruleConfig1.getScopes()).thenReturn(Map.of("s_scope1", true, "s_scope2", true));
      when(ruleConfig1.getTracing()).thenReturn(tracing);
      when(configuration.getRules()).thenReturn(rules);
      // set up scope resolving
      when(scopeResolver.resolveScopes(any())).thenReturn(activeScopes);

      Set<InstrumentationRule> activeRules = ruleResolver.getActiveRules(TEST_TYPE, configuration);
      Optional<InstrumentationRule> activeRule = activeRules.stream().findFirst();

      assertEquals(1, activeRules.size());
      assertTrue(activeRule.isPresent());
      assertEquals(2, activeRule.get().getScopes().size());
      assertTrue(activeRule.get().getScopes().contains(scope1));
      assertTrue(activeRule.get().getScopes().contains(scope2));
      assertFalse(activeRule.get().getTracing().isStartSpan());
      verify(methodMatcher).or(methodMatcher); // method matchers of scopes were combined
    }

    @Test
    void shouldNotCreateRuleWhenDisabled() {
      Map<String, RuleConfiguration> rules = Map.of("r_rule", ruleConfig1);

      // set up rule
      when(ruleConfig1.isEnabled()).thenReturn(false);
      when(configuration.getRules()).thenReturn(rules);

      Set<InstrumentationRule> activeRules = ruleResolver.getActiveRules(TEST_TYPE, configuration);
      Optional<InstrumentationRule> activeRule = activeRules.stream().findFirst();

      assertEquals(0, activeRules.size());
      assertTrue(activeRule.isEmpty());
    }

    @Test
    void shouldNotCreateRuleWhenNoScopesExist() {
      Map<String, RuleConfiguration> rules = Map.of("r_rule", ruleConfig1);
      Map<String, InstrumentationScope> activeScopes = Map.of();

      // set up rule
      when(ruleConfig1.isEnabled()).thenReturn(true);
      when(ruleConfig1.getScopes()).thenReturn(Map.of("s_scope1", true, "s_scope2", true));
      when(configuration.getRules()).thenReturn(rules);
      // set up scope resolving
      when(scopeResolver.resolveScopes(any())).thenReturn(activeScopes);

      Set<InstrumentationRule> activeRules = ruleResolver.getActiveRules(TEST_TYPE, configuration);
      Optional<InstrumentationRule> activeRule = activeRules.stream().findFirst();

      assertEquals(0, activeRules.size());
      assertTrue(activeRule.isEmpty());
    }

    @Test
    void shouldCreateTwoRulesWithDifferentScopesAndDifferentTracing() {
      Map<String, RuleConfiguration> rules = Map.of("r_rule1", ruleConfig1, "r_rule2", ruleConfig2);
      Map<String, InstrumentationScope> activeScopes =
          Map.of("s_scope1", scope1, "s_scope2", scope2);
      RuleTracingConfiguration tracing1 = new RuleTracingConfiguration(true);
      RuleTracingConfiguration tracing2 = RuleTracingConfiguration.NO_TRACING;

      // set up scopes
      when(scope1.getTypeMatcher()).thenReturn(typeMatcher);
      when(scope1.getMethodMatcher()).thenReturn(methodMatcher);
      when(scope2.getTypeMatcher()).thenReturn(typeMatcher);
      when(scope2.getMethodMatcher()).thenReturn(methodMatcher);
      when(typeMatcher.matches(TEST_TYPE)).thenReturn(true);
      // set up rules
      when(ruleConfig1.isEnabled()).thenReturn(true);
      when(ruleConfig1.getScopes()).thenReturn(Map.of("s_scope1", true));
      when(ruleConfig1.getTracing()).thenReturn(tracing1);
      when(ruleConfig2.isEnabled()).thenReturn(true);
      when(ruleConfig2.getScopes()).thenReturn(Map.of("s_scope2", true));
      when(ruleConfig2.getTracing()).thenReturn(tracing2);
      when(configuration.getRules()).thenReturn(rules);
      // set up scope resolving
      when(scopeResolver.resolveScopes(any())).thenReturn(activeScopes);

      Set<InstrumentationRule> activeRules = ruleResolver.getActiveRules(TEST_TYPE, configuration);
      Optional<InstrumentationRule> activeRule1 =
          activeRules.stream().filter(rule -> rule.getName().equals("r_rule1")).findFirst();
      Optional<InstrumentationRule> activeRule2 =
          activeRules.stream().filter(rule -> rule.getName().equals("r_rule2")).findFirst();

      assertEquals(2, activeRules.size());
      assertTrue(activeRule1.isPresent());
      assertEquals(1, activeRule1.get().getScopes().size());
      assertTrue(activeRule1.get().getScopes().contains(scope1));
      assertTrue(activeRule1.get().getTracing().isStartSpan());
      assertEquals(methodMatcher, activeRule1.get().getMethodMatcher());
      assertTrue(activeRule2.isPresent());
      assertEquals(1, activeRule2.get().getScopes().size());
      assertTrue(activeRule2.get().getScopes().contains(scope2));
      assertFalse(activeRule2.get().getTracing().isStartSpan());
      assertEquals(methodMatcher, activeRule2.get().getMethodMatcher());
    }
  }

  @Nested
  class MethodMatcher {

    @Mock private InstrumentationRule rule1;

    @Mock private InstrumentationRule rule2;

    @Test
    void shouldCombineMethodMatcherOfRules() {
      ElementMatcher.Junction<MethodDescription> methodMatcher = isMethod();

      when(rule1.getMethodMatcher()).thenReturn(methodMatcher);
      when(rule2.getMethodMatcher()).thenReturn(methodMatcher);
      Set<InstrumentationRule> activeRules = Set.of(rule1, rule2);

      ElementMatcher.Junction<MethodDescription> resultMatcher =
          ruleResolver.getClassMethodMatcher(activeRules);

      assertEquals(methodMatcher.or(methodMatcher), resultMatcher);
    }

    @Test
    void shouldReturnNoneMatcherWhenRulesHaveNoMatcher() {
      when(rule1.getMethodMatcher()).thenReturn(null);
      when(rule2.getMethodMatcher()).thenReturn(null);
      Set<InstrumentationRule> activeRules = Set.of(rule1, rule2);

      ElementMatcher.Junction<MethodDescription> resultMatcher =
          ruleResolver.getClassMethodMatcher(activeRules);

      assertEquals(none(), resultMatcher);
    }
  }
}
