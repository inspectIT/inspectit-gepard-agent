package rocks.inspectit.gepard.agent.resolver;

import java.util.*;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.Scope;

/**
 * Utility class to resolve the {@link InstrumentationConfiguration} and determine whether class
 * byte code needs updates.
 */
public class ConfigurationResolver {

  private final ConfigurationHolder holder;

  private ConfigurationResolver(ConfigurationHolder holder) {
    this.holder = holder;
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
   * Checks, if the provided class name requires instrumentation.
   *
   * @param fullyQualifiedName the full name of the class
   * @return true, if the provided class should be instrumented
   */
  private boolean shouldInstrument(String fullyQualifiedName) {
    InstrumentationConfiguration configuration = getConfiguration();

    return !shouldIgnore(fullyQualifiedName)
        && configuration.getScopes().stream()
            .anyMatch(scope -> scope.getFqn().equals(fullyQualifiedName) && scope.isEnabled());
  }

  /**
   * Builds a matcher for the methods of the provided type, based on the configured scope.
   *
   * @param typeDescription the type to build the method matcher for
   * @return a matcher for the methods of the provided type
   */
  public ElementMatcher.Junction<MethodDescription> buildMethodMatcher(
      TypeDescription typeDescription) {

    // Get the configuration and retrieve all matching scopes
    List<Scope> scopes = getConfiguration().getAllScopeWithFqn(typeDescription.getName());

    // Collect method names from the scopes, skipping null or empty method lists
    List<String> methodNames =
        scopes.stream()
            .map(Scope::getMethods) // Map Scope to its methods list
            .filter(Objects::nonNull) // Filter out null method lists
            .flatMap(Collection::stream) // Flatten the list of methods
            .toList();

    // If no method names were found, match any method
    if (methodNames.isEmpty()) {
      return ElementMatchers.isMethod();
    }

    // Otherwise, match by method names
    return ElementMatchers.namedOneOf(methodNames.toArray(String[]::new));
  }

  /**
   * @return the current {@link InstrumentationConfiguration}
   */
  private InstrumentationConfiguration getConfiguration() {
    return holder.getConfiguration().getInstrumentation();
  }

  /**
   * Checks, if the type should be able to be instrumented. Currently, we don't instrument lambda-
   * or array classes.
   *
   * @param fullyQualifiedName the full name of the class
   * @return true, if the provided type should NOT be able to be instrumented
   */
  private boolean shouldIgnore(String fullyQualifiedName) {
    return fullyQualifiedName.contains("$$Lambda") || fullyQualifiedName.startsWith("[");
  }
}
