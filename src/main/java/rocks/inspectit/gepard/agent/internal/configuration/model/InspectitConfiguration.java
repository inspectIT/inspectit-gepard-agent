package rocks.inspectit.gepard.agent.internal.configuration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;

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
