package rocks.inspectit.gepard.agent.configuration.model;

public class Scope {
  private String fqn;
  private boolean enabled;

  public Scope() {
    ;
  }

  public String getFqn() {
    return fqn;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
