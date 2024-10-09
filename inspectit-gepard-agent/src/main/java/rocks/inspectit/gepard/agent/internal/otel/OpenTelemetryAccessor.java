/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import java.util.Objects;

/**
 * Singleton to access the {@link OpenTelemetry} instance. For now, we use the OTel SDK provided by
 * the Java Agent. We use this accessor, because according to the documentation of {@link
 * GlobalOpenTelemetry}, the method should only be called once during the application.
 */
public class OpenTelemetryAccessor {

  private static OpenTelemetry openTelemetry;

  private OpenTelemetryAccessor() {}

  /**
   * Get the global {@link OpenTelemetry} instance. The global instance should have been set by the
   * Java agent beforehand.
   *
   * @return the global {@link OpenTelemetry} instance
   */
  public static OpenTelemetry getOpenTelemetry() {
    if (Objects.isNull(openTelemetry)) {
      openTelemetry = GlobalOpenTelemetry.get();
    }
    return openTelemetry;
  }
}
