/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHook;

public class HookedMethods {

  /** A set of methodSignatures and their method hook. */
  private final Map<String, MethodHook> activeHooks;

  public HookedMethods() {
    this.activeHooks = new ConcurrentHashMap<>();
  }

  public MethodHook getActiveHook(String methodSignature) {
    return activeHooks.get(methodSignature);
  }

  public Set<String> getMethodSignatures() {
    return activeHooks.keySet();
  }

  public void putMethod(String methodSignature, MethodHook methodHook) {
    activeHooks.put(methodSignature, methodHook);
  }

  public void removeMethod(String methodSignature) {
    activeHooks.remove(methodSignature);
  }

  public boolean noActiveHooks() {
    return activeHooks.isEmpty();
  }
}
