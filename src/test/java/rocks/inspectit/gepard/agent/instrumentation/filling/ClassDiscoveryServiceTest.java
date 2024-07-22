package rocks.inspectit.gepard.agent.instrumentation.filling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.PendingClassesCache;

import java.lang.instrument.Instrumentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassDiscoveryServiceTest {

    @Mock
    private Instrumentation instrumentation;

    @Test
    void discoveryDoesFillCache() {
        Class<?>[] clazz = {getClass()};
        when(instrumentation.getAllLoadedClasses()).thenReturn(clazz);
        PendingClassesCache cache = new PendingClassesCache();
        ClassDiscoveryService service = new ClassDiscoveryService(cache, instrumentation);

        service.discoverClasses();

        assertEquals(1, cache.getSize());
        verify(instrumentation, times(1)).getAllLoadedClasses();
    }
}
