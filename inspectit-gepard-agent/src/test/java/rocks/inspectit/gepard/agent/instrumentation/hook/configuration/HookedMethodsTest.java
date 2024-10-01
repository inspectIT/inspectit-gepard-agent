/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHook;

@ExtendWith(MockitoExtension.class)
class HookedMethodsTest {

  private HookedMethods hookedMethods;

  @Mock private MethodHook methodHook;

  private final String signature = "method(java.lang.String,java.lang.String)";

  @BeforeEach
  void beforeEach() {
    hookedMethods = new HookedMethods();
  }

  @Test
  void shouldReturnNullWhenNoActiveHookForSignature() {
    MethodHook result = hookedMethods.getActiveHook("dummy");

    assertNull(result);
  }

  @Test
  void shouldReturnHookAfterAddingSignature() {
    hookedMethods.putMethod(signature, methodHook);
    MethodHook result = hookedMethods.getActiveHook(signature);

    assertEquals(methodHook, result);
  }

  @Test
  void shouldReturnCorrectMethodSignatures() {
    String signature2 = "method()";
    hookedMethods.putMethod(signature, methodHook);
    hookedMethods.putMethod(signature2, methodHook);
    Set<String> signatures = hookedMethods.getMethodSignatures();

    assertTrue(signatures.contains(signature));
    assertTrue(signatures.contains(signature2));
    assertEquals(2, signatures.size());
  }

  @Test
  void shouldRemoveMethodSignatureCorrectly() {
    hookedMethods.putMethod(signature, methodHook);
    hookedMethods.removeMethod(signature);

    assertNull(hookedMethods.getActiveHook(signature));
    assertTrue(hookedMethods.noActiveHooks());
  }

  @Test
  void shouldIndicateNoActiveHooksInitially() {
    assertTrue(hookedMethods.noActiveHooks());
  }

  @Test
  void shouldIndicateHooksAreActiveAfterAddingMethod() {
    hookedMethods.putMethod(signature, methodHook);

    assertFalse(hookedMethods.noActiveHooks());
  }
}
