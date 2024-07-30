package rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation;

/**
 * Represents a scope in the instrumentation configuration. A scope defines a set of methods which
 * should be instrumented.
 */
public class Scope {

  private String fqn;

  private boolean enabled;

  public Scope() {}

  public Scope(String fqn, boolean enabled) {
    this.fqn = fqn;
    this.enabled = enabled;
  }

  public String getFqn() {
    return fqn;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
