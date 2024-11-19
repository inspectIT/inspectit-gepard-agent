/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;

/**
 * Singleton to access the OpenTelemetry API. We use this accessor, because according to the
 * documentation of {@link GlobalOpenTelemetry}, the get() method should only be called once during
 * the application.
 */
public class OpenTelemetryAccessor {

  /** The instrumentation scope name we use for our spans and metrics */
  public static final String INSTRUMENTATION_SCOPE_NAME = "rocks.inspectit.gepard";

  /** Our global OpenTelemetry instance */
  private static OpenTelemetry openTelemetry;

  private OpenTelemetryAccessor() {}

  /**
   * Sets the global {@link OpenTelemetry} instance for inspectIT. This will allow us to create
   * traces or metrics. Should only be called once.
   *
   * @param otel the openTelemetry object
   */
  public static void setOpenTelemetry(OpenTelemetry otel) {
    openTelemetry = otel;
  }

  /**
   * @return the tracer to create spans
   */
  public static Tracer getTracer() {
    return openTelemetry.getTracer(INSTRUMENTATION_SCOPE_NAME);
  }

  /**
   * @return the meter to create metric instruments
   */
  public static Meter getMeter() {
    return openTelemetry.getMeter(INSTRUMENTATION_SCOPE_NAME);
  }
}
