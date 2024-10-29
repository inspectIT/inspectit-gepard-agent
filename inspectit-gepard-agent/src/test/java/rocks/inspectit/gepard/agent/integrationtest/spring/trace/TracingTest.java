/* (C) 2024 */
package rocks.inspectit.gepard.agent.integrationtest.spring.trace;

import static org.junit.jupiter.api.Assertions.*;
import static rocks.inspectit.gepard.agent.internal.otel.OpenTelemetryAccessor.INSTRUMENTATION_SCOPE_NAME;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.integrationtest.spring.SpringTestBase;

/** Should check, if traces are received in our tracing backend according to our configuration. */
class TracingTest extends SpringTestBase {

  private static final String parentSpanName = "WebController.greeting";

  private static final String childSpanName = "WebController.withSpan";

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
  void shouldNotSendSpansToBackendWhenScopesAreInactive() throws Exception {
    configurationServerMock.configServerSetup(configDir + "empty-config.json");
    startTarget("/opentelemetry-extensions.jar");
    awaitInstrumentationUpdate(1);

    sendRequestToTarget("/greeting");
    Collection<ExportTraceServiceRequest> traces = waitForTraces();

    assertNoSpans(traces, parentSpanName, childSpanName);
  }

  /**
   * This method asserts that spans with the given names exist, they appear in the same trace and
   * that the child's {@link Span#getParentSpanId()} equals the parent's {@link Span#getSpanId()}.
   *
   * @param traces the collection of traces
   * @param parentSpanName the name of the parent span
   * @param childSpanName the name of the child span
   */
  private void assertSpans(
      Collection<ExportTraceServiceRequest> traces, String parentSpanName, String childSpanName) {
    List<Span> spans = getSpans(traces);

    Optional<Span> parentSpan = findSpan(spans, parentSpanName);
    Optional<Span> childSpan = findSpan(spans, childSpanName);
    boolean spansExist = parentSpan.isPresent() && childSpan.isPresent();

    assertTrue(spansExist);

    // We cannot compare the ByteStrings directly, because they are different objects
    String parentId = parentSpan.get().getSpanId().toStringUtf8();
    String childParentId = childSpan.get().getParentSpanId().toStringUtf8();
    String parentTraceId = parentSpan.get().getTraceId().toStringUtf8();
    String childTraceId = childSpan.get().getTraceId().toStringUtf8();

    assertEquals(parentId, childParentId);
    assertEquals(parentTraceId, childTraceId);
  }

  /**
   * This method asserts that no spans from inspectIT with the given names exist.
   *
   * @param traces the collection of traces
   * @param parentSpanName the name of the parent span
   * @param childSpanName the name of the child span
   */
  private void assertNoSpans(
      Collection<ExportTraceServiceRequest> traces, String parentSpanName, String childSpanName) {
    List<Span> spans = getInspectItSpans(traces);

    Optional<Span> parentSpan = findSpan(spans, parentSpanName);
    Optional<Span> childSpan = findSpan(spans, childSpanName);
    boolean noSpanExist = parentSpan.isEmpty() && childSpan.isEmpty();

    assertTrue(noSpanExist);
  }

  /**
   * Maps the provided traces to a collection of spans.
   *
   * @param traces the collection of traces
   * @return the collection of span within the provided traces
   */
  private List<Span> getSpans(Collection<ExportTraceServiceRequest> traces) {
    Predicate<String> scopeFilter = (scopeName) -> true;
    return getSpans(traces, scopeFilter);
  }

  /**
   * Maps the provided traces to a collection of spans, which were created by inspectIT.
   *
   * @param traces the collection of traces
   * @return the collection of span within the provided traces
   */
  private List<Span> getInspectItSpans(Collection<ExportTraceServiceRequest> traces) {
    Predicate<String> scopeFilter = (scopeName) -> scopeName.equals(INSTRUMENTATION_SCOPE_NAME);
    return getSpans(traces, scopeFilter);
  }

  /**
   * Maps the provided traces to a collection of spans filtered by a specific instrumentation scope.
   *
   * @param traces the collection of traces
   * @param scopeFilter the filter for the instrumentation scope
   * @return the filtered collection of span within the provided traces
   */
  private List<Span> getSpans(
      Collection<ExportTraceServiceRequest> traces, Predicate<String> scopeFilter) {
    return traces.stream()
        .flatMap(
            trace ->
                trace.getResourceSpansList().stream()
                    .flatMap(
                        resourceSpans ->
                            filterAndExtractSpans(resourceSpans.getScopeSpansList(), scopeFilter)))
        .toList();
  }

  /**
   * Filters and extracts all {@link Span}s from {@link ScopeSpans}.
   *
   * @param scopeSpansList the list of {@link ScopeSpans} from {@link ResourceSpans}
   * @param scopeFilter the filter for the instrumentation scope
   * @return the collection of filtered spans as stream
   */
  private Stream<Span> filterAndExtractSpans(
      List<ScopeSpans> scopeSpansList, Predicate<String> scopeFilter) {
    return scopeSpansList.stream()
        .filter(scopeSpans -> scopeFilter.test(scopeSpans.getScope().getName()))
        .flatMap(scopeSpans -> scopeSpans.getSpansList().stream());
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
