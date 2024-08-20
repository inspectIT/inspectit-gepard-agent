package rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation;

/**
 * Represents a scope in the instrumentation configuration. A scope defines a set of methods which
 * should be instrumented.
 */
public class Scope {

  private String fqn;

  private String method;

  private boolean enabled;

  public Scope() {}

  public Scope(String fqn, boolean enabled) {
    this.fqn = fqn;
    this.enabled = enabled;
  }

  public Scope(String fqn, String method, boolean enabled) {
    this.fqn = fqn;
    this.method = method;
    this.enabled = enabled;
  }

  public String getFqn() {
    return fqn;
  }

  public String getMethod() {
    return method;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
