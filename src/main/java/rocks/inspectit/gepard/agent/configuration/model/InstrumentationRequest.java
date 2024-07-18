package rocks.inspectit.gepard.agent.configuration.model;

import java.util.List;

public class InstrumentationRequest {
  private List<Scope> scopes;

  public InstrumentationRequest() {}

  public List<Scope> getScopes() {
    return scopes;
  }

  public Scope getScope(String fqn) {
    return scopes.stream().filter(scope -> scope.getFqn().equals(fqn)).findFirst().orElse(null);
  }
}
