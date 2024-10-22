/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration;

import java.util.Objects;
import rocks.inspectit.gepard.config.model.instrumentation.rules.RuleTracingConfiguration;

/**
 * Configuration for one specific method hook. Currently just for tracing. Later we will add
 * entry-/exit-actions and metrics.
 *
 * @param methodName the name of the hooked method
 * @param tracing the tracing configuration
 */
public record MethodHookConfiguration(String methodName, RuleTracingConfiguration tracing) {

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
