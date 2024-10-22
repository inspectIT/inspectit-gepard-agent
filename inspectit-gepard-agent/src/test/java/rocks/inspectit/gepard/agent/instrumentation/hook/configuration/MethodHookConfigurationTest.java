/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

class MethodHookConfigurationTest {

  @Test
  void shouldEqualOtherType() {
    MethodHookConfiguration config1 =
        new MethodHookConfiguration("method", RuleTracingConfiguration.NO_TRACING);
    MethodHookConfiguration config2 =
        new MethodHookConfiguration("method", RuleTracingConfiguration.NO_TRACING);

    assertEquals(config1, config2);
    assertEquals(config1.hashCode(), config2.hashCode());
  }

  @Test
  void shouldNotEqualOtherType() {
    MethodHookConfiguration config1 =
        new MethodHookConfiguration("method1", RuleTracingConfiguration.NO_TRACING);
    MethodHookConfiguration config2 =
        new MethodHookConfiguration("method2", RuleTracingConfiguration.NO_TRACING);

    assertNotEquals(config1, config2);
    assertNotEquals(config1.hashCode(), config2.hashCode());
  }
}
