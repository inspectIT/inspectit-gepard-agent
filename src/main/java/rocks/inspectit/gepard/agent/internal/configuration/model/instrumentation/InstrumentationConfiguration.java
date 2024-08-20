package rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation;

import java.util.List;

/**
 * The Instrumentation Configuration contains all configuration related to instrumentation. e.g
 * scopes, rules, actions.
 */
public class InstrumentationConfiguration {

  private List<Scope> scopes;

  public InstrumentationConfiguration() {
    this.scopes = List.of();
  }

  public InstrumentationConfiguration(List<Scope> scopes) {
    this.scopes = scopes;
  }

  public List<Scope> getScopes() {
    return scopes;
  }

  public Scope getScopeByFqn(String fqn) {
    return scopes.stream().filter(scope -> scope.getFqn().equals(fqn)).findFirst().orElse(null);
  }
}
