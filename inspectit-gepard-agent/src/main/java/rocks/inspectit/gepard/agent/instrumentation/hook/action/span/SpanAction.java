/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action.span;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.MethodExecutionContext;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.span.exception.CouldNotCloseSpanScopeException;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.span.util.SpanUtil;
import rocks.inspectit.gepard.agent.internal.otel.OpenTelemetryAccessor;

/** This action contains the logic to start and end a {@link Span}. */
public class SpanAction {
  private static final Logger log = LoggerFactory.getLogger(SpanAction.class);

  /**
   * Starts a new {@link Span} and adds the provided attributes to the current span. If the
   * currently active span already uses the provided spanName, no new span will be created. Should
   * be called before {@link SpanAction#endSpan}.
   *
   * @param executionContext the execution context of the current method
   * @return the scope of the started span or empty, if the current span has the same name
   */
  public Optional<AutoCloseable> startSpan(MethodExecutionContext executionContext) {
    String spanName = getSpanName(executionContext);
    Attributes methodAttributes;
    try {
      methodAttributes = SpanUtil.createMethodAttributes(executionContext);
    } catch (Exception e) {
      log.warn("Failed to read method attributes: {}", e.getMessage());
      methodAttributes = Attributes.empty();
    }

    // Use the OTel span
    if (SpanUtil.spanAlreadyExists(spanName)) {
      log.debug("Span '{}' already exists at the moment. No new span will be started", spanName);
      Span otelSpan = Span.current();
      // We overwrite the OTel attributes, if they use the same key
      otelSpan.setAllAttributes(methodAttributes);
      return Optional.empty();
    }

    // Create new inspectIT span
    Tracer tracer = OpenTelemetryAccessor.getTracer();
    Span span = tracer.spanBuilder(spanName).setParent(Context.current()).startSpan();
    span.setAllAttributes(methodAttributes);
    return Optional.of(span.makeCurrent());
  }

  /**
   * Ends the current span and closes the provided scope. Should be called after {@link
   * SpanAction#startSpan}.
   *
   * @param spanScope the scope of the span, which should be finished
   */
  public void endSpan(AutoCloseable spanScope) {
    Span current = Span.current();
    try {
      spanScope.close();
    } catch (Exception e) {
      throw new CouldNotCloseSpanScopeException(e);
    }
    current.end();
  }

  /**
   * @param executionContext the context of the current method
   * @return the span name for the current method in the format 'SimpleClassName.methodName', for
   *     instance 'MethodHook.getSpanName'
   */
  private String getSpanName(MethodExecutionContext executionContext) {
    String simpleClassName = executionContext.getDeclaringClass().getSimpleName();
    String methodName = executionContext.getMethod().getName();
    return simpleClassName + "." + methodName;
  }
}
