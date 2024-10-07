/* (C) 2024 */
package rocks.inspectit.gepard.bootstrap.instrumentation;

import rocks.inspectit.gepard.bootstrap.context.InternalInspectitContext;

/**
 * Defines the behaviour of method hooks. Each hook should contain a method, which be called while
 * entering and exiting an instrumented method.
 */
public interface IMethodHook {

  /**
   * Called when the hooked method is entered.
   *
   * @param instrumentedMethodArgs the arguments passed to the method for which the hook is executed
   * @param thiz the "this" instance of the invoked method, null if the invoked method is static
   * @return the created inspectIT context
   */
  InternalInspectitContext onEnter(Object[] instrumentedMethodArgs, Object thiz);

  /**
   * Called when the hooked method exits.
   *
   * @param context the inspectIT context of the current method
   * @param instrumentedMethodArgs the arguments passed to the method for which the hook is executed
   * @param thiz the "this" instance of the invoked method, null if the invoked method is static
   * @param returnValue the return value returned by the target method, if this hook is executed at
   *     the end and no exception was thrown
   * @param thrown the exception thrown by the instrumented method, null otherwise
   */
  void onExit(
      InternalInspectitContext context,
      Object[] instrumentedMethodArgs,
      Object thiz,
      Object returnValue,
      Throwable thrown);
}
