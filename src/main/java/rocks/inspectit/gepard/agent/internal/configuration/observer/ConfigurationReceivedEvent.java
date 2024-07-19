package rocks.inspectit.gepard.agent.internal.configuration.observer;

import java.util.EventObject;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;

public class ConfigurationReceivedEvent extends EventObject {

  private final InspectitConfiguration configuration;

  public ConfigurationReceivedEvent(Object source, InspectitConfiguration configuration) {
    super(source);
    this.configuration = configuration;
  }

  public InspectitConfiguration getInstrumentationConfiguration() {
    return configuration;
  }
}
