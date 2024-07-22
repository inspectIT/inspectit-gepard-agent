package rocks.inspectit.gepard.agent.internal.configuration.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;

/**
 * Observer pattern subject, which notifies all registered listeners about {@link
 * ConfigurationReceivedEvent}s.
 */
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

  /**
   * Notifies all listeners about a newly received configuration.
   *
   * @param configuration the received configuration
   */
  public void notifyListeners(InspectitConfiguration configuration) {
    ConfigurationReceivedEvent event = new ConfigurationReceivedEvent(this, configuration);
    for (ConfigurationReceivedObserver listener : listeners) {
      listener.handleConfiguration(event);
    }
  }
}
