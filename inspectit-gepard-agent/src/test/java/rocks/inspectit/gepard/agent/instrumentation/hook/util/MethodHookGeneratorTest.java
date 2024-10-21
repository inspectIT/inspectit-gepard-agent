/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHook;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.MethodHookConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

@ExtendWith(MockitoExtension.class)
class MethodHookGeneratorTest {

  @Mock private MethodHookConfiguration hookConfiguration;

  @Test
  void shouldCreateMethodHook() {
    when(hookConfiguration.tracing()).thenReturn(RuleTracingConfiguration.NO_TRACING);

    MethodHook hook = MethodHookGenerator.createHook(hookConfiguration);

    assertNotNull(hook);
  }

  // TODO
}
