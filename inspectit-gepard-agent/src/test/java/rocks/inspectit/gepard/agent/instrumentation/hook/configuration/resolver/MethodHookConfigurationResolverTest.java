/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration.resolver;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.model.MethodHookConfiguration;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.InstrumentationRule;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

@ExtendWith(MockitoExtension.class)
class MethodHookConfigurationResolverTest {

  @Mock private MethodDescription methodDescription;

  @Mock private InstrumentationRule activeRule;

  @Mock private ClassInstrumentationConfiguration classConfiguration;

  private final MethodHookConfigurationResolver resolver = new MethodHookConfigurationResolver();

  private final String methodName = "method";

  private final ElementMatcher.Junction<MethodDescription> matcher = isMethod();

  @BeforeEach
  void beforeEach() {
    when(methodDescription.getName()).thenReturn(methodName);
    when(activeRule.getMethodMatcher()).thenReturn(matcher);
    when(classConfiguration.getActiveRules()).thenReturn(Set.of(activeRule));
  }

  @Test
  void shouldResolveConfigWithEnabledTracing() {
    RuleTracingConfiguration tracing = new RuleTracingConfiguration(true);

    when(methodDescription.isMethod()).thenReturn(true);
    when(activeRule.getTracing()).thenReturn(tracing);

    MethodHookConfiguration hookConfig = resolver.resolve(methodDescription, classConfiguration);
    boolean tracingEnabled = hookConfig.getTracing().getStartSpan();

    assertEquals(methodName, hookConfig.getMethodName());
    assertTrue(tracingEnabled);
  }

  @Test
  void shouldResolveConfigWithoutEnabledTracing() {
    RuleTracingConfiguration tracing = RuleTracingConfiguration.NO_TRACING;

    when(methodDescription.isMethod()).thenReturn(true);
    when(activeRule.getTracing()).thenReturn(tracing);

    MethodHookConfiguration hookConfig = resolver.resolve(methodDescription, classConfiguration);
    boolean tracingEnabled = hookConfig.getTracing().getStartSpan();

    assertEquals(methodName, hookConfig.getMethodName());
    assertFalse(tracingEnabled);
  }

  @Test
  void shouldResolveConfigWithoutEnabledTracingWhenNoRulesMatch() {
    when(methodDescription.isMethod()).thenReturn(false);

    MethodHookConfiguration hookConfig = resolver.resolve(methodDescription, classConfiguration);
    boolean tracingEnabled = hookConfig.getTracing().getStartSpan();

    assertEquals(methodName, hookConfig.getMethodName());
    assertFalse(tracingEnabled);
  }
}
