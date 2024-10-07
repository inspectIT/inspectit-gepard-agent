/* (C) 2024 */
package rocks.inspectit.gepard.bootstrap.instrumentation.noop;

import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;

/** No-operation implementation of {@link IMethodHook} */
public class NoopMethodHook implements IMethodHook {

  public static final NoopMethodHook INSTANCE = new NoopMethodHook();

  private NoopMethodHook() {}

  @Override
  public AutoCloseable onEnter(String methodName, Object[] instrumentedMethodArgs, Object thiz) {
    return null;
  }

  @Override
  public void onExit(String methodName, AutoCloseable spanScope,
      Object[] instrumentedMethodArgs, Object thiz, Object returnValue, Throwable thrown) {}
}
