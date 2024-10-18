/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration;

import java.util.Set;
import java.util.stream.Collectors;
import net.bytebuddy.description.method.MethodDescription;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.exception.ConflictingConfigurationException;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.InstrumentationRule;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

/**
 * Resolves a {@link ClassInstrumentationConfiguration} to a {@link MethodHookConfiguration} of a
 * specific method.
 */
public class MethodHookConfigurationResolver {

  /**
   * Resolve the configuration for a specific method hook.
   *
   * @param method the method of the current class
   * @param classConfig the instrumentation configuration of the current class
   * @return the hook configuration for the provided method
   */
  public MethodHookConfiguration resolve(
      MethodDescription method, ClassInstrumentationConfiguration classConfig) {

    Set<InstrumentationRule> matchedRules =
        classConfig.activeRules().stream()
            .filter(rule -> rule.methodMatcher().matches(method))
            .collect(Collectors.toSet());

    String methodName = method.getName();
    RuleTracingConfiguration tracing = resolveTracing(matchedRules);
    return new MethodHookConfiguration(methodName, tracing);
  }

  /**
   * Resolve the tracing configuration for a specific method hook.
   *
   * @param rules the rules for the current method
   * @return the tracing configuration
   */
  private RuleTracingConfiguration resolveTracing(Set<InstrumentationRule> rules) {
    boolean allMatch = rules.stream().allMatch(rule -> rule.tracing().getStartSpan());

    if (allMatch && !rules.isEmpty()) return rules.stream().findFirst().get().tracing();
    else throw new ConflictingConfigurationException("Conflict in rule tracing configuration");
  }
}
