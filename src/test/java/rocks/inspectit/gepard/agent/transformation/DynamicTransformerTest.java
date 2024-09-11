package rocks.inspectit.gepard.agent.transformation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentationState;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;
import rocks.inspectit.gepard.agent.resolver.ConfigurationResolver;

@ExtendWith(MockitoExtension.class)
class DynamicTransformerTest {

  @Mock private ConfigurationResolver resolver;

  @Mock private InstrumentationState instrumentationState;

  @Mock private DynamicType.Builder<?> builder;

  @Mock private ClassInstrumentationConfiguration configuration;

  private final Class<?> TEST_CLASS = getClass();

  @Test
  void testTransformTransformsOnShouldInstrumentTrue() {
    DynamicTransformer transformer = new DynamicTransformer(resolver, instrumentationState);
    TypeDescription typeDescription = TypeDescription.ForLoadedType.of(TEST_CLASS);

    when(resolver.getClassInstrumentationConfiguration(typeDescription)).thenReturn(configuration);
    when(configuration.isActive()).thenReturn(true);

    transformer.transform(builder, typeDescription, TEST_CLASS.getClassLoader(), null, null);

    verify(builder).visit(any());
    verify(instrumentationState).addInstrumentedType(any(), any());
  }

  @Test
  void testTransformerDoesNotTransformOnShouldInstrumentFalse() {
    DynamicTransformer transformer = new DynamicTransformer(resolver, instrumentationState);
    TypeDescription typeDescription = TypeDescription.ForLoadedType.of(TEST_CLASS);

    when(resolver.getClassInstrumentationConfiguration(typeDescription)).thenReturn(configuration);
    when(configuration.isActive()).thenReturn(false);
    when(instrumentationState.isInstrumented(any(InstrumentedType.class))).thenReturn(true);

    transformer.transform(builder, typeDescription, TEST_CLASS.getClassLoader(), null, null);

    verify(builder, never()).visit(any());
    verify(instrumentationState).invalidateInstrumentedType(any());
  }
}
