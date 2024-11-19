/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.metrics;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.ObservableDoubleGauge;
import io.opentelemetry.api.metrics.ObservableDoubleMeasurement;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.otel.OpenTelemetryAccessor;

class MetricFactoryTest {

  // Interval to read and export metrics
  private static final Duration INTERVAL = Duration.ofMillis(500);

  @BeforeAll
  static void beforeAll() {
    GlobalOpenTelemetry.resetForTest();

    // Build our own OpenTelemetrySdk, so we don't use the NOOP implementations
    SdkMeterProvider meterProvider =
        SdkMeterProvider.builder()
            .registerMetricReader(
                PeriodicMetricReader.builder(LoggingMetricExporter.create())
                    .setInterval(INTERVAL)
                    .build())
            .build();
    OpenTelemetrySdk.builder().setMeterProvider(meterProvider).buildAndRegisterGlobal();
    OpenTelemetryAccessor.setOpenTelemetry(GlobalOpenTelemetry.get());
  }

  @Test
  void shouldRecordValueWithCallbackWhenCreatingGauge() throws InterruptedException {
    AtomicBoolean valueRecorded = new AtomicBoolean(false);

    Consumer<ObservableDoubleMeasurement> callback1 = (measurement) -> valueRecorded.set(true);
    Consumer<ObservableDoubleMeasurement> callback2 =
        (measurement) -> {
          throw new RuntimeException("Test exception");
        };

    ObservableDoubleGauge gauge1 =
        MetricFactory.createObservableDoubleGauge("test-gauge", callback1);
    ObservableDoubleGauge gauge2 =
        MetricFactory.createObservableDoubleGauge("fail-gauge", callback2);
    // Wait for MetricReader
    Thread.sleep(INTERVAL.toMillis() + 100);

    assertTrue(valueRecorded.get());

    gauge1.close();
    gauge2.close();
  }
}
