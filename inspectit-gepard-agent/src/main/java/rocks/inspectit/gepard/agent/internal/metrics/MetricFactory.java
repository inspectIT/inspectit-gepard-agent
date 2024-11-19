/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.metrics;

import io.opentelemetry.api.metrics.ObservableDoubleGauge;
import io.opentelemetry.api.metrics.ObservableDoubleMeasurement;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import java.util.function.Consumer;
import rocks.inspectit.gepard.agent.internal.otel.OpenTelemetryAccessor;

/** Creates new OpenTelemetry instruments (metrics). */
public class MetricFactory {

  private MetricFactory() {}

  /**
   * Creates an observable gauge metric. This gauge will record a measurement via calling the
   * callback function everytime it is observed by the {@link PeriodicMetricReader}.
   *
   * @param name the name of the gauge
   * @param callback the callback function to record a measurement
   * @return the created gauge
   */
  public static ObservableDoubleGauge createObservableDoubleGauge(
      String name, Consumer<ObservableDoubleMeasurement> callback) {
    return OpenTelemetryAccessor.getMeter().gaugeBuilder(name).buildWithCallback(callback);
  }
}
