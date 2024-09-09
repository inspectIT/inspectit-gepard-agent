package rocks.inspectit.gepard.agent.resolver.matcher;

import net.bytebuddy.description.NamedElement;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.NameMatcher;
import net.bytebuddy.matcher.StringMatcher;

/**
 * This class is a copy InspectIT Ocelot: <a
 * href="https://github.com/inspectIT/inspectit-ocelot/blob/e2e8a4c06ab696a85feee40d2c1f82ec91d3eb07/inspectit-ocelot-core/src/main/java/rocks/inspectit/ocelot/core/instrumentation/config/matcher/SpecialElementMatchers.java#L33">
 * SpecialElementMatchers </a>
 *
 * <p>Original Author: The InspectIT Ocelot team
 *
 * <p>Date Copied: 09.09.2024 This class provides advanced and custom ElementMatchers.
 */
public class CustomElementMatchers {

  /**
   * Creates an {@link ElementMatcher} matching items with the given name settings. Currently, there
   * are no nameSettings available, so we will just use the methodName. Will be enhanced, as soon as
   * the name settings are available.
   */
  public static <T extends NamedElement> ElementMatcher.Junction<T> nameIs(String methodName) {
    return new NameMatcher<>(new StringMatcher(methodName, StringMatcher.Mode.EQUALS_FULLY));
  }
}
