package rocks.inspectit.gepard.agent.internal.configuration.model;

import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;

public class InspectitConfiguration {

  private InstrumentationConfiguration instrumentationConfiguration;

  public InstrumentationConfiguration getInstrumentation() {
    return instrumentationConfiguration;
  }
}
