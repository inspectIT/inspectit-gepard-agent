/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

import java.util.Objects;
import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;

/**
 * Each {@link MethodHook} instance defines for a single method which actions are performed. This
 * defines for example which generic actions are executed or which metrics are collected. Currently,
 * we just log our method calls.
 */
public class MethodHook implements IMethodHook {

  @Override
  public void onEnter(Object[] instrumentedMethodArgs, Object thiz) {
    // Using our log4j does not work here...
    String message =
        String.format(
            "inspectIT: Enter MethodHook with %d args in %s",
            instrumentedMethodArgs.length, thiz.getClass().getName());
    System.out.println(message);
    System.out.println("HELLO GEPARD");
  }

  @Override
  public void onExit(
      Object[] instrumentedMethodArgs, Object thiz, Object returnValue, Throwable thrown) {
    // Using our log4j does not work here...
    String exceptionMessage = Objects.nonNull(thrown) ? thrown.getMessage() : "no exception";
    String message =
        String.format(
            "inspectIT: Exit MethodHook who returned %s and threw %s",
            returnValue.toString(), exceptionMessage);
    System.out.println(message);
    System.out.println("BYE GEPARD");
  }
}
