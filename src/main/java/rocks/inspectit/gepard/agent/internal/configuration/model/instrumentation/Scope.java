package rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation;

import java.util.List;

/**
 * Represents a scope in the instrumentation configuration. A scope defines a set of methods which
 * should be instrumented.
 */
public class Scope {

  private String fqn;

  private List<String> methods;

  private boolean enabled;

  public Scope() {}

  public Scope(String fqn, boolean enabled) {
    this.fqn = fqn;
    this.enabled = enabled;
  }

  public Scope(String fqn, List<String> methods, boolean enabled) {
    this.fqn = fqn;
    this.methods = methods;
    this.enabled = enabled;
  }

  public String getFqn() {
    return fqn;
  }

  public List<String> getMethods() {
    return methods;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
