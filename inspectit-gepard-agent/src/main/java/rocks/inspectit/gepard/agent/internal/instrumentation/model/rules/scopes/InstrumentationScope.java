/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.instrumentation.model.rules.scopes;

import static net.bytebuddy.matcher.ElementMatchers.*;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.matcher.CustomElementMatchers;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.matcher.MatcherChainBuilder;
import rocks.inspectit.gepard.config.model.instrumentation.scopes.ScopeConfiguration;

/** Internal model, which holds type and method matchers to instrument a scope. */
public class InstrumentationScope {

  /**
   * The method matcher we use in inspectIT, that indicates that all methods should be matched.
   * TODO: Currently, we exclude synthetic methods, since they cause a bug.
   */
  public static final ElementMatcher.Junction<MethodDescription> ALL_METHODS =
      isMethod().and(not(isSynthetic()));

  /** Matcher for types of this scope */
  private final ElementMatcher.Junction<TypeDescription> typeMatcher;

  /** Matcher for methods of this scope */
  private final ElementMatcher.Junction<MethodDescription> methodMatcher;

  private InstrumentationScope(
      @Nonnull ElementMatcher.Junction<TypeDescription> typeMatcher,
      @Nonnull ElementMatcher.Junction<MethodDescription> methodMatcher) {
    this.typeMatcher = typeMatcher;
    this.methodMatcher = methodMatcher;
  }

  /**
   * @return the type matcher of this scope
   */
  public ElementMatcher.Junction<TypeDescription> getTypeMatcher() {
    return typeMatcher;
  }

  /**
   * @return the method matcher of this scope
   */
  public ElementMatcher.Junction<MethodDescription> getMethodMatcher() {
    return methodMatcher;
  }

  /**
   * Creates an {@link InstrumentationScope} out of a {@link ScopeConfiguration}
   *
   * @param scope the scope configuration
   * @return the instrumentation scope
   */
  public static InstrumentationScope create(ScopeConfiguration scope) {
    ElementMatcher.Junction<TypeDescription> typeMatcher = createTypeMatcher(scope);
    ElementMatcher.Junction<MethodDescription> methodMatcher = createMethodMatcher(scope);

    return new InstrumentationScope(typeMatcher, methodMatcher);
  }

  /**
   * @param scope the scope configuration
   * @return the type matcher for the scope (Currently, we just match the fqn of the class)
   */
  private static ElementMatcher.Junction<TypeDescription> createTypeMatcher(
      ScopeConfiguration scope) {
    return named(scope.getFqn());
  }

  /**
   * @param scope the scope configuration
   * @return the method matcher for the scope
   */
  private static ElementMatcher.Junction<MethodDescription> createMethodMatcher(
      ScopeConfiguration scope) {
    MatcherChainBuilder<MethodDescription> matcherChainBuilder = new MatcherChainBuilder<>();
    List<String> methods = scope.getMethods();

    if (methods.isEmpty()) return ALL_METHODS;

    methods.forEach(methodName -> matcherChainBuilder.or(CustomElementMatchers.nameIs(methodName)));
    return matcherChainBuilder.build();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof InstrumentationScope otherScope)
      return typeMatcher.equals(otherScope.typeMatcher)
          && methodMatcher.equals(otherScope.methodMatcher);
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeMatcher, methodMatcher);
  }

  @Override
  public String toString() {
    return "InstrumentationScope {"
        + "typeMatcher = "
        + typeMatcher
        + ", methodMatcher = "
        + methodMatcher
        + '}';
  }
}
