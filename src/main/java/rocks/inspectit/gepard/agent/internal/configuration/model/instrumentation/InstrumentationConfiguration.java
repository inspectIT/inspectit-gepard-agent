package rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation;

import java.util.List;

public class InstrumentationConfiguration {

  private List<Scope> scopes;

  public List<Scope> getScopes() {
    return scopes;
  }

  public InstrumentationConfiguration() {
    this.scopes = List.of();
  }
  public Scope getScope(String fqn) {
    return scopes.stream().filter(scope -> scope.getFqn().equals(fqn)).findFirst().orElse(null);
  }
}
