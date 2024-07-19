package rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation;

public class Scope {

  private String fqn;

  private boolean enabled;
  
  public String getFqn() {
    return fqn;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
