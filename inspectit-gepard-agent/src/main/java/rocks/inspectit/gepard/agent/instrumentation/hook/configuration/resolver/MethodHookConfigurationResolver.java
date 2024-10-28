/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration.resolver;

import java.util.Set;
import java.util.stream.Collectors;
import net.bytebuddy.description.method.MethodDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.exception.ConflictingConfigurationException;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.model.MethodHookConfiguration;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.InstrumentationRule;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

/**
 * Resolves a {@link ClassInstrumentationConfiguration} to a {@link MethodHookConfiguration} for a
 * specific {@link MethodDescription}. <br>
 * Every {@link ClassInstrumentationConfiguration} has a set of rules. The configurations of each
 * rule, which match for a specific {@link MethodDescription}, are resolved into the {@link
 * MethodHookConfiguration}.
 */
public class MethodHookConfigurationResolver {
  private static final Logger log = LoggerFactory.getLogger(MethodHookConfigurationResolver.class);

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
        classConfig.getActiveRules().stream()
            .filter(rule -> rule.getMethodMatcher().matches(method))
            .collect(Collectors.toSet());
    String methodName = method.getName();

    if (matchedRules.isEmpty()) {
      log.debug("No matching rules found for {}", methodName);
      return new MethodHookConfiguration(methodName);
    }

    RuleTracingConfiguration tracing = resolveTracing(matchedRules);
    return new MethodHookConfiguration(methodName, tracing);
  }

  /**
   * Resolve the tracing configuration for a specific method hook. Currently, if not all rules have
   * the same tracing configuration, there is a conflict, which we cannot resolve. <br>
   * For example, we cannot apply two different rules with {@code startSpan: true} and {@code
   * startSpan: false}.<br>
   * If there is no conflict, we use the tracing configuration of the first rule, which should be
   * the same as in all other rules.
   *
   * @param rules the rules for the current method, not empty
   * @return the tracing configuration, if there was no conflict
   */
  private RuleTracingConfiguration resolveTracing(Set<InstrumentationRule> rules) {
    InstrumentationRule firstRule = rules.iterator().next();
    for (InstrumentationRule rule : rules)
      if (!firstRule.getTracing().equals(rule.getTracing()))
        throw new ConflictingConfigurationException("Conflict in rule tracing configuration");

    return firstRule.getTracing();
  }
}
