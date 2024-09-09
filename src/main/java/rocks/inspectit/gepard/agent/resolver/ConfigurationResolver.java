package rocks.inspectit.gepard.agent.resolver;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;
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
   * Checks, if the provided class requires instrumentation.
   *
   * @param clazz the class object
   * @return true, if the provided class should be instrumented
   */
  public boolean shouldInstrument(Class<?> clazz) {
    String className = clazz.getName();
    return shouldInstrument(className);
  }

  /**
   * Checks, if the provided type requires instrumentation.
   *
   * @param type the class type description
   * @return true, if the provided type should be instrumented
   */
  public boolean shouldInstrument(TypeDescription type) {
    String typeName = type.getName();
    return shouldInstrument(typeName);
  }

  /**
   * Gets a matcher for the methods of the provided type, based on the configured scope.
   *
   * @param typeDescription the type to build the method matcher for
   * @return a matcher for the methods of the provided type
   */
  public ElementMatcher.Junction<MethodDescription> getMethodMatcher(
          TypeDescription typeDescription) {

    return scopeResolver.buildMethodMatcher(typeDescription);
  }

  /**
   * Checks, if the provided class name requires instrumentation.
   *
   * @param fullyQualifiedName the full name of the class
   * @return true, if the provided class should be instrumented
   */
  private boolean shouldInstrument(String fullyQualifiedName) {
    return scopeResolver.shouldInstrument(fullyQualifiedName);
  }

}
