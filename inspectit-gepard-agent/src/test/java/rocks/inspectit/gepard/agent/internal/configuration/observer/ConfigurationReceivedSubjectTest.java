/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.configuration.observer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;

class ConfigurationReceivedSubjectTest {

  @Test
  void listenersAreNotified() {
    AtomicBoolean wasNotified = new AtomicBoolean(false);

    ConfigurationReceivedObserver observer = (event) -> wasNotified.set(true);
    observer.subscribeToConfigurationReceivedEvents();

    ConfigurationReceivedSubject subject = ConfigurationReceivedSubject.getInstance();
    subject.notifyObservers(new InspectitConfiguration());

    assertTrue(wasNotified.get());
  }
}
