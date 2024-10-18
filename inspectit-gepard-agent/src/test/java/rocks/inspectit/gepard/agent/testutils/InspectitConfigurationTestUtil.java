/* (C) 2024 */
package rocks.inspectit.gepard.agent.testutils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.scopes.ScopeConfiguration;

public class InspectitConfigurationTestUtil {

  /**
   * @param scopes a list of scopes to be added to the configuration
   * @return the inspectit configuration with the current class as scope
   */
  public static InspectitConfiguration createConfiguration(Map<String, ScopeConfiguration> scopes) {
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(scopes, Map.of());
    return new InspectitConfiguration(instrumentationConfiguration);
  }

  /**
   * Create a new scope
   *
   * @param enabled the status of the scope for this test class
   * @param methodNames the method names to be instrumented
   * @return the scope with the current class as fqn
   */
  public static ScopeConfiguration createScope(
      boolean enabled, String name, List<String> methodNames) {
    return new ScopeConfiguration(enabled, name, methodNames);
  }

  public static ScopeConfiguration createScope(boolean enabled, String name) {
    return createScope(enabled, name, Collections.emptyList());
  }

  /**
   * @return the inspectIT configuration as string.
   */
  public static String expectedString() {
    return "{\"instrumentation\":{\"scopes\":{\"s_scope\":{\"enabled\":true,\"fqn\":\"com.example.Application\",\"methods\":[]}},\"rules\":{}}}";
  }
}
