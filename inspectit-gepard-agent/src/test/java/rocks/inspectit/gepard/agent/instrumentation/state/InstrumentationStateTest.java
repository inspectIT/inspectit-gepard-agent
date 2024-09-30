/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHookManager;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.ConfigurationResolver;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

@ExtendWith(MockitoExtension.class)
class InstrumentationStateTest {

  @Mock private ConfigurationResolver resolver;

  @Mock private MethodHookManager hookState;

  @Mock private ClassInstrumentationConfiguration configuration;

  private InstrumentationState instrumentationState;

  private static final Class<?> TEST_CLASS = InstrumentationStateTest.class;

  private static final InstrumentedType TEST_TYPE =
      new InstrumentedType(TEST_CLASS.getName(), TEST_CLASS.getClassLoader());

  @BeforeEach
  void beforeEach() {
    instrumentationState = InstrumentationState.create(resolver, hookState);
  }

  @Test
  void typeIsNotInstrumented() {
    boolean isActive = instrumentationState.isActive(TEST_TYPE);

    assertFalse(isActive);
  }

  @Test
  void typeIsNotInstrumentedWithConfiguration() {
    when(configuration.isActive()).thenReturn(false);

    instrumentationState.addInstrumentedType(TEST_TYPE, configuration);

    boolean isActive = instrumentationState.isActive(TEST_TYPE);

    assertFalse(isActive);
  }

  @Test
  void typeIsInstrumented() {
    when(configuration.isActive()).thenReturn(true);

    instrumentationState.addInstrumentedType(TEST_TYPE, configuration);

    boolean isActive = instrumentationState.isActive(TEST_TYPE);

    assertTrue(isActive);
  }

  @Test
  void typeIsDeinstrumented() {
    instrumentationState.addInstrumentedType(TEST_TYPE, configuration);
    instrumentationState.invalidateInstrumentedType(TEST_TYPE);

    boolean isActive = instrumentationState.isActive(TEST_TYPE);

    assertFalse(isActive);
  }
}
