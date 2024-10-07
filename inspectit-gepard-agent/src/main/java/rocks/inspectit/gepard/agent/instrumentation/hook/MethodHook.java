/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import java.util.Objects;
import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;

/**
 * Each {@link MethodHook} instance defines for a single method which actions are performed. This
 * defines for example which generic actions are executed or which metrics are collected. Currently,
 * we just log our method calls.
 */
public class MethodHook implements IMethodHook {

  // TODO Property entryActions mit StartSpanAction drinnen

  // TODO Property exitActions mit EndSpanAction drinnen

  // TODO diese Listen abarbeiten und execute() aufrufen

  @Override
  public AutoCloseable onEnter(String methodName, Object[] instrumentedMethodArgs, Object thiz) {
    // Using our log4j here will not be visible in the target application...
    String message =
        String.format(
            "inspectIT: Enter MethodHook with %d args in %s",
            instrumentedMethodArgs.length, thiz.getClass().getName());
    System.out.println(message);

    AutoCloseable spanScope = startSpan(methodName);

    System.out.println("HELLO GEPARD : " + methodName);
    return spanScope;
  }

  @Override
  public void onExit(
      String methodName,
      AutoCloseable spanScope,
      Object[] instrumentedMethodArgs,
      Object thiz,
      Object returnValue,
      Throwable thrown) {
    try {
      // Using our log4j here will not be visible in the target application...
      String exceptionMessage = Objects.nonNull(thrown) ? thrown.getMessage() : "no exception";
      String returnMessage = Objects.nonNull(returnValue) ? returnValue.toString() : "nothing";
      String message =
          String.format(
              "inspectIT: Exit MethodHook who returned %s and threw %s",
              returnMessage, exceptionMessage);
      System.out.println(message);
      endSpan(methodName, spanScope);

      System.out.println("BYE GEPARD");
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  // TODO move to StartSpanAction
  private AutoCloseable startSpan(String name) {
    Span current = Span.current();
    Tracer tracer = GlobalOpenTelemetry.getTracer("inspectit-gepard");
    Span span = tracer.spanBuilder(name).setParent(Context.current()).startSpan();
    return span.makeCurrent();
  }

  // TODO move to EndSpanAction
  // TODO Zuerst scope schließen oder span beenden?
  private void endSpan(String name, AutoCloseable spanScope) throws Exception {
    Span current = Span.current();
    System.out.println("END SPAN FOR " + name);

    spanScope.close(); // TODO Exception handling
    current.end();
  }
}
