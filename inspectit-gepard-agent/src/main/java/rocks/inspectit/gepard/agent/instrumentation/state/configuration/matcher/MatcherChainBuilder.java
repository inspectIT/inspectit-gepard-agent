/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration.matcher;

import static net.bytebuddy.matcher.ElementMatchers.not;

import java.util.Objects;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * This class is a copy InspectIT Ocelot: <a
 * href="https://github.com/inspectIT/inspectit-ocelot/blob/master/inspectit-ocelot-core/src/main/java/rocks/inspectit/ocelot/core/instrumentation/config/matcher/MatcherChainBuilder.java">
 * MatcherChainBuilder </a>
 *
 * <p>Original Author: The InspectIT Ocelot team
 *
 * <p>Date Copied: 09.09.2024
 *
 * <p>Helper class for building and linking {@link ElementMatcher}s. All methods are null-safe.
 * Adding a null matcher will not have an effect on the matcher in construction.
 *
 * @param <T> The matchers generic type. Most of the time it will be TypeDescription or
 *     MethodDescription.
 */
public class MatcherChainBuilder<T> {

  private ElementMatcher.Junction<T> matcher;

  /**
   * Adds the given matcher to the chain using an OR operation.
   *
   * @param nextMatcher the matcher to add
   */
  public void or(ElementMatcher.Junction<T> nextMatcher) {
    if (Objects.nonNull(nextMatcher)) {
      if (Objects.isNull(matcher)) {
        matcher = nextMatcher;
      } else {
        matcher = matcher.or(nextMatcher);
      }
    }
  }

  /**
   * Adds the given matcher to the chain using an AND operation.
   *
   * @param nextMatcher the matcher to add
   */
  public void and(ElementMatcher.Junction<T> nextMatcher) {
    if (Objects.nonNull(nextMatcher)) {
      if (Objects.isNull(matcher)) {
        matcher = nextMatcher;
      } else {
        matcher = matcher.and(nextMatcher);
      }
    }
  }

  /**
   * Adds the negation of the given matcher to the chain using an AND operation.
   *
   * @param nextMatcher the matcher to negate and add
   */
  public void and(boolean condition, ElementMatcher.Junction<T> nextMatcher) {
    if (Objects.nonNull(nextMatcher)) {
      if (condition) {
        and(nextMatcher);
      } else {
        and(not(nextMatcher));
      }
    }
  }

  public ElementMatcher.Junction<T> build() {
    return matcher;
  }

  public boolean isEmpty() {
    return Objects.isNull(matcher);
  }
}
