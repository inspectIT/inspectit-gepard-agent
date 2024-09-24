/* (C) 2024 */
package rocks.inspectit.gepard.agent.bootstrap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import io.opentelemetry.javaagent.bootstrap.InstrumentationHolder;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InspectitBootstrapManagerTest {

  @Mock private Instrumentation instrumentation;

  @Mock private JarFile jarFile;

  @BeforeEach
  void beforeEach() {
    InstrumentationHolder.setInstrumentation(instrumentation);
  }

  @Test
  void bootstrapClassesAreAvailable() throws IOException {
    InspectitBootstrapManager manager = Mockito.spy(InspectitBootstrapManager.class);

    Mockito.doReturn(jarFile).when(manager).copyJarFile(anyString(), anyString());
    manager.appendToBootstrapClassLoader();

    verify(instrumentation).appendToBootstrapClassLoaderSearch(jarFile);
  }
}
