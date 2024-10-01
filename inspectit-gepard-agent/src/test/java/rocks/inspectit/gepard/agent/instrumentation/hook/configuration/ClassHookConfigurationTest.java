/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClassHookConfigurationTest {

  private ClassHookConfiguration classHookConfiguration;

  @Mock private MethodDescription method;

  @BeforeEach
  void beforeEach() {
    classHookConfiguration = new ClassHookConfiguration();
  }

  @Test
  void asMapReturnsEmptyMapInitially() {
    Map<MethodDescription, Boolean> configMap = classHookConfiguration.asMap();
    assertTrue(configMap.isEmpty());
  }

  @Test
  void getMethodsReturnsEmptySetInitially() {
    Set<MethodDescription> methods = classHookConfiguration.getMethods();
    assertTrue(methods.isEmpty());
  }

  @Test
  void putHookConfigurationAddsMethod() {
    classHookConfiguration.putHookConfiguration(method);
    Map<MethodDescription, Boolean> configMap = classHookConfiguration.asMap();

    assertTrue(configMap.containsKey(method));
    assertEquals(true, configMap.get(method));
  }

  @Test
  void getMethodsReturnsCorrectSetAfterAddingMethod() {
    classHookConfiguration.putHookConfiguration(method);

    Set<MethodDescription> methods = classHookConfiguration.getMethods();

    assertTrue(methods.contains(method));
    assertEquals(1, methods.size());
  }

  @Test
  void testPutHookConfigurationWithMultipleMethods() {
    MethodDescription method2 = Mockito.mock(MethodDescription.class);

    classHookConfiguration.putHookConfiguration(method);
    classHookConfiguration.putHookConfiguration(method2);
    Map<MethodDescription, Boolean> configMap = classHookConfiguration.asMap();

    assertTrue(configMap.containsKey(method));
    assertTrue(configMap.containsKey(method2));
    assertEquals(2, configMap.size());
  }
}
