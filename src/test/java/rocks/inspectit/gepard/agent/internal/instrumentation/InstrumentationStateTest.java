package rocks.inspectit.gepard.agent.internal.instrumentation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InstrumentationStateTest {

  private static final Class<?> TEST_CLASS = InstrumentationStateTest.class;

  @Test
  void typeIsNotInstrumented() {
    InstrumentationState state = InstrumentationState.create();

    boolean isInstrumented = state.isInstrumented(TEST_CLASS);

    assertFalse(isInstrumented);
  }

  @Test
  void typeIsInstrumented() {
    InstrumentationState state = InstrumentationState.create();

    state.addInstrumentation(TEST_CLASS);
    boolean isInstrumented = state.isInstrumented(TEST_CLASS);

    assertTrue(isInstrumented);
  }

  @Test
  void typeIsDeinstrumented() {
    InstrumentationState state = InstrumentationState.create();

    state.addInstrumentation(TEST_CLASS);
    state.invalidateInstrumentation(TEST_CLASS);
    boolean isInstrumented = state.isInstrumented(TEST_CLASS);

    assertFalse(isInstrumented);
  }
}
