/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration;

import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.rules.RuleResolver;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.InstrumentationRule;
import rocks.inspectit.gepard.config.model.instrumentation.InstrumentationConfiguration;

/**
 * Resolves the {@link InstrumentationConfiguration} and determine whether a class's byte code needs
 * updates.
 */
public class ConfigurationResolver {

  private final InspectitConfigurationHolder holder;

  private final RuleResolver ruleResolver;

  private ConfigurationResolver(InspectitConfigurationHolder holder) {
    this.holder = holder;
    this.ruleResolver = new RuleResolver();
  }

  /**
   * Factory method to create an {@link ConfigurationResolver}
   *
   * @return the created resolver
   */
  public static ConfigurationResolver create(InspectitConfigurationHolder holder) {
    return new ConfigurationResolver(holder);
  }

  /**
   * Gets the current instrumentation configuration for the specified type.
   *
   * @param type the instrumented type
   * @return The active configuration or {@link
   *     ClassInstrumentationConfiguration#NO_INSTRUMENTATION}
   */
  public ClassInstrumentationConfiguration getClassInstrumentationConfiguration(
      InstrumentedType type) {
    TypeDescription typeDescription = type.getTypeDescription();
    if (shouldIgnore(typeDescription)) return ClassInstrumentationConfiguration.NO_INSTRUMENTATION;

    InstrumentationConfiguration currentConfig = holder.getConfiguration().getInstrumentation();
    Set<InstrumentationRule> activeRules =
        ruleResolver.getActiveRules(typeDescription, currentConfig);
    if (activeRules.isEmpty()) return ClassInstrumentationConfiguration.NO_INSTRUMENTATION;

    ElementMatcher.Junction<MethodDescription> methodMatcher =
        ruleResolver.getClassMethodMatcher(activeRules);
    return new ClassInstrumentationConfiguration(activeRules, methodMatcher);
  }

  /**
   * Checks, if the type should be able to be instrumented. Currently, we don't instrument lambda-
   * or array classes.
   *
   * @param type the type description
   * @return true, if the provided type should NOT be able to be instrumented
   */
  private boolean shouldIgnore(TypeDescription type) {
    String typeName = type.getName();
    return typeName.contains("$$Lambda") || typeName.startsWith("[");
  }
}
