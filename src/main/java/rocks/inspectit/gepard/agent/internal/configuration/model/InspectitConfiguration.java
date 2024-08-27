package rocks.inspectit.gepard.agent.internal.configuration.model;

import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;

/** Model of an inspectit gepard configuration. */
public class InspectitConfiguration {

  private InstrumentationConfiguration instrumentation;

  public InspectitConfiguration() {
    this.instrumentation = new InstrumentationConfiguration();
  }

  public InspectitConfiguration(InstrumentationConfiguration instrumentation) {
    this.instrumentation = instrumentation;
  }

  public InstrumentationConfiguration getInstrumentation() {
    return instrumentation;
  }
}
