/* (C) 2024 */
package rocks.inspectit.gepard.agent.transformation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.state.InstrumentationState;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

@ExtendWith(MockitoExtension.class)
class DynamicTransformerTest {

  @Mock private InstrumentationState instrumentationState;

  @Mock private DynamicType.Builder<?> builder;

  @Mock private ClassInstrumentationConfiguration configuration;

  private static final Class<?> TEST_CLASS = DynamicTransformerTest.class;

  @Test
  void testTransformTransformsOnShouldInstrumentTrue() {
    DynamicTransformer transformer = new DynamicTransformer(instrumentationState);
    InstrumentedType type = new InstrumentedType(TEST_CLASS.getName(), TEST_CLASS.getClassLoader());
    TypeDescription typeDescription = TypeDescription.ForLoadedType.of(TEST_CLASS);

    when(instrumentationState.resolveClassConfiguration(type)).thenReturn(configuration);
    when(configuration.isActive()).thenReturn(true);

    transformer.transform(builder, typeDescription, TEST_CLASS.getClassLoader(), null, null);

    verify(builder).visit(any());
    verify(instrumentationState).addInstrumentedType(type, configuration);
  }

  @Test
  void testTransformerDoesNotTransformOnShouldInstrumentFalse() {
    DynamicTransformer transformer = new DynamicTransformer(instrumentationState);
    InstrumentedType type = new InstrumentedType(TEST_CLASS.getName(), TEST_CLASS.getClassLoader());
    TypeDescription typeDescription = TypeDescription.ForLoadedType.of(TEST_CLASS);

    when(instrumentationState.resolveClassConfiguration(type)).thenReturn(configuration);
    when(configuration.isActive()).thenReturn(false);
    when(instrumentationState.isActive(type)).thenReturn(true);

    transformer.transform(builder, typeDescription, TEST_CLASS.getClassLoader(), null, null);

    verify(builder, never()).visit(any());
    verify(instrumentationState).invalidateInstrumentedType(type);
  }
}
