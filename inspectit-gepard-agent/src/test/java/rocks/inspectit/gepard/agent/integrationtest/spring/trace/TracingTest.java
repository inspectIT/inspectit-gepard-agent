/* (C) 2024 */
package rocks.inspectit.gepard.agent.integrationtest.spring.trace;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.integrationtest.spring.SpringTestBase;

/** Should check, if traces are received in our tracing backend according to our configuration. */
class TracingTest extends SpringTestBase {

  private static final String parentSpanName = "WebController.greeting";

  private static final String childSpanName = "WebController.withSpan";

  @Override
  protected Map<String, String> getExtraEnv() {
    // We need to disable the OTel annotations to prevent span duplicates
    return Map.of("OTEL_INSTRUMENTATION_OPENTELEMETRY_EXTENSION_ANNOTATIONS_ENABLED", "false");
  }

  @Test
  void shouldSendSpansToBackendWhenScopesAreActive() throws Exception {
    configurationServerMock.configServerSetup(configDir + "simple-config.json");
    startTarget("/opentelemetry-extensions.jar");
    awaitInstrumentationUpdate(1);

    sendRequestToTarget("/greeting");
    Collection<ExportTraceServiceRequest> traces = waitForTraces();

    assertSpans(traces, parentSpanName, childSpanName);
  }

  @Test
  void shouldNotSendSpansToBackendWhenNoScopesAreActive() throws Exception {
    configurationServerMock.configServerSetup(configDir + "empty-config.json");
    startTarget("/opentelemetry-extensions.jar");
    awaitInstrumentationUpdate(1);

    sendRequestToTarget("/greeting");
    Collection<ExportTraceServiceRequest> traces = waitForTraces();

    assertNoSpans(traces, parentSpanName, childSpanName);
  }

  /**
   * This method asserts that spans with the given names exist and that the child's {@link
   * Span#getParentSpanId()} equals the parent's {@link Span#getSpanId()}.
   *
   * @param traces the collection of traces
   * @param parentSpanName the name of the parent span
   * @param childSpanName the name of the child span
   */
  private void assertSpans(
      Collection<ExportTraceServiceRequest> traces, String parentSpanName, String childSpanName) {
    Stream<List<Span>> spanLists = getSpanLists(traces);

    assertTrue(
        spanLists.anyMatch(
            spans -> {
              Optional<Span> parentSpan = findSpan(spans, parentSpanName);
              if (parentSpan.isEmpty()) return false;

              Optional<Span> childSpan = findSpan(spans, childSpanName);
              if (childSpan.isEmpty()) return false;

              // We cannot compare the ByteStrings directly, because they are different objects
              String childParentId = childSpan.get().getParentSpanId().toStringUtf8();
              String parentId = parentSpan.get().getSpanId().toStringUtf8();
              return childParentId.equals(parentId);
            }));
  }

  /**
   * This method asserts that no spans with the given names exist.
   *
   * @param traces the collection of traces
   * @param parentSpanName the name of the parent span
   * @param childSpanName the name of the child span
   */
  private void assertNoSpans(
      Collection<ExportTraceServiceRequest> traces, String parentSpanName, String childSpanName) {
    Stream<List<Span>> spanLists = getSpanLists(traces);

    assertTrue(
        spanLists.anyMatch(
            spans -> {
              Optional<Span> parentSpan = findSpan(spans, parentSpanName);
              Optional<Span> childSpan = findSpan(spans, childSpanName);

              return parentSpan.isEmpty() && childSpan.isEmpty();
            }));
  }

  /**
   * Maps the provided traces to a collection of span lists.
   *
   * @param traces the collection of traces
   * @return the collection of span lists within the provided traces
   */
  private Stream<List<Span>> getSpanLists(Collection<ExportTraceServiceRequest> traces) {
    return traces.stream()
        .flatMap(
            trace ->
                trace.getResourceSpansList().stream()
                    .flatMap(
                        resourceSpans ->
                            resourceSpans.getScopeSpansList().stream()
                                .map(ScopeSpans::getSpansList)));
  }

  /**
   * Tries to find the provided span name in the list of spans.
   *
   * @param spans the list of spans
   * @param spanName the span name to look for
   * @return the found span or empty
   */
  private Optional<Span> findSpan(List<Span> spans, String spanName) {
    return spans.stream().filter(span -> span.getName().equals(spanName)).findFirst();
  }
}
