package rocks.inspectit.gepard.agent.internal.instrumentation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InstrumentationStateTest {

  private static final Class<?> TEST_CLASS = InstrumentationStateTest.class;

  private static final InstrumentedType TEST_TYPE =
      new InstrumentedType(TEST_CLASS.getName(), TEST_CLASS.getClassLoader());

  @Test
  void classIsNotInstrumented() {
    InstrumentationState state = InstrumentationState.create();

    boolean isInstrumented = state.isInstrumented(TEST_CLASS);

    assertFalse(isInstrumented);
  }

  @Test
  void classIsInstrumented() {
    InstrumentationState state = InstrumentationState.create();

    state.addInstrumentedType(TEST_TYPE);
    boolean isInstrumented = state.isInstrumented(TEST_CLASS);

    assertTrue(isInstrumented);
  }

  @Test
  void classIsDeinstrumented() {
    InstrumentationState state = InstrumentationState.create();

    state.addInstrumentedType(TEST_TYPE);
    state.invalidateInstrumentedType(TEST_TYPE);
    boolean isInstrumented = state.isInstrumented(TEST_CLASS);

    assertFalse(isInstrumented);
  }

  @Test
  void typeIsNotInstrumented() {
    InstrumentationState state = InstrumentationState.create();

    boolean isInstrumented = state.isInstrumented(TEST_TYPE);

    assertFalse(isInstrumented);
  }

  @Test
  void typeIsInstrumented() {
    InstrumentationState state = InstrumentationState.create();

    state.addInstrumentedType(TEST_TYPE);
    boolean isInstrumented = state.isInstrumented(TEST_TYPE);

    assertTrue(isInstrumented);
  }

  @Test
  void typeIsDeinstrumented() {
    InstrumentationState state = InstrumentationState.create();

    state.addInstrumentedType(TEST_TYPE);
    state.invalidateInstrumentedType(TEST_TYPE);
    boolean isInstrumented = state.isInstrumented(TEST_TYPE);

    assertFalse(isInstrumented);
  }
}
