package rocks.inspectit.gepard.agent.internal.configuration.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;

public class ConfigurationReceivedSubject {

  private static ConfigurationReceivedSubject instance;

  private final List<ConfigurationReceivedObserver> listeners;

  private ConfigurationReceivedSubject() {
    this.listeners = new ArrayList<>();
  }

  public static ConfigurationReceivedSubject getInstance() {
    if (Objects.isNull(instance)) instance = new ConfigurationReceivedSubject();
    return instance;
  }

  public void addListener(ConfigurationReceivedObserver listener) {
    listeners.add(listener);
  }

  public void notifyListeners(InspectitConfiguration configuration) {
    ConfigurationReceivedEvent event = new ConfigurationReceivedEvent(this, configuration);
    for (ConfigurationReceivedObserver listener : listeners) {
      listener.handleConfiguration(event);
    }
  }
}
