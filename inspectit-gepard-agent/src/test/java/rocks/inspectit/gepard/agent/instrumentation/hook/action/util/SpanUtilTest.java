/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpanUtilTest {

  private Tracer tracer;

  private final String spanName = "SpanUtilTest.method";

  @BeforeEach
  void beforeEach() {
    OpenTelemetry openTelemetry = OpenTelemetrySdk.builder().buildAndRegisterGlobal();
    tracer = openTelemetry.getTracer("inspectit-gepard");
  }

  @AfterEach
  void afterEach() {
    GlobalOpenTelemetry.resetForTest();
  }

  @Test
  void shouldReturnTrueWhenSpanExists() throws Exception {
    Span span = tracer.spanBuilder(spanName).startSpan();
    Scope scope = span.makeCurrent();

    boolean exists = SpanUtil.spanAlreadyExists(spanName);

    assertTrue(exists);

    scope.close();
    span.end();
  }

  @Test
  void shouldReturnFalseWhenNoSpanExists() throws Exception {
    boolean exists = SpanUtil.spanAlreadyExists(spanName);

    assertFalse(exists);
  }

  @Test
  void shouldReturnFalseWhenOtherSpanExists() throws Exception {
    Span span = tracer.spanBuilder("dummy").startSpan();
    Scope scope = span.makeCurrent();

    boolean exists = SpanUtil.spanAlreadyExists(spanName);

    assertFalse(exists);

    scope.close();
    span.end();
  }
}
