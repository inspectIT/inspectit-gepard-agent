/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import net.bytebuddy.description.method.MethodDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.resolver.MethodHookConfigurationResolver;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

@ExtendWith(MockitoExtension.class)
class ClassHookConfigurationTest {

  private ClassHookConfiguration classHookConfiguration;

  @Mock private MethodDescription method;

  @Mock private MethodHookConfiguration methodHookConfiguration;

  @Mock private MethodHookConfigurationResolver resolver;

  @Mock private ClassInstrumentationConfiguration classConfiguration;

  @BeforeEach
  void beforeEach() {
    classHookConfiguration = new ClassHookConfiguration(resolver);
  }

  @Test
  void asMapReturnsEmptyMapInitially() {
    Map<MethodDescription, MethodHookConfiguration> configMap = classHookConfiguration.asMap();
    assertTrue(configMap.isEmpty());
  }

  @Test
  void getMethodsReturnsEmptySetInitially() {
    Set<MethodDescription> methods = classHookConfiguration.getMethods();
    assertTrue(methods.isEmpty());
  }

  @Test
  void putHookConfigurationAddsMethod() {
    when(resolver.resolve(method, classConfiguration)).thenReturn(methodHookConfiguration);

    classHookConfiguration.putHookConfiguration(method, classConfiguration);
    Map<MethodDescription, MethodHookConfiguration> configMap = classHookConfiguration.asMap();

    assertTrue(configMap.containsKey(method));
    assertEquals(methodHookConfiguration, configMap.get(method));
  }

  @Test
  void getMethodsReturnsCorrectSetAfterAddingMethod() {
    when(resolver.resolve(method, classConfiguration)).thenReturn(methodHookConfiguration);

    classHookConfiguration.putHookConfiguration(method, classConfiguration);

    Set<MethodDescription> methods = classHookConfiguration.getMethods();

    assertTrue(methods.contains(method));
    assertEquals(1, methods.size());
  }

  @Test
  void testPutHookConfigurationWithMultipleMethods() {
    when(resolver.resolve(method, classConfiguration)).thenReturn(methodHookConfiguration);
    MethodDescription method2 = Mockito.mock(MethodDescription.class);

    classHookConfiguration.putHookConfiguration(method, classConfiguration);
    classHookConfiguration.putHookConfiguration(method2, classConfiguration);
    Map<MethodDescription, MethodHookConfiguration> configMap = classHookConfiguration.asMap();

    assertTrue(configMap.containsKey(method));
    assertTrue(configMap.containsKey(method2));
    assertEquals(2, configMap.size());
  }
}
