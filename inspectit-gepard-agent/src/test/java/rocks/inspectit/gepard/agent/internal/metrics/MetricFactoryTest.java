/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.metrics;

import static org.mockito.Mockito.*;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.ObservableDoubleGauge;
import io.opentelemetry.api.metrics.ObservableDoubleMeasurement;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import java.time.Duration;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.internal.otel.OpenTelemetryAccessor;

@ExtendWith(MockitoExtension.class)
class MetricFactoryTest {

  @Mock private Consumer<ObservableDoubleMeasurement> mockCallback;

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
  void shouldRecordValueWithCallbackWhenCreatingGauge() {
    Consumer<ObservableDoubleMeasurement> errorCallback =
        (measurement) -> {
          throw new RuntimeException("Test exception");
        };

    ObservableDoubleGauge gauge1 =
        MetricFactory.createObservableDoubleGauge("test-gauge", mockCallback);
    ObservableDoubleGauge gauge2 =
        MetricFactory.createObservableDoubleGauge("fail-gauge", errorCallback);

    verify(mockCallback, timeout(INTERVAL.toMillis() + 100)).accept(any());

    gauge1.close();
    gauge2.close();
  }
}
