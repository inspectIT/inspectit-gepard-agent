/* (C) 2024 */
package rocks.inspectit.gepard.agent.testutils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.scopes.ScopeConfiguration;

public class InspectitConfigurationTestUtil {

  /**
   * @param scopes a list of scopes to be added to the configuration
   * @return the inspectit configuration
   */
  public static InspectitConfiguration createConfiguration(Map<String, ScopeConfiguration> scopes) {
    RuleConfiguration rule = createRule(scopes);
    Map<String, RuleConfiguration> rules = Map.of("r_rule", rule);

    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(scopes, rules);
    return new InspectitConfiguration(instrumentationConfiguration);
  }

  /**
   * @param enabled the status of the scope
   * @param name the fqn of a class
   * @param methodNames the method names to be instrumented
   * @return the scope with the provided class as fqn and methods
   */
  public static ScopeConfiguration createScope(
      boolean enabled, String name, List<String> methodNames) {
    return new ScopeConfiguration(enabled, name, methodNames);
  }

  /**
   * @param enabled the status of the scope
   * @param name the fqn of a class
   * @return the scope with the provided class as fqn
   */
  public static ScopeConfiguration createScope(boolean enabled, String name) {
    return createScope(enabled, name, Collections.emptyList());
  }

  /**
   * @return the inspectIT configuration as object
   */
  public static InspectitConfiguration expectedConfiguration() {
    ScopeConfiguration scope =
        new ScopeConfiguration(true, "com.example.Application", Collections.emptyList());
    RuleTracingConfiguration tracing = new RuleTracingConfiguration(true);
    RuleConfiguration rule = new RuleConfiguration(true, Map.of("s_scope", true), tracing);
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(Map.of("s_scope", scope), Map.of("r_rule", rule));

    return new InspectitConfiguration(instrumentationConfiguration);
  }

  /**
   * @return the inspectIT configuration as string
   */
  public static String expectedString() {
    return "{\"instrumentation\":{\"scopes\":{\"s_scope\":{\"enabled\":true,\"fqn\":\"com.example.Application\",\"methods\":[]}},\"rules\":{\"r_rule\":{\"enabled\":true,\"scopes\":{\"s_scope\":true},\"tracing\":{\"startSpan\":true}}}}}";
  }

  /**
   * Creates a rule, which includes all provided scopes and enables tracing
   *
   * @param scopes the scopes for the rule
   * @return the rule with scopes and enabled tracing
   */
  private static RuleConfiguration createRule(Map<String, ScopeConfiguration> scopes) {
    Map<String, Boolean> enabledScopes = new HashMap<>();
    scopes.forEach((name, scope) -> enabledScopes.put(name, true));
    RuleTracingConfiguration tracing = new RuleTracingConfiguration(true);

    return new RuleConfiguration(true, enabledScopes, tracing);
  }
}
