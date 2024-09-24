/* (C) 2024 */
package rocks.inspectit.gepard.bootstrap.instrumentation;

import rocks.inspectit.gepard.bootstrap.instrumentation.noop.NoopMethodHook;

/** Functional interface to implement access to method hooks for classes. */
public interface IHookManager {

  /**
   * Returns the currently configured hook for the given method of the given class. This method
   * never returns null, if no Hook is configured a {@link NoopMethodHook} is returned.
   *
   * @param clazz the class to query the hook for
   * @param methodSignature the signature of the method to query the hook for
   * @return the configured hook or a no-operation hook
   */
  IMethodHook getHook(Class<?> clazz, String methodSignature);
}
