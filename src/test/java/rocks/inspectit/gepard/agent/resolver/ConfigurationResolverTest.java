package rocks.inspectit.gepard.agent.resolver;

import static net.bytebuddy.matcher.ElementMatchers.anyOf;
import static net.bytebuddy.matcher.ElementMatchers.hasMethodName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
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

  private final TypeDescription TEST_TYPE =
      TypeDescription.ForLoadedType.of(ConfigurationResolverTest.class);

  private final String TEST_METHOD = "createConfiguration";

  @BeforeEach
  void initialize() {
    resolver = ConfigurationResolver.create(holder);
  }

  @Test
  void typeShouldNotBeInstrumented() {
    InspectitConfiguration configuration = new InspectitConfiguration();
    when(holder.getConfiguration()).thenReturn(configuration);

    boolean shouldRetransform = resolver.shouldInstrument(TEST_TYPE);

    assertFalse(shouldRetransform);
  }

  @Test
  void typeShouldBeInstrumented() {
    InspectitConfiguration configuration = createConfiguration(true);
    when(holder.getConfiguration()).thenReturn(configuration);

    boolean shouldInstrument = resolver.shouldInstrument(TEST_TYPE);

    assertTrue(shouldInstrument);
  }

  @Test
  void typeShouldBeDeinstrumented() {
    InspectitConfiguration configuration = createConfiguration(false);
    when(holder.getConfiguration()).thenReturn(configuration);

    boolean shouldInstrument = resolver.shouldInstrument(TEST_TYPE);

    assertFalse(shouldInstrument);
  }

  @Test
  void methodIsNullShouldReturnIsMethodMatcher() {

    InspectitConfiguration configuration = createConfiguration(true);
    when(holder.getConfiguration()).thenReturn(configuration);

    ElementMatcher.Junction<MethodDescription> elementMatcher =
        resolver.getElementMatcherForType(TEST_TYPE);

    assertEquals(elementMatcher, ElementMatchers.isMethod());
  }

  @Test
  void methodIsSpecifiedShouldReturnMatcherForOneMethod() {
    InspectitConfiguration configuration = createConfiguration(true, List.of("create"));
    when(holder.getConfiguration()).thenReturn(configuration);

    ElementMatcher<MethodDescription> expectedMatcher = hasMethodName("create");

    ElementMatcher.Junction<MethodDescription> elementMatcher =
        resolver.getElementMatcherForType(TEST_TYPE);

    assertEquals(expectedMatcher, elementMatcher);
  }

  @Test
  void multipleMethodsAreSpecifiedReturnMatcherForMultipleMethods() {
    InspectitConfiguration configuration = createConfiguration(true, List.of("create", "initialize"));
    when(holder.getConfiguration()).thenReturn(configuration);

    ElementMatcher<MethodDescription> expectedMatcher = anyOf("create","initialize");

    ElementMatcher.Junction<MethodDescription> elementMatcher =
        resolver.getElementMatcherForType(TEST_TYPE);

    assertEquals(expectedMatcher, elementMatcher);
  }

  /**
   * @param enabled the status of the scope for this test class
   * @return the inspectit configuration with the current class as scope
   */
  private InspectitConfiguration createConfiguration(boolean enabled) {
    Scope scope = new Scope(TEST_TYPE.getName(), enabled);
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(List.of(scope));
    return new InspectitConfiguration(instrumentationConfiguration);
  }

  private InspectitConfiguration createConfiguration(boolean enabled, List<String> methodNames) {
    Scope scope = new Scope(TEST_TYPE.getName(), methodNames, enabled);
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(List.of(scope));
    return new InspectitConfiguration(instrumentationConfiguration);
  }
}
