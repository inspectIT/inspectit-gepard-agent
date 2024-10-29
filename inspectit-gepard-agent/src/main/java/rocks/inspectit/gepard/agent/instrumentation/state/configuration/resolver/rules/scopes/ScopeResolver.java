/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration.resolver.rules.scopes;

import java.util.*;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.scopes.InstrumentationScope;
import rocks.inspectit.gepard.config.model.instrumentation.scopes.ScopeConfiguration;

/** This class is used to resolve {@link ScopeConfiguration}s to {@link InstrumentationScope}s. */
public class ScopeResolver {

  /**
   * Resolves every scope configuration to an instrumentation scope.
   *
   * @param scopeConfigs the scope configurations
   * @return the resolved configuration.
   */
  public Map<String, InstrumentationScope> resolveScopes(
      Map<String, ScopeConfiguration> scopeConfigs) {
    int scopesSize = scopeConfigs.size();
    Map<String, InstrumentationScope> result = new HashMap<>(scopesSize);

    scopeConfigs.forEach(
        (name, scopeConfig) -> {
          if (scopeConfig.isEnabled()) {
            InstrumentationScope scope = InstrumentationScope.create(scopeConfig);
            result.put(name, scope);
          }
        });
    return result;
  }
}
