/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

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
    String spanName = thiz.getClass().getSimpleName() + "." + methodName;
    AutoCloseable spanScope = null;

    try {
      spanScope = spanAction.startSpan(spanName);
    } catch (Throwable t) {
      log.error("Could not execute start-span-action", t);
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
    AutoCloseable spanScope = context.getSpanScope();
    try {
      spanAction.endSpan(spanScope);
    } catch (Throwable t) {
      log.error("Could not execute end-span-action", t);
    }

    // Using our log4j here will not be visible in the target application...
    System.out.println("BYE GEPARD");
  }
}
