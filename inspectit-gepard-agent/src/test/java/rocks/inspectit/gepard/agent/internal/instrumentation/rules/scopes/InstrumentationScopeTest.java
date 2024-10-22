/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.instrumentation.rules.scopes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static rocks.inspectit.gepard.agent.testutils.CustomAssertions.assertMethodDescriptionMatcherMatches;

import java.util.Collections;
import java.util.List;
import net.bytebuddy.description.type.TypeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.scopes.InstrumentationScope;
import rocks.inspectit.gepard.config.model.instrumentation.scopes.ScopeConfiguration;

@ExtendWith(MockitoExtension.class)
class InstrumentationScopeTest {

  @Mock private ScopeConfiguration scopeConfig;

  private static final TypeDescription TEST_TYPE =
      TypeDescription.ForLoadedType.of(InstrumentationScopeTest.class);

  private static final String METHOD_NAME = "method";

  @BeforeEach
  void beforeEach() {
    when(scopeConfig.getFqn()).thenReturn(getClass().getName());
    when(scopeConfig.getMethods()).thenReturn(List.of(METHOD_NAME));
  }

  @Test
  void shouldCreateScope() throws NoSuchMethodException {
    InstrumentationScope scope = InstrumentationScope.create(scopeConfig);

    assertEquals(getClass().getName(), scopeConfig.getFqn());
    assertTrue(scope.getTypeMatcher().matches(TEST_TYPE));
    assertMethodDescriptionMatcherMatches(scope.getMethodMatcher(), getClass(), METHOD_NAME);
  }

  @Test
  void shouldMatchAllMethodsWhenNoMethodsSpecified() {
    when(scopeConfig.getMethods()).thenReturn(Collections.emptyList());

    InstrumentationScope scope = InstrumentationScope.create(scopeConfig);

    assertEquals(InstrumentationScope.ALL_METHODS, scope.getMethodMatcher());
  }

  @Test
  void shouldEqualOtherType() {
    InstrumentationScope scope1 = InstrumentationScope.create(scopeConfig);
    InstrumentationScope scope2 = InstrumentationScope.create(scopeConfig);

    assertEquals(scope1, scope2);
  }

  @Test
  void shouldNotEqualOtherType() {
    ScopeConfiguration otherConfig = new ScopeConfiguration();

    InstrumentationScope scope1 = InstrumentationScope.create(scopeConfig);
    InstrumentationScope scope2 = InstrumentationScope.create(otherConfig);

    assertNotEquals(scope1, scope2);
  }

  @Test
  void shouldHaveSameHashCode() {
    InstrumentationScope scope1 = InstrumentationScope.create(scopeConfig);
    InstrumentationScope scope2 = InstrumentationScope.create(scopeConfig);

    assertEquals(scope1.hashCode(), scope2.hashCode());
  }

  @Test
  void shouldNotHaveSameHashCode() {
    ScopeConfiguration otherConfig = new ScopeConfiguration(false, "fqn", Collections.emptyList());

    InstrumentationScope scope1 = InstrumentationScope.create(scopeConfig);
    InstrumentationScope scope2 = InstrumentationScope.create(otherConfig);

    assertNotEquals(scope1.hashCode(), scope2.hashCode());
  }

  // Mock methods for the matcher test. Has to be public to be visible.
  public void method() {
    System.out.println("method");
  }
}
