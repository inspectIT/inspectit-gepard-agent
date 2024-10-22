/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import net.bytebuddy.description.type.TypeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHookManager;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.resolver.ConfigurationResolver;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

@ExtendWith(MockitoExtension.class)
class InstrumentationStateTest {

  @Mock private ConfigurationResolver resolver;

  @Mock private MethodHookManager methodHookManager;

  @Mock private ClassInstrumentationConfiguration configuration;

  private InstrumentationState instrumentationState;

  private static final Class<?> TEST_CLASS = InstrumentationStateTest.class;

  private static final TypeDescription TYPE_DESCRIPTION =
      TypeDescription.ForLoadedType.of(TEST_CLASS);

  private static final InstrumentedType TEST_TYPE =
      new InstrumentedType(TYPE_DESCRIPTION, TEST_CLASS.getClassLoader());

  @BeforeEach
  void beforeEach() {
    instrumentationState = InstrumentationState.create(resolver, methodHookManager);
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

  @Test
  void shouldNotRetransformWhenConfigurationEquals() {
    when(resolver.getClassInstrumentationConfiguration(TEST_TYPE)).thenReturn(configuration);
    instrumentationState.addInstrumentedType(TEST_TYPE, configuration);

    boolean shouldRetransform = instrumentationState.shouldRetransform(TEST_CLASS);

    assertFalse(shouldRetransform);
  }

  @Test
  void shouldNotRetransformWhenConfigurationDiffer() {
    ClassInstrumentationConfiguration oldConfig = mock(ClassInstrumentationConfiguration.class);
    when(resolver.getClassInstrumentationConfiguration(TEST_TYPE)).thenReturn(configuration);
    instrumentationState.addInstrumentedType(TEST_TYPE, oldConfig);

    boolean shouldRetransform = instrumentationState.shouldRetransform(TEST_CLASS);

    assertTrue(shouldRetransform);
  }

  @Test
  void shouldRetransformWhenConfigurationIsActive() {
    when(resolver.getClassInstrumentationConfiguration(TEST_TYPE)).thenReturn(configuration);
    when(configuration.isActive()).thenReturn(true);

    boolean shouldRetransform = instrumentationState.shouldRetransform(TEST_CLASS);

    assertTrue(shouldRetransform);
  }

  @Test
  void shouldNotRetransformWhenConfigurationIsInactive() {
    when(resolver.getClassInstrumentationConfiguration(TEST_TYPE)).thenReturn(configuration);
    when(configuration.isActive()).thenReturn(false);

    boolean shouldRetransform = instrumentationState.shouldRetransform(TEST_CLASS);

    assertFalse(shouldRetransform);
  }

  @Test
  void shouldUpdateHooksWhenConfigurationIsActive() {
    when(resolver.getClassInstrumentationConfiguration(TEST_TYPE)).thenReturn(configuration);
    when(configuration.isActive()).thenReturn(true);

    instrumentationState.shouldRetransform(TEST_CLASS);

    verify(methodHookManager).updateHooksFor(TEST_CLASS, configuration);
  }

  @Test
  void shouldNotUpdateHooksWhenConfigurationIsInactive() {
    when(resolver.getClassInstrumentationConfiguration(TEST_TYPE)).thenReturn(configuration);
    when(configuration.isActive()).thenReturn(false);

    instrumentationState.shouldRetransform(TEST_CLASS);

    verifyNoInteractions(methodHookManager);
  }
}
