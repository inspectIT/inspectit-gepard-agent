/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.instrumentation.rules;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.InstrumentationRule;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

class InstrumentationRuleTest {

  @Test
  void shouldEqualOtherType() {
    InstrumentationRule rule1 =
        new InstrumentationRule(
            "rule", Collections.emptySet(), isMethod(), RuleTracingConfiguration.NO_TRACING);
    InstrumentationRule rule2 =
        new InstrumentationRule(
            "rule", Collections.emptySet(), isMethod(), RuleTracingConfiguration.NO_TRACING);

    assertEquals(rule1, rule2);
    assertEquals(rule1.hashCode(), rule2.hashCode());
  }

  @Test
  void shouldNotEqualOtherType() {
    InstrumentationRule rule1 =
        new InstrumentationRule(
            "rule1", Collections.emptySet(), isMethod(), RuleTracingConfiguration.NO_TRACING);
    InstrumentationRule rule2 =
        new InstrumentationRule(
            "rule2", Collections.emptySet(), isMethod(), RuleTracingConfiguration.NO_TRACING);

    assertNotEquals(rule1, rule2);
    assertNotEquals(rule1.hashCode(), rule2.hashCode());
  }
}
