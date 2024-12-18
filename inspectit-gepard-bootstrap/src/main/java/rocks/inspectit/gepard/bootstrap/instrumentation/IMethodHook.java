/* (C) 2024 */
package rocks.inspectit.gepard.bootstrap.instrumentation;

import java.lang.reflect.Method;
import rocks.inspectit.gepard.bootstrap.context.InternalInspectitContext;

/**
 * Defines the behaviour of method hooks. Each hook should contain a method, which will be called
 * while entering and exiting an instrumented method.
 */
public interface IMethodHook {

  /**
   * Called when the hooked method is entered. Stores internal data within the returned context.
   *
   * @param clazz the class object of the hooked method
   * @param thiz the {@code this} instance of the invoked method, null if the invoked method is
   *     static
   * @param method the method of this hook
   * @param instrumentedMethodArgs the arguments passed to the method for which the hook is executed
   * @return the created inspectIT context
   */
  InternalInspectitContext onEnter(
      Class<?> clazz, Object thiz, Method method, Object[] instrumentedMethodArgs);

  /**
   * Called when the hooked method exits.
   *
   * @param context the internal inspectIT context of the current method
   * @param returnValue the return value returned by the target method, if this hook is executed at
   *     the end and no exception was thrown
   * @param thrown the exception thrown by the instrumented method, null otherwise
   */
  void onExit(InternalInspectitContext context, Object returnValue, Throwable thrown);
}
