/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration.scope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static rocks.inspectit.gepard.agent.testutils.CustomAssertions.assertMethodDescriptionMatcherMatches;
import static rocks.inspectit.gepard.agent.testutils.InspectitConfigurationUtil.createConfiguration;
import static rocks.inspectit.gepard.agent.testutils.InspectitConfigurationUtil.createScope;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.InspectitConfigurationHolder;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.Scope;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.InstrumentationScope;

@ExtendWith(MockitoExtension.class)
class ScopeResolverTest {

  @Mock private InspectitConfigurationHolder holder;

  @InjectMocks private ScopeResolver resolver;

  private static final String CLASS_NAME = ScopeResolverTest.class.getName();

  @Test
  void returnsOnlyActiveScopes() {
    Scope matchingScope = createScope(true, CLASS_NAME, List.of("method"));
    Scope nonMatchingScope1 = createScope(false, CLASS_NAME);
    Scope nonMatchingScope2 = createScope(true, "dummyName");
    List<Scope> scopes = List.of(matchingScope, nonMatchingScope1, nonMatchingScope2);
    InspectitConfiguration configuration = createConfiguration(scopes);
    when(holder.getConfiguration()).thenReturn(configuration);

    Set<InstrumentationScope> activeScopes = resolver.getActiveScopes(CLASS_NAME);
    InstrumentationScope activeScope = activeScopes.iterator().next();

    assertEquals(1, activeScopes.size());
    assertEquals(matchingScope.getFqn(), activeScope.fqn());
    assertEquals(matchingScope.getMethods(), activeScope.methods());
  }

  @Test
  void methodIsNullShouldReturnIsMethodMatcher() {
    InstrumentationScope instrumentationScope =
        new InstrumentationScope(CLASS_NAME, Collections.emptyList());

    ElementMatcher.Junction<MethodDescription> elementMatcher =
        resolver.getMethodMatcher(Set.of(instrumentationScope));

    assertEquals(elementMatcher, ElementMatchers.isMethod());
  }

  @Test
  void methodIsSpecifiedShouldReturnMatcherForOneMethod() throws NoSuchMethodException {
    InstrumentationScope instrumentationScope =
        new InstrumentationScope(CLASS_NAME, List.of("create"));

    ElementMatcher.Junction<MethodDescription> elementMatcher =
        resolver.getMethodMatcher(Set.of(instrumentationScope));

    assertMethodDescriptionMatcherMatches(elementMatcher, this.getClass(), "create");
  }

  @Test
  void multipleMethodsAreSpecifiedReturnMatcherForMultipleMethods() throws NoSuchMethodException {
    InstrumentationScope instrumentationScope =
        new InstrumentationScope(CLASS_NAME, List.of("create", "use"));

    ElementMatcher.Junction<MethodDescription> elementMatcher =
        resolver.getMethodMatcher(Set.of(instrumentationScope));

    assertMethodDescriptionMatcherMatches(elementMatcher, this.getClass(), "use");
    assertMethodDescriptionMatcherMatches(elementMatcher, this.getClass(), "create");
  }

  @Test
  void multipleScopesAreSpecifiedAndOneIsWholeClassCreatesMatcherForAllMethods()
      throws NoSuchMethodException {
    InstrumentationScope instMethodedScope =
        new InstrumentationScope(CLASS_NAME, List.of("create"));
    InstrumentationScope instFullClassScope =
        new InstrumentationScope(CLASS_NAME, Collections.emptyList());

    ElementMatcher.Junction<MethodDescription> elementMatcher =
        resolver.getMethodMatcher(Set.of(instMethodedScope, instFullClassScope));

    assertMethodDescriptionMatcherMatches(elementMatcher, this.getClass(), "use");
    assertMethodDescriptionMatcherMatches(elementMatcher, this.getClass(), "create");
  }

  // Mock methods for the matcher test. Has to be public to be visible.
  public void create() {
    System.out.println("create");
  }

  public void use() {
    System.out.println("use");
  }
}
