package rocks.inspectit.gepard.agent.configuration;

import java.util.ArrayList;
import java.util.List;
import rocks.inspectit.gepard.agent.configuration.events.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.configuration.events.ConfigurationReceivedListener;
import rocks.inspectit.gepard.agent.configuration.model.InstrumentationRequest;

public class ConfigurationSubject {

  private final List<ConfigurationReceivedListener> listeners = new ArrayList<>();

  public void addListener(ConfigurationReceivedListener listener) {
    listeners.add(listener);
  }

  public void removeListener(ConfigurationReceivedListener listener) {
    listeners.remove(listener);
  }

  public void notifyListeners(InstrumentationRequest configuration) {
    ConfigurationReceivedEvent event = new ConfigurationReceivedEvent(this, configuration);
    for (ConfigurationReceivedListener listener : listeners) {
      listener.handleConfiguration(event);
    }
  }
}
