/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration.resolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static rocks.inspectit.gepard.agent.testutils.InspectitConfigurationTestUtil.createConfiguration;
import static rocks.inspectit.gepard.agent.testutils.InspectitConfigurationTestUtil.createScope;

import java.util.Map;
import net.bytebuddy.description.type.TypeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.InspectitConfigurationHolder;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.scopes.ScopeConfiguration;

@ExtendWith(MockitoExtension.class)
class ConfigurationResolverTest {

  @Mock private InspectitConfigurationHolder holder;

  private ConfigurationResolver resolver;

  private static final TypeDescription TYPE_DESCRIPTION =
      TypeDescription.ForLoadedType.of(ConfigurationResolverTest.class);

  private static final InstrumentedType TEST_TYPE =
      new InstrumentedType(TYPE_DESCRIPTION, ConfigurationResolverTest.class.getClassLoader());

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
    ScopeConfiguration scope = createScope(true, getClass().getName());
    InspectitConfiguration configuration = createConfiguration(Map.of("s_scope", scope));
    when(holder.getConfiguration()).thenReturn(configuration);

    ClassInstrumentationConfiguration config =
        resolver.getClassInstrumentationConfiguration(TEST_TYPE);
    boolean isActive = config.isActive();

    assertTrue(isActive);
  }

  @Test
  void typeShouldBeDeinstrumented() {
    ScopeConfiguration scope = createScope(false, getClass().getName());
    InspectitConfiguration configuration = createConfiguration(Map.of("s_scope", scope));
    when(holder.getConfiguration()).thenReturn(configuration);

    ClassInstrumentationConfiguration config =
        resolver.getClassInstrumentationConfiguration(TEST_TYPE);
    boolean isActive = config.isActive();

    assertFalse(isActive);
  }
}
