package rocks.inspectit.gepard.agent.transformation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentationState;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.resolver.ConfigurationResolver;

class DynamicTransformerTest {

  @Mock private ConfigurationResolver resolver = mock(ConfigurationResolver.class);

  @Mock private InstrumentationState instrumentationState = mock(InstrumentationState.class);

  @Mock DynamicType.Builder<?> builder = mock(DynamicType.Builder.class);

  @Test
  void testTransformTransformsOnShouldInstrumentFalse() {

    Class<?> TEST_CLASS = DynamicTransformerTest.class;

    DynamicTransformer transformer = new DynamicTransformer(resolver, instrumentationState);

    TypeDescription typeDescription = TypeDescription.ForLoadedType.of(TEST_CLASS);

    when(resolver.shouldInstrument(typeDescription)).thenReturn(true);

    transformer.transform(builder, typeDescription, TEST_CLASS.getClassLoader(), null, null);

    verify(builder).visit(any());
    verify(instrumentationState).addInstrumentedType(any());
  }

  @Test
  void testTransformerDoesNotTransformOnShouldInstrumentFalse() {

    Class<?> TEST_CLASS = DynamicTransformerTest.class;

    DynamicTransformer transformer = new DynamicTransformer(resolver, instrumentationState);

    TypeDescription typeDescription = TypeDescription.ForLoadedType.of(TEST_CLASS);

    when(resolver.shouldInstrument(typeDescription)).thenReturn(false);

    when(instrumentationState.isInstrumented(any(InstrumentedType.class))).thenReturn(true);

    transformer.transform(builder, typeDescription, TEST_CLASS.getClassLoader(), null, null);

    verify(builder, never()).visit(any());
    verify(instrumentationState).invalidateInstrumentedType(any());
  }
}
