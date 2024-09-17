package rocks.inspectit.gepard.agent.testutils;

import java.util.Collections;
import java.util.List;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.Scope;

public class InspectitConfigurationUtil {

  /**
   * @param scopes a list of scopes to be added to the configuration
   * @return the inspectit configuration with the current class as scope
   */
  public static InspectitConfiguration createConfiguration(List<Scope> scopes) {
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
  public static Scope createScope(boolean enabled, String name, List<String> methodNames) {
    return new Scope(enabled, name, methodNames);
  }

  public static Scope createScope(boolean enabled, String name) {
    return createScope(enabled, name, Collections.emptyList());
  }
}
