package rocks.inspectit.gepard.agent.resolver.scope;

import java.util.Collection;
import java.util.List;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.Scope;
import rocks.inspectit.gepard.agent.resolver.ConfigurationHolder;
import rocks.inspectit.gepard.agent.resolver.matcher.CustomElementMatchers;
import rocks.inspectit.gepard.agent.resolver.matcher.MatcherChainBuilder;

/**
 * This class is used to resolve the {@link Scope} based on the {@link Scope} List, contained in the
 * {@link InstrumentationConfiguration}.
 */
public class ScopeResolver {

  ConfigurationHolder holder;

  public ScopeResolver(ConfigurationHolder holder) {
    this.holder = holder;
  }

  /**
   * Checks, if the provided class name requires instrumentation.
   *
   * @param fullyQualifiedName the full name of the class
   * @return true, if the provided class should be instrumented
   */
  public boolean shouldInstrument(String fullyQualifiedName) {

    List<Scope> scopes = getScopes();

    return !shouldIgnore(fullyQualifiedName)
        && scopes.stream()
            .anyMatch(scope -> scope.getFqn().equals(fullyQualifiedName) && scope.isEnabled());
  }

  /**
   * Builds a matcher for the methods of the provided type.
   *
   * @param typeDescription the type to build the method matcher for
   * @return the matcher for the methods
   */
  public ElementMatcher.Junction<MethodDescription> buildMethodMatcher(
      TypeDescription typeDescription) {

    List<Scope> scopes = getAllMatchingScopes(typeDescription.getName());

    if (containsWholeClassScope(scopes)) {
      return ElementMatchers.isMethod();
    }

    List<String> methodNames = collectMethodNames(scopes);

    return buildMatcherForMethods(methodNames);
  }

  /**
   * Checks if the provided scope contains method definitions.
   *
   * @param scope the scope to check
   * @return true if the scope contains method definitions
   */
  private boolean isWholeClassScope(Scope scope) {
    return scope.getMethods() == null || scope.getMethods().isEmpty();
  }

  /**
   * Checks if the provided list of scopes contains at least one whole class scope.
   *
   * @param scopes the list of scopes to check
   * @return true if the list of scopes contains at least one whole class scope
   */
  private boolean containsWholeClassScope(List<Scope> scopes) {
    return scopes.stream().anyMatch(this::isWholeClassScope);
  }

  /**
   * Collects all method names from the provided list of scopes.
   *
   * @param scopes the list of scopes to collect the method names from
   * @return the list of method names
   */
  private List<String> collectMethodNames(List<Scope> scopes) {
    return scopes.stream()
        .map(Scope::getMethods) // Map Scope to its methods list
        .flatMap(Collection::stream) // Flatten the list of methods
        .toList();
  }

  /**
   * Builds a matcher for each method name and chains them using 'or'.
   *
   * @param methodNames the method names to build matchers for
   * @return a matcher for the methods
   */
  private ElementMatcher.Junction<MethodDescription> buildMatcherForMethods(
      List<String> methodNames) {
    MatcherChainBuilder<MethodDescription> matcherChainBuilder =
        new MatcherChainBuilder<>();
    methodNames.forEach(
        methodName -> matcherChainBuilder.or(CustomElementMatchers.nameIs(methodName)));
    return matcherChainBuilder.build();
  }

  /**
   * Checks, if the type should be able to be instrumented. Currently, we don't instrument lambda-
   * or array classes.
   *
   * @param fullyQualifiedName the full name of the class
   * @return true, if the provided type should NOT be able to be instrumented
   */
  private boolean shouldIgnore(String fullyQualifiedName) {
    return fullyQualifiedName.contains("$$Lambda") || fullyQualifiedName.startsWith("[");
  }

  /**
   * Returns the list of scopes from the current {@link InstrumentationConfiguration}.
   *
   * @return the list of scopes
   */
  private List<Scope> getScopes() {
    return holder.getConfiguration().getInstrumentation().getScopes();
  }

  private List<Scope> getAllMatchingScopes(String fqn) {
    return holder.getConfiguration().getInstrumentation().getAllMatchingScopes(fqn);
  }
}
