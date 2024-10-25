/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration;

import java.util.Objects;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

/**
 * Configuration for one specific method hook. Currently just for tracing. Later we will add
 * entry-/exit-actions and metrics.
 */
public class MethodHookConfiguration {

  private final String methodName;

  private final RuleTracingConfiguration tracing;

  public MethodHookConfiguration(String methodName, RuleTracingConfiguration tracing) {
    this.methodName = methodName;
    this.tracing = tracing;
  }

  public MethodHookConfiguration(String methodName) {
    this.methodName = methodName;
    this.tracing = RuleTracingConfiguration.NO_TRACING;
  }

  /**
   * @return the name of the hooked method
   */
  public String getMethodName() {
    return methodName;
  }

  /**
   * @return the tracing configuration
   */
  public RuleTracingConfiguration getTracing() {
    return tracing;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof MethodHookConfiguration otherConfig)
      return methodName.equals(otherConfig.methodName) && tracing.equals(otherConfig.tracing);
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, tracing);
  }
}
