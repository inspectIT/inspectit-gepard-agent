package rocks.inspectit.gepard.agent.resolver.matcher;

import static org.junit.jupiter.api.Assertions.*;
import static rocks.inspectit.gepard.agent.testutils.CustomAssertions.assertNamedElementMatcherMatches;

import net.bytebuddy.description.NamedElement;
import net.bytebuddy.matcher.ElementMatcher;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CustomElementMatchersTest {
  @Nested
  public class NameIs {

    @Test
    public void emptyNameReturnsNull() {
      String method = "";
      ElementMatcher.Junction<NamedElement> result = CustomElementMatchers.nameIs(method);
      assertNull(result);
    }

    @Test
    public void validNameMatcherIsReturned() throws NoSuchMethodException {
      String method = "testMethod";
      ElementMatcher.Junction<NamedElement> result = CustomElementMatchers.nameIs(method);
      assert result != null;

      assertNamedElementMatcherMatches(result, this.getClass(), method);
    }

    public void testMethod() {}
  }
}
