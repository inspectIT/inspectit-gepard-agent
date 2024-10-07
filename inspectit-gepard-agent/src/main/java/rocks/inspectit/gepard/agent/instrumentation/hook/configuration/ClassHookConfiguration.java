/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;

/**
 * Stores the hook configuration of all methods for a specific class. Currently, there is no complex
 * hook configuration, thus we only use booleans.
 */
public class ClassHookConfiguration {

  /** Set of methods and their hook configuration. Currently, just true. */
  private final Map<MethodDescription, Boolean> hookConfigurations;

  public ClassHookConfiguration() {
    this.hookConfigurations = new HashMap<>();
  }

  /**
   * @return the configuration as map
   */
  public Map<MethodDescription, Boolean> asMap() {
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
  public void putHookConfiguration(MethodDescription method) {
    hookConfigurations.put(method, true);
  }
}
