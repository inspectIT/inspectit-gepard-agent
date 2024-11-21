/* (C) 2024 */
package rocks.inspectit.gepard.agent.integrationtest.spring.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static rocks.inspectit.gepard.agent.internal.otel.OpenTelemetryAccessor.INSTRUMENTATION_SCOPE_NAME;

import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.metrics.v1.ScopeMetrics;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.integrationtest.spring.SpringTestBase;

/**
 * Note: Currently self-monitoring is always enabled. <br>
 * Later, this should be configurable. Thus, we will have to provide a proper self-monitoring
 * configuration.
 */
class SelfMonitoringIntTest extends SpringTestBase {

  @Override
  protected Map<String, String> getExtraEnv() {
    // Set metric export interval to 10s (default 60s)
    // Might have to be adjusted
    return Map.of("OTEL_METRIC_EXPORT_INTERVAL", "10000");
  }

  @Test
  void shouldRecordMetricForInstrumentedClassWhenScopesAreActive() throws Exception {
    configurationServerMock.configServerSetup(configDir + "multiple-scopes.json");
    startTarget("/opentelemetry-extensions.jar");
    awaitInstrumentationUpdate(1);

    Collection<ExportMetricsServiceRequest> exportedMetrics = waitForMetrics();

    assertGaugeMetric(exportedMetrics, "inspectit.self.instrumented-classes", 2.0);
  }

  @Test
  void shouldNotRecordMetricWhenScopesAreInactive() throws Exception {
    configurationServerMock.configServerSetup(configDir + "empty-config.json");
    startTarget("/opentelemetry-extensions.jar");
    awaitInstrumentationUpdate(1);

    Collection<ExportMetricsServiceRequest> exportedMetrics = waitForMetrics();

    assertGaugeMetric(exportedMetrics, "inspectit.self.instrumented-classes", 0.0);
  }

  /**
   * This method asserts that a metric with the given name exists and that the last recorded value
   * of this metric equals the expected value. The metric has to be a gauge.
   *
   * @param exportedMetrics the collection of exported metrics
   * @param name the metric name
   * @param expectedValue the expected value
   */
  private void assertGaugeMetric(
      Collection<ExportMetricsServiceRequest> exportedMetrics, String name, Double expectedValue) {
    List<Metric> metrics = getMetrics(exportedMetrics);
    Optional<Metric> maybeMetric = findMetric(metrics, name);

    assertTrue(maybeMetric.isPresent());

    List<NumberDataPoint> dataPoints = maybeMetric.get().getGauge().getDataPointsList();
    double lastValue = dataPoints.get(dataPoints.size() - 1).getAsDouble();

    assertEquals(expectedValue, lastValue);
  }

  /**
   * Maps the provided {@link ExportMetricsServiceRequest}s to simple {@link Metric} objects.
   *
   * @param exportedMetrics the exported metrics
   * @return the collection of metric objects
   */
  private List<Metric> getMetrics(Collection<ExportMetricsServiceRequest> exportedMetrics) {
    return exportedMetrics.stream()
        .flatMap(
            exportedMetric ->
                exportedMetric.getResourceMetricsList().stream()
                    .flatMap(
                        resourceMetrics ->
                            filterAndExtractMetrics(resourceMetrics.getScopeMetricsList())))
        .toList();
  }

  /**
   * Filters and extracts all {@link Metric}s from {@link ScopeMetrics}. We filter for metrics
   * created by inspectIT.
   *
   * @param scopeMetricsList the list of {@link ScopeMetrics} from {@link ResourceMetrics}
   * @return the collection of filtered metrics as stream
   */
  private Stream<Metric> filterAndExtractMetrics(List<ScopeMetrics> scopeMetricsList) {
    return scopeMetricsList.stream()
        .filter(
            scopeMetrics -> scopeMetrics.getScope().getName().equals(INSTRUMENTATION_SCOPE_NAME))
        .flatMap(scopeMetric -> scopeMetric.getMetricsList().stream());
  }

  /**
   * Tries to find the provided metric name in list of metrics.
   *
   * @param metrics the list of metrics
   * @param metricName the metric name to look for
   * @return the found metric or empty
   */
  private Optional<Metric> findMetric(List<Metric> metrics, String metricName) {
    return metrics.stream().filter(metric -> metric.getName().equals(metricName)).findFirst();
  }
}
