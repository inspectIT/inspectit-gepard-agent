/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.cache.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.lang.instrument.Instrumentation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.cache.PendingClassesCache;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;

@ExtendWith(MockitoExtension.class)
class ConfigurationReceiverTest {

  @Mock private Instrumentation instrumentation;

  @Test
  void receiverDoesFillCache() {
    Class<?>[] clazz = {getClass()};
    when(instrumentation.getAllLoadedClasses()).thenReturn(clazz);
    PendingClassesCache cache = new PendingClassesCache();
    ConfigurationReceiver receiver = ConfigurationReceiver.create(cache, instrumentation);
    ConfigurationReceivedEvent event =
        new ConfigurationReceivedEvent(this, new InspectitConfiguration());

    receiver.handleConfiguration(event);

    assertEquals(1, cache.getSize());
    verify(instrumentation, times(1)).getAllLoadedClasses();
  }
}
