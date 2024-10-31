/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.exception.CouldNotCloseSpanScopeException;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.util.SpanUtil;
import rocks.inspectit.gepard.agent.internal.otel.OpenTelemetryAccessor;

/** This action contains the logic to start and end a {@link Span}. */
public class SpanAction {
  private static final Logger log = LoggerFactory.getLogger(SpanAction.class);

  /**
   * Starts a new {@link Span} and adds the provided attributes to the current span. If the
   * currently active span already uses the provided spanName, no new span will be created. Should
   * be called before {@link SpanAction#endSpan}.
   *
   * @param spanName the name of the span
   * @param attributes the attributes for the span
   * @return the scope of the started span or null, if the current span has the same name
   */
  public AutoCloseable startSpan(String spanName, Attributes attributes) {
    if (SpanUtil.spanAlreadyExists(spanName)) {
      log.debug("Span '{}' already exists at the moment. No new span will be started", spanName);
      Span otelSpan = Span.current();
      // We overwrite the OTel attributes, if they use the same key
      otelSpan.setAllAttributes(attributes);
      return null;
    }

    Tracer tracer = OpenTelemetryAccessor.getTracer();
    Span span = tracer.spanBuilder(spanName).setParent(Context.current()).startSpan();
    span.setAllAttributes(attributes);
    return span.makeCurrent();
  }

  /**
   * Ends the current span and closes its scope. Should be called after {@link
   * SpanAction#startSpan}. If the scope is null, we won't do anything.
   *
   * @param spanScope the scope of the span, which should be finished
   */
  public void endSpan(AutoCloseable spanScope) {
    if (Objects.isNull(spanScope)) return;

    Span current = Span.current();
    try {
      spanScope.close();
    } catch (Exception e) {
      throw new CouldNotCloseSpanScopeException(e);
    }
    current.end();
  }
}
