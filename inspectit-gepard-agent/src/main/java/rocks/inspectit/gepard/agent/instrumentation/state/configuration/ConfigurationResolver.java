/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration;

import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.scope.ScopeResolver;
import rocks.inspectit.gepard.config.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.InstrumentationScope;

/**
 * Utility class to resolve the {@link InstrumentationConfiguration} and determine whether class
 * byte code needs updates.
 */
public class ConfigurationResolver {

  private final ScopeResolver scopeResolver;

  private ConfigurationResolver(InspectitConfigurationHolder holder) {
    this.scopeResolver = new ScopeResolver(holder);
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
   * Gets the current instrumentation configuration for the specified class.
   *
   * @param clazz the class
   * @return The active configuration or {@link
   *     ClassInstrumentationConfiguration#NO_INSTRUMENTATION}
   */
  public ClassInstrumentationConfiguration getClassInstrumentationConfiguration(Class<?> clazz) {
    String className = clazz.getName();
    return getClassInstrumentationConfiguration(className);
  }

  /**
   * Gets the current instrumentation configuration for the specified class.
   *
   * @param type the instrumented type
   * @return The active configuration or {@link
   *     ClassInstrumentationConfiguration#NO_INSTRUMENTATION}
   */
  public ClassInstrumentationConfiguration getClassInstrumentationConfiguration(
      InstrumentedType type) {
    String typeName = type.getName();
    return getClassInstrumentationConfiguration(typeName);
  }

  /**
   * Gets the current instrumentation configuration for the provided class name
   *
   * @param fullyQualifiedName the class name
   * @return The active configuration or {@link
   *     ClassInstrumentationConfiguration#NO_INSTRUMENTATION}
   */
  private ClassInstrumentationConfiguration getClassInstrumentationConfiguration(
      String fullyQualifiedName) {
    Set<InstrumentationScope> activeScopes = scopeResolver.getActiveScopes(fullyQualifiedName);

    if (activeScopes.isEmpty()) return ClassInstrumentationConfiguration.NO_INSTRUMENTATION;

    ElementMatcher.Junction<MethodDescription> methodMatcher =
        scopeResolver.getMethodMatcher(activeScopes);
    return new ClassInstrumentationConfiguration(activeScopes, methodMatcher);
  }
}
