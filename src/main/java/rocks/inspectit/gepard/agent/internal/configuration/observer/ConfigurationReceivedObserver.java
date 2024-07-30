package rocks.inspectit.gepard.agent.internal.configuration.observer;

import java.util.EventListener;

/** Interface for classes, which should listen to the {@link ConfigurationReceivedEvent} */
public interface ConfigurationReceivedObserver extends EventListener {

  /** Processes the {@link ConfigurationReceivedEvent} */
  void handleConfiguration(ConfigurationReceivedEvent event);

  /**
   * Subscribes this instance to {@link ConfigurationReceivedEvent}s. Should be called once after
   * initialization.
   */
  default void subscribeToConfigurationReceivedEvents() {
    ConfigurationReceivedSubject subject = ConfigurationReceivedSubject.getInstance();
    subject.addListener(this);
  }
}
