/* (C) 2024 */
package rocks.inspectit.gepard.agent.integrationtest.spring.trace;

import static org.junit.jupiter.api.Assertions.assertFalse;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.integrationtest.spring.SpringTestBase;

/** Should check, if traces are received in our tracing backend, when configured */
class TracingTest extends SpringTestBase {

  @Test
  void shouldSendSpansToBackendWhenScopesAreActive() throws Exception {
    configurationServerMock.configServerSetup(configDir + "simple-scope.json");
    startTarget("/opentelemetry-extensions.jar");
    awaitInstrumentationUpdate(1);

    sendRequestToTarget("/greeting");

    Collection<ExportTraceServiceRequest> traces = waitForTraces();

    // TODO check for our spans
    assertFalse(traces.isEmpty());
  }

  @Test
  void shouldNotSendSpansToBackendWhenNoScopesAreActive() throws Exception {
    configurationServerMock.configServerSetup(configDir + "empty-configuration.json");
    startTarget("/opentelemetry-extensions.jar");
    awaitInstrumentationUpdate(1);

    sendRequestToTarget("/greeting");

    Collection<ExportTraceServiceRequest> traces = waitForTraces();

    // TODO check for our spans
    assertFalse(traces.isEmpty());
  }
}
