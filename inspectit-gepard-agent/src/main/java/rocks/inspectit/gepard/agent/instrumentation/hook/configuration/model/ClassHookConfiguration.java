/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.resolver.MethodHookConfigurationResolver;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

/**
 * Stores the hook configuration of all methods for a specific class, which is used to update hooks.
 */
public class ClassHookConfiguration {

  /** Set of methods and their hook configuration. */
  private final Map<MethodDescription, MethodHookConfiguration> hookConfigurations;

  private final MethodHookConfigurationResolver hookResolver;

  public ClassHookConfiguration(MethodHookConfigurationResolver hookResolver) {
    this.hookConfigurations = new HashMap<>();
    this.hookResolver = hookResolver;
  }

  /**
   * @return the configuration as map
   */
  public Map<MethodDescription, MethodHookConfiguration> asMap() {
    return hookConfigurations;
  }

  /**
   * @return the set of all methods within this configuration
   */
  public Set<MethodDescription> getMethods() {
    return hookConfigurations.keySet();
  }

  /**
   * Stores a new hook configuration.
   *
   * @param method the method, which should be put into the configurations
   */
  public void putHookConfiguration(
      MethodDescription method, ClassInstrumentationConfiguration classConfig) {
    MethodHookConfiguration hookConfig = hookResolver.resolve(method, classConfig);
    hookConfigurations.put(method, hookConfig);
  }
}
