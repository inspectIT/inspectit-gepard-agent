/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action.util;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.sdk.trace.ReadableSpan;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/** Util class to access or create span data, like the name or attributes. */
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

  /**
   * Creates attributes of the method arguments with their name as key and with the particular
   * object as value.
   *
   * @param method the current method
   * @param arguments the method arguments as objects
   * @return the attributes for the method arguments
   */
  public static Attributes createSpanAttributes(Method method, Object[] arguments) {
    AttributesBuilder builder = Attributes.builder();
    if (arguments.length == 0) return builder.build();

    Parameter[] parameters = method.getParameters();
    if (parameters.length != arguments.length)
      throw new IllegalStateException(
          "Number of passed method parameter does not match with number of parameter in method definition of "
              + method.getName());

    for (int i = 0; i < parameters.length; i++) {
      String argumentName = parameters[i].getName();
      Object argumentValue = arguments[i];
      String argumentString = Objects.nonNull(argumentValue) ? argumentValue.toString() : null;
      builder.put(stringKey(argumentName), argumentString);
    }

    return builder.build();
  }
}
