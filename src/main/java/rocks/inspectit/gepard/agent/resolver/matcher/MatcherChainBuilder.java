package rocks.inspectit.gepard.agent.resolver.matcher;

import static net.bytebuddy.matcher.ElementMatchers.not;

import net.bytebuddy.matcher.ElementMatcher;

/**
 * This class is a copy InspectIT Ocelot: <a
 * href="https://github.com/inspectIT/inspectit-ocelot/blob/master/inspectit-ocelot-core/src/main/java/rocks/inspectit/ocelot/core/instrumentation/config/matcher/MatcherChainBuilder.java">
 * MatcherChainBuilder </a>
 *
 * <p>Original Author: The InspectIT Ocelot team
 *
 * <p>Date Copied: 09.09.2024

 * Helper class for building and linking {@link ElementMatcher}s. All methods are null-safe. Adding
 * a null matcher will not have an effect on the matcher in construction.
 *
 * @param <T> The matchers generic type. Most of the time it will be TypeDescription or
 *     MethodDescription.
 */
public class MatcherChainBuilder<T> {

  private ElementMatcher.Junction<T> matcher = null;

  public void or(ElementMatcher.Junction<T> nextMatcher) {
    if (nextMatcher != null) {
      if (matcher == null) {
        matcher = nextMatcher;
      } else {
        matcher = matcher.or(nextMatcher);
      }
    }
  }

  public void and(ElementMatcher.Junction<T> nextMatcher) {
    if (nextMatcher != null) {
      if (matcher == null) {
        matcher = nextMatcher;
      } else {
        matcher = matcher.and(nextMatcher);
      }
    }
  }

  public void and(boolean condition, ElementMatcher.Junction<T> nextMatcher) {
    if (nextMatcher != null) {
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
    return matcher == null;
  }
}
