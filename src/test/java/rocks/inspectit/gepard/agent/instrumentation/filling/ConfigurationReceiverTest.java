package rocks.inspectit.gepard.agent.instrumentation.filling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.lang.instrument.Instrumentation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.PendingClassesCache;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;

@ExtendWith(MockitoExtension.class)
class ConfigurationReceiverTest {

  @Mock private Instrumentation instrumentation;

  @Test
  void receiverDoesFillCache() {
    Class<?>[] clazz = {getClass()};
    when(instrumentation.getAllLoadedClasses()).thenReturn(clazz);
    PendingClassesCache cache = new PendingClassesCache();
    ConfigurationReceiver receiver = new ConfigurationReceiver(cache, instrumentation);
    ConfigurationReceivedEvent event =
        new ConfigurationReceivedEvent(this, new InspectitConfiguration());

    receiver.handleConfiguration(event);

    assertEquals(1, cache.getSize());
    verify(instrumentation, times(1)).getAllLoadedClasses();
  }
}
