/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;

public class ClassHookConfiguration {

  /** Set of methods and their hook configuration. Currently, just true. */
  private final Map<MethodDescription, Boolean> hookConfigurations;

  public ClassHookConfiguration() {
    this.hookConfigurations = new HashMap<>();
  }

  public Map<MethodDescription, Boolean> asMap() {
    return hookConfigurations;
  }

  public Set<MethodDescription> getMethods() {
    return hookConfigurations.keySet();
  }

  public void putHookConfiguration(MethodDescription method) {
    hookConfigurations.put(method, true);
  }
}
