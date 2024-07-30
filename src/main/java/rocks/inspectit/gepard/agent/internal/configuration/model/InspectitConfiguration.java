package rocks.inspectit.gepard.agent.internal.configuration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;

/** Model of an inspectit gepard configuration. */
public class InspectitConfiguration {

  public InspectitConfiguration() {
    this.instrumentationConfiguration = new InstrumentationConfiguration();
  }

  public InspectitConfiguration(InstrumentationConfiguration instrumentationConfiguration) {
    this.instrumentationConfiguration = instrumentationConfiguration;
  }

  @JsonProperty("instrumentationConfiguration")
  private InstrumentationConfiguration instrumentationConfiguration;

  public InstrumentationConfiguration getInstrumentation() {
    return instrumentationConfiguration;
  }
}
