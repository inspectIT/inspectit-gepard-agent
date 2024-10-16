/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.instrumentation.model;

import java.util.List;
import java.util.Objects;
import rocks.inspectit.gepard.config.model.instrumentation.scopes.ScopeConfiguration;

/**
 * @param fqn the fully qualified name of a class
 * @param methods the methods of the class to instrument
 */
public record InstrumentationScope(String fqn, List<String> methods) {

  /**
   * Creates an {@link InstrumentationScope} out of a {@link ScopeConfiguration}
   *
   * @param scope the scope
   * @return the instrumentation scope
   */
  public static InstrumentationScope create(ScopeConfiguration scope) {
    return new InstrumentationScope(scope.getFqn(), scope.getMethods());
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof InstrumentationScope otherScope)
      return fqn.equals(otherScope.fqn) && methods.equals(otherScope.methods);
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(fqn, methods);
  }
}
