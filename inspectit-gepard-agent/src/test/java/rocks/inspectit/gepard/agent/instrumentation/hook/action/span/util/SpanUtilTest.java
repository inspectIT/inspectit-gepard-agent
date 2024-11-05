/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action.span.util;

import static io.opentelemetry.api.common.AttributeKey.stringKey;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.MethodExecutionContext;

@ExtendWith(MockitoExtension.class)
class SpanUtilTest {

  private static Tracer tracer;

  private final String spanName = "SpanUtilTest.method";

  @BeforeAll
  static void beforeAll() {
    GlobalOpenTelemetry.resetForTest();
    // Build our own OpenTelemetrySdk, so we don't use the NOOP implementations
    OpenTelemetry openTelemetry = OpenTelemetrySdk.builder().buildAndRegisterGlobal();
    tracer = openTelemetry.getTracer("inspectit-gepard");
  }

  @Nested
  class SpanAlreadyExists {
    @Test
    void shouldReturnTrueWhenSpanExists() {
      Span span = tracer.spanBuilder(spanName).startSpan();
      Scope scope = span.makeCurrent();

      boolean exists = SpanUtil.spanAlreadyExists(spanName);

      assertTrue(exists);

      scope.close();
      span.end();
    }

    @Test
    void shouldReturnFalseWhenNoSpanExists() {
      boolean exists = SpanUtil.spanAlreadyExists(spanName);

      assertFalse(exists);
    }

    @Test
    void shouldReturnFalseWhenOtherSpanExists() {
      Span span = tracer.spanBuilder("dummy").startSpan();
      Scope scope = span.makeCurrent();

      boolean exists = SpanUtil.spanAlreadyExists(spanName);

      assertFalse(exists);

      scope.close();
      span.end();
    }
  }

  @Nested
  class CreateMethodAttributes {

    @Mock private Parameter parameter1;

    @Mock private Parameter parameter2;

    @Mock private Method method;

    @Test
    void shouldCreateAttributesForMethodArguments() {
      String parameterName1 = "param1";
      String parameterName2 = "param2";
      String argument1 = "argument1";
      String argument2 = "argument2";
      MethodExecutionContext context =
          new MethodExecutionContext(
              SpanUtilTest.class, method, new Object[] {argument1, argument2});

      Parameter[] parameters = new Parameter[] {parameter1, parameter2};
      when(parameter1.getName()).thenReturn(parameterName1);
      when(parameter2.getName()).thenReturn(parameterName2);
      when(method.getParameters()).thenReturn(parameters);

      Attributes attributes = SpanUtil.createMethodAttributes(context);

      assertEquals(argument1, attributes.get(stringKey(parameterName1)));
      assertEquals(argument2, attributes.get(stringKey(parameterName2)));
    }

    @Test
    void shouldCreateEmptyAttributesForNoMethodArguments() {
      MethodExecutionContext context =
          new MethodExecutionContext(SpanUtilTest.class, method, new Object[] {});

      Attributes attributes = SpanUtil.createMethodAttributes(context);

      assertTrue(attributes.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenArgumentAndParameterCountMismatch() {
      String argument = "argument";
      MethodExecutionContext context =
          new MethodExecutionContext(SpanUtilTest.class, method, new Object[] {argument});

      Parameter[] parameters = new Parameter[] {parameter1, parameter2};
      when(method.getParameters()).thenReturn(parameters);

      assertThrows(IllegalStateException.class, () -> SpanUtil.createMethodAttributes(context));
    }
  }
}
