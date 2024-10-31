/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action;

import static io.opentelemetry.api.common.AttributeKey.stringKey;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.ReadableSpan;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.exception.CouldNotCloseSpanScopeException;
import rocks.inspectit.gepard.agent.internal.otel.OpenTelemetryAccessor;

@ExtendWith(MockitoExtension.class)
class SpanActionTest {

  @Mock private AutoCloseable closeable;

  private final SpanAction action = new SpanAction();

  @BeforeAll
  static void beforeAll() {
    // Build our own OpenTelemetrySdk, so we don't use the NOOP implementations
    OpenTelemetrySdk.builder().buildAndRegisterGlobal();
    OpenTelemetryAccessor.setOpenTelemetry(GlobalOpenTelemetry.get());
  }

  @Test
  void shouldCreateScope() throws Exception {
    String spanName = "Test.method";
    Attributes attributes = Attributes.empty();

    AutoCloseable scope = action.startSpan(spanName, attributes);
    ReadableSpan currentSpan = (ReadableSpan) Span.current();

    assertNotNull(scope);
    assertTrue(currentSpan.getAttributes().isEmpty());

    scope.close();
  }

  @Test
  void shouldSetAttributes() throws Exception {
    String spanName = "Test.method";
    AttributeKey<String> key = stringKey("key");
    String value = "value";
    Attributes attributes = Attributes.builder().put(key, value).build();

    AutoCloseable scope = action.startSpan(spanName, attributes);
    ReadableSpan currentSpan = (ReadableSpan) Span.current();

    assertEquals(currentSpan.getAttribute(key), value);

    scope.close();
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
