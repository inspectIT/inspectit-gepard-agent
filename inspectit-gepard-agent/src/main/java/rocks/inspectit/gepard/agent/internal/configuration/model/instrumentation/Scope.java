/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation;

import java.util.List;

/**
 * Represents a scope in the instrumentation configuration. A scope defines a set of methods which
 * should be instrumented.
 */
public class Scope {

  private boolean enabled;

  private String fqn;

  private List<String> methods;

  public Scope() {}

  public Scope(boolean enabled, String fqn, List<String> methods) {
    this.fqn = fqn;
    this.methods = methods;
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getFqn() {
    return fqn;
  }

  public List<String> getMethods() {
    return methods;
  }
}
