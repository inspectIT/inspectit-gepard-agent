package rocks.inspectit.gepard.agent.state.scope;

import java.util.*;
import java.util.stream.Collectors;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.Scope;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.InstrumentationScope;
import rocks.inspectit.gepard.agent.state.InspectitConfigurationHolder;
import rocks.inspectit.gepard.agent.state.matcher.CustomElementMatchers;
import rocks.inspectit.gepard.agent.state.matcher.MatcherChainBuilder;

/**
 * This class is used to resolve the {@link Scope} based on the {@link Scope} List, contained in the
 * {@link InstrumentationConfiguration}.
 */
public class ScopeResolver {

  private final InspectitConfigurationHolder holder;

  public ScopeResolver(InspectitConfigurationHolder holder) {
    this.holder = holder;
  }

  /**
   * Finds all active scopes of the specified class for instrumentation.
   *
   * @param fullyQualifiedName the name of the class
   * @return the set of active scopes for instrumentation
   */
  public Set<InstrumentationScope> getActiveScopes(String fullyQualifiedName) {
    if (shouldIgnore(fullyQualifiedName)) return Collections.emptySet();

    List<Scope> scopes = getAllMatchingScopes(fullyQualifiedName);
    return scopes.stream()
        .filter(Scope::isEnabled)
        .map(InstrumentationScope::create)
        .collect(Collectors.toSet());
  }

  /**
   * Creates a matcher for the methods of the provided scopes.
   *
   * @param scopes the scopes containing methods
   * @return the matcher for the methods of the scopes
   */
  public ElementMatcher.Junction<MethodDescription> getMethodMatcher(
      Set<InstrumentationScope> scopes) {
    if (containsAllMethodsScope(scopes)) return ElementMatchers.isMethod();

    Set<String> methodNames = collectMethodNames(scopes);
    return buildMatcherForMethods(methodNames);
  }

  /**
   * Returns all scopes, which match the provided fully qualified name.
   *
   * @param fqn the fully qualified name to match
   * @return the list of matching scopes
   */
  private List<Scope> getAllMatchingScopes(String fqn) {
    return holder.getConfiguration().getInstrumentation().getAllMatchingScopes(fqn);
  }

  /**
   * Checks if the provided list of scopes contains at least one whole class scope.
   *
   * @param scopes the list of scopes to check
   * @return true if the list of scopes contains at least one whole class scope
   */
  private boolean containsAllMethodsScope(Set<InstrumentationScope> scopes) {
    return scopes.stream().anyMatch(this::isAllMethodsScope);
  }

  /**
   * Checks if the provided scope contains method definitions.
   *
   * @param scope the scope to check
   * @return true if the scope contains method definitions
   */
  private boolean isAllMethodsScope(InstrumentationScope scope) {
    return scope.methods().isEmpty();
  }

  /**
   * Collects all method names from the provided list of scopes.
   *
   * @param scopes the list of scopes to collect the method names from
   * @return the set of method names
   */
  private Set<String> collectMethodNames(Set<InstrumentationScope> scopes) {
    return scopes.stream()
        .map(InstrumentationScope::methods)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  /**
   * Builds a matcher for each method name and chains them using 'or'.
   *
   * @param methodNames the method names to build matchers for
   * @return a matcher for the methods
   */
  private ElementMatcher.Junction<MethodDescription> buildMatcherForMethods(
      Set<String> methodNames) {
    MatcherChainBuilder<MethodDescription> matcherChainBuilder = new MatcherChainBuilder<>();
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
}
