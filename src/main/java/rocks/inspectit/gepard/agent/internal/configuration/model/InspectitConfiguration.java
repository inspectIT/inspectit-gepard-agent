package rocks.inspectit.gepard.agent.internal.configuration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;

import java.io.Serial;
import java.io.Serializable;

/** Model of an inspectit gepard configuration. */
public class InspectitConfiguration {

  public InspectitConfiguration() {
    this.instrumentationConfiguration = new InstrumentationConfiguration();
  }

  @JsonProperty("instrumentationConfiguration")
  private InstrumentationConfiguration instrumentationConfiguration;

  public InstrumentationConfiguration getInstrumentation() {
    return instrumentationConfiguration;
  }
}
