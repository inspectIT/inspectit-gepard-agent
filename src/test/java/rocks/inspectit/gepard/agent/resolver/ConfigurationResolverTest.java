package rocks.inspectit.gepard.agent.resolver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import net.bytebuddy.description.type.TypeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.Scope;

@ExtendWith(MockitoExtension.class)
class ConfigurationResolverTest {

  @Mock private ConfigurationHolder holder;

  private ConfigurationResolver resolver;

  private final Class<?> TEST_CLASS = getClass();

  @BeforeEach
  void initialize() {
    resolver = ConfigurationResolver.create(holder);
  }

  @Test
  void classShouldNotBeRetransformed() {
    InspectitConfiguration configuration = new InspectitConfiguration();
    when(holder.getConfiguration()).thenReturn(configuration);

    boolean shouldRetransform = resolver.shouldRetransform(TEST_CLASS);

    assertFalse(shouldRetransform);
  }

  @Test
  void classShouldBeRetransformed() {
    InspectitConfiguration configuration = createConfiguration(true);
    when(holder.getConfiguration()).thenReturn(configuration);

    boolean shouldRetransform = resolver.shouldRetransform(TEST_CLASS);

    assertTrue(shouldRetransform);
  }

  @Test
  void typeShouldNotBeInstrumented() {
    InspectitConfiguration configuration = new InspectitConfiguration();
    when(holder.getConfiguration()).thenReturn(configuration);

    TypeDescription type = new TypeDescription.ForLoadedType(TEST_CLASS);
    boolean shouldInstrument = resolver.shouldInstrument(type);

    assertFalse(shouldInstrument);
  }

  @Test
  void typeShouldBeInstrumented() {
    InspectitConfiguration configuration = createConfiguration(true);
    when(holder.getConfiguration()).thenReturn(configuration);

    TypeDescription type = new TypeDescription.ForLoadedType(TEST_CLASS);
    boolean shouldInstrument = resolver.shouldInstrument(type);

    assertTrue(shouldInstrument);
  }

  @Test
  void typeShouldBeDeinstrumented() {
    InspectitConfiguration configuration = createConfiguration(false);
    when(holder.getConfiguration()).thenReturn(configuration);

    TypeDescription type = new TypeDescription.ForLoadedType(TEST_CLASS);
    boolean shouldInstrument = resolver.shouldInstrument(type);

    assertFalse(shouldInstrument);
  }

  /**
   * @param enabled the status of the scope for this test class
   * @return the inspectit configuration with the current class as scope
   */
  private InspectitConfiguration createConfiguration(boolean enabled) {
    Scope scope = new Scope(TEST_CLASS.getName(), enabled);
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(List.of(scope));
    return new InspectitConfiguration(instrumentationConfiguration);
  }
}
