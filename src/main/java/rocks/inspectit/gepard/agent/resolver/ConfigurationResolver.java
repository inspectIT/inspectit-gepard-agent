package rocks.inspectit.gepard.agent.resolver;

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
   * Checks, if the provided class should be retransformed.
   *
   * @param clazz the class object
   * @return true, if the provided class should be retransformed via {@code retransform()}
   */
  public boolean shouldRetransform(Class<?> clazz) {
    InstrumentationConfiguration configuration = getConfiguration();
    return !shouldIgnore(clazz)
        && configuration.getScopes().stream()
            .anyMatch(scope -> scope.getFqn().equals(clazz.getName()));
  }

  /**
   * Checks, if the provided type needs instrumentation.
   *
   * @param type the type description of the class, which should be instrumented
   * @return true, if the provided type should be instrumented
   */
  public boolean shouldInstrument(TypeDescription type) {
    String typeName = type.getName();
    InstrumentationConfiguration configuration = getConfiguration();
    return configuration.getScopes().stream()
        .anyMatch(scope -> scope.getFqn().equals(typeName) && scope.isEnabled());
  }

  /**
   * @return the current {@link InstrumentationConfiguration}
   */
  private InstrumentationConfiguration getConfiguration() {
    return holder.getConfiguration().getInstrumentation();
  }

  /**
   * Checks, if the class should be able to be instrumented. Currently, we don't instrument lambda-
   * or array classes.
   *
   * @param clazz the class object
   * @return true, if the provided class should NOT be able to be instrumented
   */
  private boolean shouldIgnore(Class<?> clazz) {
    String className = clazz.getName();
    return className.contains("$$Lambda") || className.startsWith("[");
  }

  public ElementMatcher.Junction<MethodDescription> getElementMatcherForType(TypeDescription type) {
    InstrumentationConfiguration configuration = getConfiguration();
    Scope scope = configuration.getScopeByFqn(type.getName());
    String methodName = scope.getMethod();
    if (methodName == null) {
      return ElementMatchers.isMethod();
    }
    return ElementMatchers.named(methodName);
  }
}
