package rocks.inspectit.gepard.agent.resolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static rocks.inspectit.gepard.agent.testutils.CustomAssertions.assertMethodDescriptionMatcherMatches;

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
    Scope scope = createScope(true);
    InspectitConfiguration configuration = createConfiguration(List.of(scope));
    when(holder.getConfiguration()).thenReturn(configuration);

    boolean shouldInstrument = resolver.shouldInstrument(TEST_TYPE);

    assertTrue(shouldInstrument);
  }

  @Test
  void typeShouldBeDeinstrumented() {
    Scope scope = createScope(false);
    InspectitConfiguration configuration = createConfiguration(List.of(scope));
    when(holder.getConfiguration()).thenReturn(configuration);

    boolean shouldInstrument = resolver.shouldInstrument(TEST_TYPE);

    assertFalse(shouldInstrument);
  }

  @Test
  void methodIsNullShouldReturnIsMethodMatcher() {

    Scope scope = createScope(true);
    InspectitConfiguration configuration = createConfiguration(List.of(scope));
    when(holder.getConfiguration()).thenReturn(configuration);

    ElementMatcher.Junction<MethodDescription> elementMatcher =
        resolver.getMethodMatcher(TEST_TYPE);

    assertEquals(elementMatcher, ElementMatchers.isMethod());
  }

  @Test
  void methodIsSpecifiedShouldReturnMatcherForOneMethod() throws NoSuchMethodException {
    Scope scope = createScope(true, List.of("create"));
    InspectitConfiguration configuration = createConfiguration(List.of(scope));
    when(holder.getConfiguration()).thenReturn(configuration);

    ElementMatcher.Junction<MethodDescription> elementMatcher =
        resolver.getMethodMatcher(TEST_TYPE);

    System.out.println(TEST_TYPE.getClass());

    assertMethodDescriptionMatcherMatches(elementMatcher, this.getClass(), "create");
  }

  @Test
  void multipleMethodsAreSpecifiedReturnMatcherForMultipleMethods() throws NoSuchMethodException {
    Scope scope = createScope(true, List.of("create", "use"));
    InspectitConfiguration configuration = createConfiguration(List.of(scope));
    when(holder.getConfiguration()).thenReturn(configuration);

    ElementMatcher.Junction<MethodDescription> elementMatcher =
        resolver.getMethodMatcher(TEST_TYPE);

    assertMethodDescriptionMatcherMatches(elementMatcher, this.getClass(), "use");
    assertMethodDescriptionMatcherMatches(elementMatcher, this.getClass(), "create");
  }

  @Test
  void multipleScopesAreSpecifiedAndOneIsWholeClassCreatesMatcherForAllMethods()
      throws NoSuchMethodException {

    Scope methodedScope = createScope(true, List.of("create"));
    Scope fullClassScope = createScope(true);

    InspectitConfiguration configuration =
        createConfiguration(List.of(methodedScope, fullClassScope));

    when(holder.getConfiguration()).thenReturn(configuration);

    ElementMatcher.Junction<MethodDescription> elementMatcher =
        resolver.getMethodMatcher(TEST_TYPE);

    assertMethodDescriptionMatcherMatches(elementMatcher, this.getClass(), "use");
    assertMethodDescriptionMatcherMatches(elementMatcher, this.getClass(), "create");
  }

  /**
   * @param scopes a list of scopes to be added to the configuration
   * @return the inspectit configuration with the current class as scope
   */
  private InspectitConfiguration createConfiguration(List<Scope> scopes) {
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(scopes);
    return new InspectitConfiguration(instrumentationConfiguration);
  }

  /**
   * Create a new scope
   *
   * @param enabled the status of the scope for this test class
   * @param methodNames the method names to be instrumented
   * @return the scope with the current class as fqn
   */
  private Scope createScope(boolean enabled, List<String> methodNames) {
    return new Scope(TEST_TYPE.getName(), methodNames, enabled);
  }

  private Scope createScope(boolean enabled) {
    return createScope(enabled, null);
  }

  // Mock methods for the matcher test. Has to be public to be visible.
  public void create() {
    System.out.println("create");
  }

  public void use() {
    System.out.println("use");
  }
}
