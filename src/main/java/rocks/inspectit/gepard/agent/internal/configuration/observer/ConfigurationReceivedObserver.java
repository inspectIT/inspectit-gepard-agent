package rocks.inspectit.gepard.agent.internal.configuration.observer;

import java.util.EventListener;

public interface ConfigurationReceivedObserver extends EventListener {

  void handleConfiguration(ConfigurationReceivedEvent event);

  default void subscribeToConfigurationReceivedEvents() {
    ConfigurationReceivedSubject subject = ConfigurationReceivedSubject.getInstance();
    subject.addListener(this);
  }
}
