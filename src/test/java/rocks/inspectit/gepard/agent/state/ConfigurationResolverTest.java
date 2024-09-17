package rocks.inspectit.gepard.agent.state;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static rocks.inspectit.gepard.agent.testutils.InspectitConfigurationUtil.createConfiguration;
import static rocks.inspectit.gepard.agent.testutils.InspectitConfigurationUtil.createScope;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.Scope;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

@ExtendWith(MockitoExtension.class)
class ConfigurationResolverTest {

  @Mock private InspectitConfigurationHolder holder;

  private ConfigurationResolver resolver;

  private final InstrumentedType TEST_TYPE =
      new InstrumentedType(getClass().getName(), getClass().getClassLoader());

  @BeforeEach
  void beforeEach() {
    resolver = ConfigurationResolver.create(holder);
  }

  @Test
  void typeShouldNotBeInstrumented() {
    InspectitConfiguration configuration = new InspectitConfiguration();
    when(holder.getConfiguration()).thenReturn(configuration);

    ClassInstrumentationConfiguration config =
        resolver.getClassInstrumentationConfiguration(TEST_TYPE);
    boolean isActive = config.isActive();

    assertFalse(isActive);
  }

  @Test
  void typeShouldBeInstrumented() {
    Scope scope = createScope(true, getClass().getName());
    InspectitConfiguration configuration = createConfiguration(List.of(scope));
    when(holder.getConfiguration()).thenReturn(configuration);

    ClassInstrumentationConfiguration config =
        resolver.getClassInstrumentationConfiguration(TEST_TYPE);
    boolean isActive = config.isActive();

    assertTrue(isActive);
  }

  @Test
  void typeShouldBeDeinstrumented() {
    Scope scope = createScope(false, getClass().getName());
    InspectitConfiguration configuration = createConfiguration(List.of(scope));
    when(holder.getConfiguration()).thenReturn(configuration);

    ClassInstrumentationConfiguration config =
        resolver.getClassInstrumentationConfiguration(TEST_TYPE);
    boolean isActive = config.isActive();

    assertFalse(isActive);
  }
}
