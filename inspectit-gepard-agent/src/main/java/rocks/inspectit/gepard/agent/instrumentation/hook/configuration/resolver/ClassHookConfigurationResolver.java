/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration.resolver;

import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.ClassHookConfiguration;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.MethodHookConfiguration;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

/**
 * This class is used to resolve {@link ClassInstrumentationConfiguration}s to a {@link
 * ClassHookConfiguration}s.
 */
public class ClassHookConfigurationResolver {
  private static final Logger log = LoggerFactory.getLogger(ClassHookConfigurationResolver.class);

  private final MethodHookConfigurationResolver methodHookResolver;

  public ClassHookConfigurationResolver(MethodHookConfigurationResolver methodHookResolver) {
    this.methodHookResolver = methodHookResolver;
  }

  /**
   * Resolves the {@link ClassInstrumentationConfiguration} to a {@link ClassHookConfiguration}. For
   * every method, which should be instrumented, there will be a {@link MethodHookConfiguration}
   * created.
   *
   * @param instrumentedMethods the set of methods from the current class, which should be
   *     instrumented
   * @param classConfiguration the instrumentation configuration of the current class
   * @param className the name of the current class
   * @return the resolved hook configuration for the class
   */
  public ClassHookConfiguration resolve(
      Set<MethodDescription> instrumentedMethods,
      ClassInstrumentationConfiguration classConfiguration,
      String className) {
    ClassHookConfiguration classHookConfiguration = new ClassHookConfiguration(methodHookResolver);

    for (MethodDescription method : instrumentedMethods) {
      try {
        classHookConfiguration.putHookConfiguration(method, classConfiguration);
      } catch (Exception e) {
        log.error("Could not create hook configuration for {}.{}", className, method.getName(), e);
      }
    }
    return classHookConfiguration;
  }
}
