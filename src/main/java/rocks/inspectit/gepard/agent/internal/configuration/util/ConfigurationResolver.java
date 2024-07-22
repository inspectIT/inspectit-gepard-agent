package rocks.inspectit.gepard.agent.internal.configuration.util;

import net.bytebuddy.description.type.TypeDescription;
import rocks.inspectit.gepard.agent.internal.configuration.ConfigurationHolder;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;

public class ConfigurationResolver {

  /**
   * @param clazz the class object
   * @return True, if the provided class should be retransformed via {@code retransform()}
   */
  public static boolean shouldRetransform(Class<?> clazz) {
    InstrumentationConfiguration configuration = getConfiguration();
    return !shouldIgnore(clazz)
        && configuration.getScopes().stream()
            .anyMatch(scope -> scope.getFqn().equals(clazz.getName()));
  }

  /**
   * @param type the type description of the class, which should be instrumented
   * @return True, if the provided type should be instrumented
   */
  public static boolean shouldInstrument(TypeDescription type) {
    String typeName = type.getName();
    InstrumentationConfiguration configuration = getConfiguration();
    return configuration.getScopes().stream()
        .anyMatch(scope -> scope.getFqn().equals(typeName) && scope.isEnabled());
  }

  /**
   * @return the current {@link InstrumentationConfiguration}
   */
  private static InstrumentationConfiguration getConfiguration() {
    return ConfigurationHolder.getInstance().getConfiguration().getInstrumentation();
  }

  /**
   * Check, if the class should be able to be instrumented. Currently, we don't instrument lambda-
   * or array classes.
   *
   * @param clazz the class object
   * @return True, if the provided class should NOT be able to be instrumented
   */
  private static boolean shouldIgnore(Class<?> clazz) {
    String className = clazz.getName();
    return className.contains("$$Lambda") || className.startsWith("[");
  }
}
