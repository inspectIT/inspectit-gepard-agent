package rocks.inspectit.gepard.agent.internal.instrumentation.model;

import java.util.List;
import java.util.Objects;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.Scope;

/**
 * @param fqn
 * @param methods
 */
public record InstrumentationScope(String fqn, List<String> methods) {

  /**
   * @param scope
   * @return
   */
  public static InstrumentationScope create(Scope scope) {
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
