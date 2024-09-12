package rocks.inspectit.gepard.agent.internal.instrumentation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

@ExtendWith(MockitoExtension.class)
class InstrumentationStateTest {

  @Mock private ClassInstrumentationConfiguration configuration;

  private static final Class<?> TEST_CLASS = InstrumentationStateTest.class;

  private static final InstrumentedType TEST_TYPE =
      new InstrumentedType(TEST_CLASS.getName(), TEST_CLASS.getClassLoader());

  @Test
  void typeIsNotInstrumented() {
    InstrumentationState state = InstrumentationState.create();

    boolean isInstrumented = state.isInstrumented(TEST_TYPE);

    assertFalse(isInstrumented);
  }

  @Test
  void typeIsNotInstrumentedWithConfiguration() {
    when(configuration.isActive()).thenReturn(false);

    InstrumentationState state = InstrumentationState.create();
    state.addInstrumentedType(TEST_TYPE, configuration);

    boolean isInstrumented = state.isInstrumented(TEST_TYPE);

    assertFalse(isInstrumented);
  }

  @Test
  void typeIsInstrumented() {
    when(configuration.isActive()).thenReturn(true);

    InstrumentationState state = InstrumentationState.create();
    state.addInstrumentedType(TEST_TYPE, configuration);

    boolean isInstrumented = state.isInstrumented(TEST_TYPE);

    assertTrue(isInstrumented);
  }

  @Test
  void typeIsDeinstrumented() {
    InstrumentationState state = InstrumentationState.create();
    state.addInstrumentedType(TEST_TYPE, configuration);
    state.invalidateInstrumentedType(TEST_TYPE);

    boolean isInstrumented = state.isInstrumented(TEST_TYPE);

    assertFalse(isInstrumented);
  }
}
