/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.SpanAction;
import rocks.inspectit.gepard.bootstrap.context.InternalInspectitContext;
import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;

/**
 * Each {@link MethodHook} instance defines for a single method which actions are performed. This
 * defines for example which generic actions are executed or which metrics are collected. Currently,
 * we just log our method calls.
 */
public class MethodHook implements IMethodHook {
  private static final Logger log = LoggerFactory.getLogger(MethodHook.class);

  private final String methodName;

  private final SpanAction spanAction;

  public MethodHook(String methodName, SpanAction spanAction) {
    this.methodName = methodName;
    this.spanAction = spanAction;
  }

  @Override
  public InternalInspectitContext onEnter(Object[] instrumentedMethodArgs, Object thiz) {
    String message =
        String.format(
            "inspectIT: Enter MethodHook with %d args in %s",
            instrumentedMethodArgs.length, thiz.getClass().getName());
    System.out.println(message);

    String spanName = thiz.getClass().getSimpleName() + "." + methodName;
    AutoCloseable spanScope = null;

    try {
      spanScope = spanAction.startSpan(spanName);
    } catch (Exception e) {
      log.error("Could not execute start-span-action", e);
    }

    // Using our log4j here will not be visible in the target application...
    System.out.println("HELLO GEPARD : " + methodName);
    return new InternalInspectitContext(this, spanScope);
  }

  @Override
  public void onExit(
      InternalInspectitContext context,
      Object[] instrumentedMethodArgs,
      Object thiz,
      Object returnValue,
      Throwable thrown) {
    String exceptionMessage = Objects.nonNull(thrown) ? thrown.getMessage() : "no exception";
    String returnMessage = Objects.nonNull(returnValue) ? returnValue.toString() : "nothing";
    String message =
        String.format(
            "inspectIT: Exit MethodHook who returned %s and threw %s",
            returnMessage, exceptionMessage);
    System.out.println(message);

    AutoCloseable spanScope = context.getSpanScope();
    try {
      spanAction.endSpan(spanScope);
    } catch (Exception e) {
      log.error("Could not execute end-span-action", e);
    }

    // Using our log4j here will not be visible in the target application...
    System.out.println("BYE GEPARD");
  }
}
