/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.ClassHookConfiguration;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.HookedMethods;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.MethodHookConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

@ExtendWith(MockitoExtension.class)
class MethodHookStateTest {

  private MethodHookState methodHookState;

  @Mock private MethodDescription methodDescription;

  @Mock private ClassHookConfiguration classHookConfiguration;

  @Mock private MethodHookConfiguration methodHookConfiguration;

  @Mock private MethodHook methodHook;

  private static final Class<?> TEST_CLASS = MethodHookManagerTest.class;

  @BeforeEach
  void beforeEach() {
    methodHookState = spy(new MethodHookState());
  }

  @Test
  void shouldReturnNullWhenClassNotPresentInCache() {
    HookedMethods result = methodHookState.getIfPresent(TEST_CLASS);

    assertNull(result);
  }

  @Test
  void shouldReturnHookedMethodsWhenClassPresentInCache() {
    methodHookState.setHook(TEST_CLASS, "method()", methodHook);

    HookedMethods result = methodHookState.getIfPresent(TEST_CLASS);

    assertNotNull(result);
  }

  @Test
  void shouldRemoveObsoleteHooksWhenMethodsAreNotInstrumented() {
    String signature = "method()";
    methodHookState.setHook(TEST_CLASS, signature, methodHook);

    int removedHooks = methodHookState.removeObsoleteHooks(TEST_CLASS, emptySet());

    assertEquals(1, removedHooks);
    verify(methodHookState).removeHook(TEST_CLASS, signature);
  }

  @Test
  void shouldRemoveNoHooksWhenMethodsAreInstrumented() {
    String signature = "method()";
    methodHookState.setHook(TEST_CLASS, signature, methodHook);
    doReturn(signature).when(methodHookState).getSignature(methodDescription);

    int removedHooks = methodHookState.removeObsoleteHooks(TEST_CLASS, Set.of(methodDescription));

    assertEquals(0, removedHooks);
    verify(methodHookState, times(0)).removeHook(TEST_CLASS, signature);
  }

  @Test
  void shouldUpdateHooksForClassWhenMethodsIsConfigured() {
    String expectedSignature = "method()";
    RuleTracingConfiguration tracing = new RuleTracingConfiguration();
    when(classHookConfiguration.asMap())
        .thenReturn(Map.of(methodDescription, methodHookConfiguration));
    when(methodHookConfiguration.tracing()).thenReturn(tracing);
    doReturn(expectedSignature).when(methodHookState).getSignature(methodDescription);

    int updatedHooks = methodHookState.updateHooks(TEST_CLASS, classHookConfiguration);

    assertEquals(1, updatedHooks);
    verify(methodHookState).setHook(eq(TEST_CLASS), eq(expectedSignature), any(MethodHook.class));
  }

  @Test
  void shouldNotUpdateHooksWhenNoMethodsAreConfigured() {
    when(classHookConfiguration.asMap()).thenReturn(emptyMap());

    int updatedHooks = methodHookState.updateHooks(TEST_CLASS, classHookConfiguration);

    assertEquals(0, updatedHooks);
    verify(methodHookState, times(0))
        .setHook(eq(TEST_CLASS), any(String.class), any(MethodHook.class));
  }

  @Test
  public void shouldRemoveClassWhenNoActiveHooksExist() {
    methodHookState.updateHooks(TEST_CLASS, classHookConfiguration);
    methodHookState.removeHook(TEST_CLASS, "method()");

    HookedMethods result = methodHookState.getIfPresent(TEST_CLASS);

    assertNull(result);
  }

  @Test
  void shouldReturnEmptyOptionalWhenNoHookExists() {
    MethodHookConfiguration configuration =
        methodHookState.getCurrentHookConfiguration(TEST_CLASS, "");

    assertNull(configuration);
  }

  @Test
  void shouldReturnHookWhenExits() {
    String signature = "method()";
    when(methodHook.getConfiguration()).thenReturn(methodHookConfiguration);
    methodHookState.setHook(TEST_CLASS, signature, methodHook);

    MethodHookConfiguration configuration =
        methodHookState.getCurrentHookConfiguration(TEST_CLASS, signature);

    assertNotNull(configuration);
    assertEquals(methodHook.getConfiguration(), configuration);
  }
}
