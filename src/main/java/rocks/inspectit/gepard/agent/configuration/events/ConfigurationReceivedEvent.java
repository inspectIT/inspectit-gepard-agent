package rocks.inspectit.gepard.agent.configuration.events;

import java.util.EventObject;
import rocks.inspectit.gepard.agent.configuration.model.InstrumentationRequest;

public class ConfigurationReceivedEvent extends EventObject {
  private final InstrumentationRequest instrumentationRequest;

  public ConfigurationReceivedEvent(Object source, InstrumentationRequest configuration) {
    super(source);
    this.instrumentationRequest = configuration;
  }

  public InstrumentationRequest getInstrumentationConfiguration() {
    return instrumentationRequest;
  }
}
