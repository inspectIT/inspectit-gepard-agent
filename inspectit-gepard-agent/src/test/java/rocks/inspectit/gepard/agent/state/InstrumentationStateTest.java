/* (C) 2024 */
package rocks.inspectit.gepard.agent.state;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

@ExtendWith(MockitoExtension.class)
class InstrumentationStateTest {

  @Mock private ConfigurationResolver resolver;

  @Mock private ClassInstrumentationConfiguration configuration;

  private InstrumentationState state;

  private static final Class<?> TEST_CLASS = InstrumentationStateTest.class;

  private static final InstrumentedType TEST_TYPE =
      new InstrumentedType(TEST_CLASS.getName(), TEST_CLASS.getClassLoader());

  @BeforeEach
  void beforeEach() {
    state = InstrumentationState.create(resolver);
  }

  @Test
  void typeIsNotInstrumented() {
    boolean isActive = state.isActive(TEST_TYPE);

    assertFalse(isActive);
  }

  @Test
  void typeIsNotInstrumentedWithConfiguration() {
    when(configuration.isActive()).thenReturn(false);

    state.addInstrumentedType(TEST_TYPE, configuration);

    boolean isActive = state.isActive(TEST_TYPE);

    assertFalse(isActive);
  }

  @Test
  void typeIsInstrumented() {
    when(configuration.isActive()).thenReturn(true);

    state.addInstrumentedType(TEST_TYPE, configuration);

    boolean isActive = state.isActive(TEST_TYPE);

    assertTrue(isActive);
  }

  @Test
  void typeIsDeinstrumented() {
    state.addInstrumentedType(TEST_TYPE, configuration);
    state.invalidateInstrumentedType(TEST_TYPE);

    boolean isActive = state.isActive(TEST_TYPE);

    assertFalse(isActive);
  }
}
