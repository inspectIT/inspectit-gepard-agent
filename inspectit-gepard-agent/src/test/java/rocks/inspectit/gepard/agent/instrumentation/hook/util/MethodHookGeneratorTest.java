/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.util;

import static org.junit.jupiter.api.Assertions.*;
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
  void shouldCreateMethodHookWithoutTracing() {
    String methodName = "method";
    when(hookConfiguration.methodName()).thenReturn(methodName);
    when(hookConfiguration.tracing()).thenReturn(RuleTracingConfiguration.NO_TRACING);

    MethodHook hook = MethodHookGenerator.createHook(hookConfiguration);
    boolean tracingEnabled = hook.getConfiguration().tracing().getStartSpan();

    assertNotNull(hook);
    assertEquals(methodName, hook.getConfiguration().methodName());
    assertFalse(tracingEnabled);
  }

  @Test
  void shouldCreateMethodHookWithTracing() {
    String methodName = "method";
    RuleTracingConfiguration tracing = new RuleTracingConfiguration(true);
    when(hookConfiguration.methodName()).thenReturn(methodName);
    when(hookConfiguration.tracing()).thenReturn(tracing);

    MethodHook hook = MethodHookGenerator.createHook(hookConfiguration);
    boolean tracingEnabled = hook.getConfiguration().tracing().getStartSpan();

    assertNotNull(hook);
    assertEquals(methodName, hook.getConfiguration().methodName());
    assertTrue(tracingEnabled);
  }
}
