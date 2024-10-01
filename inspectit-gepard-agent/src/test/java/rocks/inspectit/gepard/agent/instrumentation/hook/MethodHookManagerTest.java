/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.ClassHookConfiguration;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.HookedMethods;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;
import rocks.inspectit.gepard.bootstrap.Instances;
import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;
import rocks.inspectit.gepard.bootstrap.instrumentation.noop.NoopHookManager;
import rocks.inspectit.gepard.bootstrap.instrumentation.noop.NoopMethodHook;

@ExtendWith(MockitoExtension.class)
class MethodHookManagerTest {

  private MethodHookManager methodHookManager;

  @Mock private MethodHookState hookState;

  @Mock private HookedMethods hookedMethods;

  @Mock private ClassInstrumentationConfiguration classConfiguration;

  @Mock private MethodHook methodHook;

  private static final Class<?> TEST_CLASS = MethodHookManagerTest.class;

  private static final String methodSignature = "method()";

  @BeforeEach
  void beforeEach() {
    methodHookManager = MethodHookManager.create(hookState);
  }

  @AfterEach
  void afterEach() {
    Instances.hookManager = NoopHookManager.INSTANCE;
  }

  @Test
  void shouldThrowExceptionWhenManagerAlreadySet() {
    assertThrows(IllegalStateException.class, () -> MethodHookManager.create(hookState));
  }

  @Test
  void shouldReturnNoopHookWhenNoActiveHooksPresent() {
    when(hookState.getIfPresent(TEST_CLASS)).thenReturn(null);

    IMethodHook hook = methodHookManager.getHook(TEST_CLASS, methodSignature);

    assertEquals(NoopMethodHook.INSTANCE, hook);
  }

  @Test
  void shouldReturnActiveHookWhenPresent() {
    when(hookState.getIfPresent(TEST_CLASS)).thenReturn(hookedMethods);
    when(hookedMethods.getActiveHook(methodSignature)).thenReturn(methodHook);

    IMethodHook hook = methodHookManager.getHook(TEST_CLASS, methodSignature);

    assertNotEquals(NoopMethodHook.INSTANCE, hook);
  }

  @Test
  void shouldUpdateHooksForClassWhenMethodsAreInstrumented() {
    when(classConfiguration.methodMatcher()).thenReturn(isMethod());

    methodHookManager.updateHooksFor(TEST_CLASS, classConfiguration);

    verify(hookState).removeObsoleteHooks(eq(TEST_CLASS), any(Set.class));
    verify(hookState).updateHooks(eq(TEST_CLASS), any(ClassHookConfiguration.class));
  }
}
