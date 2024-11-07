/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHook;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.model.MethodHookConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

@ExtendWith(MockitoExtension.class)
class MethodHookFactoryTest {

  @Mock private MethodHookConfiguration hookConfiguration;

  @Test
  void shouldCreateMethodHookWithoutTracing() {
    String methodName = "method";
    when(hookConfiguration.getMethodName()).thenReturn(methodName);
    when(hookConfiguration.getTracing()).thenReturn(RuleTracingConfiguration.NO_TRACING);

    MethodHook hook = MethodHookFactory.createHook(hookConfiguration);
    boolean tracingEnabled = hook.getConfiguration().getTracing().isStartSpan();

    assertNotNull(hook);
    assertEquals(methodName, hook.getConfiguration().getMethodName());
    assertFalse(tracingEnabled);
  }

  @Test
  void shouldCreateMethodHookWithTracing() {
    String methodName = "method";
    RuleTracingConfiguration tracing = new RuleTracingConfiguration(true);
    when(hookConfiguration.getMethodName()).thenReturn(methodName);
    when(hookConfiguration.getTracing()).thenReturn(tracing);

    MethodHook hook = MethodHookFactory.createHook(hookConfiguration);
    boolean tracingEnabled = hook.getConfiguration().getTracing().isStartSpan();

    assertNotNull(hook);
    assertEquals(methodName, hook.getConfiguration().getMethodName());
    assertTrue(tracingEnabled);
  }
}
