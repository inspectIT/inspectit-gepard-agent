/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration.resolver.rules.scopes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.scopes.InstrumentationScope.ALL_METHODS;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.scopes.InstrumentationScope;
import rocks.inspectit.gepard.config.model.instrumentation.scopes.ScopeConfiguration;

@ExtendWith(MockitoExtension.class)
class ScopeResolverTest {

  @Mock private ScopeConfiguration scopeConfig;

  private final ScopeResolver resolver = new ScopeResolver();

  private static final TypeDescription TEST_TYPE =
      TypeDescription.ForLoadedType.of(ScopeResolverTest.class);

  @Test
  void shouldCreateScopeWhichMatchesTypeAndAllMethods() {
    Map<String, ScopeConfiguration> scopeConfigs = Map.of("s_scope", scopeConfig);
    when(scopeConfig.isEnabled()).thenReturn(true);
    when(scopeConfig.getFqn()).thenReturn(TEST_TYPE.getName());
    when(scopeConfig.getMethods()).thenReturn(Collections.emptyList());

    Map<String, InstrumentationScope> scopes = resolver.resolveScopes(scopeConfigs);
    InstrumentationScope resultScope = scopes.get("s_scope");

    assertEquals(1, scopes.size());
    assertEquals(ALL_METHODS, resultScope.getMethodMatcher());
    assertTrue(resultScope.getTypeMatcher().matches(TEST_TYPE));
  }

  @Test
  void shouldCreateScopeWhichMatchesSpecificMethod() {
    String methodName1 = "method1";
    MethodDescription method1 = mock(MethodDescription.class);
    when(method1.getActualName()).thenReturn(methodName1);
    String methodName2 = "method2";
    MethodDescription method2 = mock(MethodDescription.class);
    when(method2.getActualName()).thenReturn(methodName2);

    Map<String, ScopeConfiguration> scopeConfigs = Map.of("s_scope", scopeConfig);
    when(scopeConfig.isEnabled()).thenReturn(true);
    when(scopeConfig.getFqn()).thenReturn(TEST_TYPE.getName());
    when(scopeConfig.getMethods()).thenReturn(List.of(methodName1));

    Map<String, InstrumentationScope> scopes = resolver.resolveScopes(scopeConfigs);
    InstrumentationScope resultScope = scopes.get("s_scope");

    assertEquals(1, scopes.size());
    assertTrue(resultScope.getMethodMatcher().matches(method1));
    assertFalse(resultScope.getMethodMatcher().matches(method2));
    assertTrue(resultScope.getTypeMatcher().matches(TEST_TYPE));
  }

  @Test
  void shouldNotCreateScopeWhenInactive() {
    Map<String, ScopeConfiguration> scopeConfigs = Map.of("s_scope", scopeConfig);
    when(scopeConfig.isEnabled()).thenReturn(false);

    Map<String, InstrumentationScope> scopes = resolver.resolveScopes(scopeConfigs);

    assertEquals(0, scopes.size());
  }
}
