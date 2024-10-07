/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import net.bytebuddy.description.method.MethodDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHook;

@ExtendWith(MockitoExtension.class)
class MethodHookGeneratorTest {

  @Mock private MethodDescription methodDescription;

  @Test
  void shouldCreateMethodHook() {
    when(methodDescription.getName()).thenReturn("method");

    MethodHook hook = MethodHookGenerator.createHook(methodDescription);

    assertNotNull(hook);
  }
}
