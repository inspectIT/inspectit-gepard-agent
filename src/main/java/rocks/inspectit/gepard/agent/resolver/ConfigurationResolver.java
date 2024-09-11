package rocks.inspectit.gepard.agent.resolver;

import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.InstrumentationScope;
import rocks.inspectit.gepard.agent.resolver.scope.ScopeResolver;

/**
 * Utility class to resolve the {@link InstrumentationConfiguration} and determine whether class
 * byte code needs updates.
 */
public class ConfigurationResolver {

  private final ScopeResolver scopeResolver;

  private ConfigurationResolver(ConfigurationHolder holder) {
    this.scopeResolver = new ScopeResolver(holder);
  }

  /**
   * Factory method to create an {@link ConfigurationResolver}
   *
   * @return the created resolver
   */
  public static ConfigurationResolver create(ConfigurationHolder holder) {
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
   * @param typeDescription the type
   * @return The active configuration or {@link
   *     ClassInstrumentationConfiguration#NO_INSTRUMENTATION}
   */
  public ClassInstrumentationConfiguration getClassInstrumentationConfiguration(
      TypeDescription typeDescription) {
    String typeName = typeDescription.getName();
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
    return new ClassInstrumentationConfiguration(activeScopes);
  }

  /**
   * Gets a matcher for the methods of the provided type, based on the configured scope.
   *
   * @param typeDescription the type to build the method matcher for
   * @return a matcher for the methods of the provided type
   */
  public ElementMatcher.Junction<MethodDescription> getMethodMatcher(
      TypeDescription typeDescription) {
    return scopeResolver.getMethodMatcher(typeDescription);
  }
}
