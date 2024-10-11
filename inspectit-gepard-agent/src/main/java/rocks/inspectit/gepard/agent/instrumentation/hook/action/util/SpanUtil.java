/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action.util;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.sdk.trace.data.SpanData;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.exception.SdkSpanClassNotFoundException;

/**
 * Special util class, to access the package private {@link io.opentelemetry.sdk.trace.SdkSpan}
 * class. We want to access this class, so we can read data from the current span, like the name or
 * attributes. To do so, we use reflection.
 */
public class SpanUtil {

  /** The class of {@link io.opentelemetry.sdk.trace.SdkSpan} */
  private static final Class<?> SDKSPAN_CLASS;

  private SpanUtil() {}

  static {
    String className = "io.opentelemetry.sdk.trace.SdkSpan";
    try {
      SDKSPAN_CLASS = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new SdkSpanClassNotFoundException(className, e);
    }
  }

  /**
   * Checks, if the current span uses the provided span name. This check might be necessary to
   * prevent span duplicates. For example, we would like to create a span for a method, which is
   * already recorded by OpenTelemetry. In this case, we should not create a new span.
   *
   * @param spanName the name of the span with the format 'simple-class-name.method-name', for
   *     instance 'SpanUtil.spanAlreadyExists'
   * @return true, if the current span uses the provided span name
   */
  public static boolean spanAlreadyExists(String spanName)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Span span = Span.current();

    if (!SDKSPAN_CLASS.isInstance(span)) {
      throw new IllegalArgumentException("The provided Span is not an instance of SdkSpan.");
    }

    Object sdkSpan = SDKSPAN_CLASS.cast(span);
    Method toSpanData = SDKSPAN_CLASS.getDeclaredMethod("toSpanData");
    toSpanData.setAccessible(true);

    SpanData spanData = (SpanData) toSpanData.invoke(sdkSpan);

    return spanName.equals(spanData.getName());
  }
}
