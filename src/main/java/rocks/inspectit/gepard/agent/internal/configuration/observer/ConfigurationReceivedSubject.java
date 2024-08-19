package rocks.inspectit.gepard.agent.internal.configuration.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;

/**
 * Observer pattern subject, which notifies all registered observers about {@link
 * ConfigurationReceivedEvent}s.
 */
public class ConfigurationReceivedSubject {

  private static ConfigurationReceivedSubject instance;

  private final List<ConfigurationReceivedObserver> observers;

  private ConfigurationReceivedSubject() {
    this.observers = new ArrayList<>();
  }

  public static ConfigurationReceivedSubject getInstance() {
    if (Objects.isNull(instance)) instance = new ConfigurationReceivedSubject();
    return instance;
  }

  public void addObserver(ConfigurationReceivedObserver observer) {
    observers.add(observer);
  }

  public void removeObserver(ConfigurationReceivedObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies all observers about a newly received configuration.
   *
   * @param configuration the received configuration
   */
  public void notifyObservers(InspectitConfiguration configuration) {
    ConfigurationReceivedEvent event = new ConfigurationReceivedEvent(this, configuration);
    for (ConfigurationReceivedObserver observer : observers) {
      observer.handleConfiguration(event);
    }
  }
}
