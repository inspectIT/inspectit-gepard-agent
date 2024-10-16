/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action.util;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.sdk.trace.ReadableSpan;

/** Util class to access span data, like the name or attributes. */
public class SpanUtil {

  private SpanUtil() {}

  /**
   * Checks, if the current span uses the provided span name. This check might be necessary to
   * prevent span duplicates. For example, we would like to create a span for a method, which is
   * already recorded by OpenTelemetry. In this case, we should not create a new span.
   *
   * @param spanName the name of the span with the format 'SimpleClassName.methodName', for instance
   *     'SpanUtil.spanAlreadyExists'
   * @return true, if the current span uses the provided span name
   */
  public static boolean spanAlreadyExists(String spanName) {
    Span span = Span.current();

    if (span instanceof ReadableSpan readableSpan) return spanName.equals(readableSpan.getName());
    return false;
  }
}
