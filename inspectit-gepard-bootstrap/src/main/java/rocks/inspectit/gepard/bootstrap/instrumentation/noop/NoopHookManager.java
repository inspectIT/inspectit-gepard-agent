/* (C) 2024 */
package rocks.inspectit.gepard.bootstrap.instrumentation.noop;

import rocks.inspectit.gepard.bootstrap.instrumentation.IHookManager;
import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;

/** No-operation implementation of {@link IHookManager} */
public class NoopHookManager implements IHookManager {

  public static final NoopHookManager INSTANCE = new NoopHookManager();

  private NoopHookManager() {}

  @Override
  public IMethodHook getHook(Class<?> clazz, String methodSignature) {
    return NoopMethodHook.INSTANCE;
  }
}
