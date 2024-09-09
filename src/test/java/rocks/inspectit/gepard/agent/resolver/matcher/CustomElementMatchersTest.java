package rocks.inspectit.gepard.agent.resolver.matcher;

import net.bytebuddy.description.NamedElement;
import net.bytebuddy.matcher.ElementMatcher;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        public void validNameMatcherIsReturned () {
            String method = "testMethod";
            ElementMatcher.Junction<NamedElement> result = CustomElementMatchers.nameIs(method);
            assertNotNull(result);
        }
    }

}