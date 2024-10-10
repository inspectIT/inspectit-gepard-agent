/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;

/**
 * Singleton to access the {@link OpenTelemetry} instance. We use this accessor, because according
 * to the documentation of {@link GlobalOpenTelemetry}, the get() method should only be called once
 * during the application.
 */
public class OpenTelemetryAccessor {

  private static OpenTelemetry openTelemetry;

  private OpenTelemetryAccessor() {}

  /**
   * @return the global {@link OpenTelemetry} instance
   */
  public static OpenTelemetry getOpenTelemetry() {
    return openTelemetry;
  }

  /**
   * Sets the global {@link OpenTelemetry} instance for inspectIT. This will allow us to create
   * traces or metrics. Should only be called once.
   *
   * @param otel the openTelemetry object
   */
  public static void setOpenTelemetry(OpenTelemetry otel) {
    openTelemetry = otel;
  }
}
