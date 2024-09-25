/* (C) 2024 */
package rocks.inspectit.gepard.agent.testutils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class CustomAssertions {

  public static void assertMethodDescriptionMatcherMatches(
      ElementMatcher.Junction<MethodDescription> matcher, Class<?> clazz, String methodName)
      throws NoSuchMethodException {
    MethodDescription methodDescription =
        new MethodDescription.ForLoadedMethod(clazz.getMethod(methodName));
    assertTrue(matcher.matches(methodDescription));
  }

  public static void assertNamedElementMatcherMatches(
      ElementMatcher.Junction<NamedElement> matcher, Class<?> clazz, String methodName)
      throws NoSuchMethodException {
    NamedElement element = new MethodDescription.ForLoadedMethod(clazz.getMethod(methodName));
    assertTrue(matcher.matches(element));
  }
}
