/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHook;

/** Stores a set of method signatures and their hook object. */
public class HookedMethods {

  /** The set of method signatures and their method hook. */
  private final Map<String, MethodHook> activeHooks;

  public HookedMethods() {
    this.activeHooks = new ConcurrentHashMap<>();
  }

  /**
   * @param signature the method signature to look for
   * @return the method hook for the provided signature
   */
  public MethodHook getActiveHook(String signature) {
    return activeHooks.get(signature);
  }

  /**
   * @return the set of stored method signatures
   */
  public Set<String> getMethodSignatures() {
    return activeHooks.keySet();
  }

  /**
   * Updates the method hook for the provided signature
   *
   * @param signature the method signature
   * @param hook the method hook
   */
  public void putMethod(String signature, MethodHook hook) {
    activeHooks.put(signature, hook);
  }

  /**
   * Removes the hook for the provided method signature
   *
   * @param methodSignature the method signature
   */
  public void removeMethod(String methodSignature) {
    activeHooks.remove(methodSignature);
  }

  /**
   * @return true, if no active hooks are stored here
   */
  public boolean noActiveHooks() {
    return activeHooks.isEmpty();
  }
}
