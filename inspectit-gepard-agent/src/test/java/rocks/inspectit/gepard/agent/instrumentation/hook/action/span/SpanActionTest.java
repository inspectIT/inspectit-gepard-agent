/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action.span;

import static io.opentelemetry.api.common.AttributeKey.stringKey;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.ReadableSpan;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.MethodExecutionContext;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.span.exception.CouldNotCloseSpanScopeException;
import rocks.inspectit.gepard.agent.internal.otel.OpenTelemetryAccessor;

@ExtendWith(MockitoExtension.class)
class SpanActionTest {

  @Mock private AutoCloseable closeable;

  @Mock private Parameter parameter;

  @Mock private Method method;

  private MethodExecutionContext executionContext;

  private final String key = "key";

  private final String value = "value";

  // Format: SimpleClassName.methodName
  private final String spanName = "SpanActionTest.method";

  private final SpanAction action = new SpanAction();

  @BeforeAll
  static void beforeAll() {
    GlobalOpenTelemetry.resetForTest();
    // Build our own OpenTelemetrySdk, so we don't use the NOOP implementations
    OpenTelemetrySdk.builder().buildAndRegisterGlobal();
    OpenTelemetryAccessor.setOpenTelemetry(GlobalOpenTelemetry.get());
  }

  @BeforeEach
  void beforeEach() {
    Parameter[] parameters = new Parameter[] {parameter};
    lenient().when(method.getParameters()).thenReturn(parameters);
    lenient().when(method.getName()).thenReturn("method");
    lenient().when(parameter.getName()).thenReturn(key);

    executionContext =
        new MethodExecutionContext(SpanActionTest.class, method, new Object[] {value});
  }

  @Test
  void shouldCreateScopeAndWriteAttributesWhenNoSpanExist() throws Exception {
    Optional<AutoCloseable> scope = action.startSpan(executionContext);
    ReadableSpan currentSpan = (ReadableSpan) Span.current();

    assertTrue(scope.isPresent());
    assertEquals(currentSpan.getAttribute(stringKey(key)), value);

    scope.get().close();
  }

  @Test
  void shouldNotCreateScopeButWriteAttributesWhenSpanAlreadyExist() {
    Tracer tracer = GlobalOpenTelemetry.getTracer("otel");
    Span otelSpan = tracer.spanBuilder(spanName).startSpan();
    Scope otelScope = otelSpan.makeCurrent();

    Optional<AutoCloseable> scope = action.startSpan(executionContext);
    ReadableSpan currentSpan = (ReadableSpan) Span.current();

    assertTrue(scope.isEmpty());
    assertEquals(currentSpan.getAttribute(stringKey(key)), value);

    otelScope.close();
    otelSpan.end();
  }

  @Test
  void shouldCreateEmptyAttributesForNoMethodArguments() throws Exception {
    executionContext = new MethodExecutionContext(SpanActionTest.class, method, new Object[] {});

    Optional<AutoCloseable> scope = action.startSpan(executionContext);
    ReadableSpan currentSpan = (ReadableSpan) Span.current();

    assertTrue(scope.isPresent());
    assertTrue(currentSpan.getAttributes().isEmpty());

    scope.get().close();
  }

  @Test
  void shouldCloseScope() throws Exception {
    action.endSpan(closeable);

    verify(closeable).close();
  }

  @Test
  void shouldThrowExceptionWhenScopeNotClosable() throws Exception {
    doThrow(Exception.class).when(closeable).close();

    assertThrows(CouldNotCloseSpanScopeException.class, () -> action.endSpan(closeable));
  }
}
