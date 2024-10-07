/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import java.util.Objects;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.exception.CouldNotCloseSpanScopeException;
import rocks.inspectit.gepard.agent.internal.otel.OpenTelemetryAccessor;

/** This action contains the logic to start and end a {@link Span}. */
public class SpanAction {

  private static final String INSTRUMENTATION_SCOPE_NAME = "inspectit-gepard";

  private final OpenTelemetry openTelemetry;

  public SpanAction() {
    this.openTelemetry = OpenTelemetryAccessor.getOpenTelemetry();
  }

  // TODO We need to check, if a span is already recorded for the current method by OTEL

  /**
   * Starts a new {@link Span}. Should be called before {@link SpanAction#endSpan}.
   *
   * @param spanName the name of the created span
   * @return the scope of the started span
   */
  public AutoCloseable startSpan(String spanName) {
    Tracer tracer = openTelemetry.getTracer(INSTRUMENTATION_SCOPE_NAME);
    Span span = tracer.spanBuilder(spanName).setParent(Context.current()).startSpan();
    return span.makeCurrent();
  }

  /**
   * Ends the current span and closes its scope. Should be called after {@link
   * SpanAction#startSpan}.
   *
   * @param spanScope the scope of the span, which should be finished
   */
  public void endSpan(AutoCloseable spanScope) {
    Span current = Span.current();

    if (Objects.nonNull(spanScope)) {
      try {
        spanScope.close();
      } catch (Exception e) {
        throw new CouldNotCloseSpanScopeException(e);
      }
    }
    current.end();
  }
}
