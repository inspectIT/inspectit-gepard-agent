package rocks.inspectit.gepard.agent.resolver.matcher;

import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.bytebuddy.matcher.ElementMatchers.not;
import static org.junit.jupiter.api.Assertions.*;

class MatcherChainBuilderTest {
    private MatcherChainBuilder<Object> builder;

    private ElementMatcher.Junction<Object> anyMatcher = ElementMatchers.any();

    @BeforeEach
    public void beforeEach() {
        builder = new MatcherChainBuilder<>();
    }

    @Nested
    public class Or {

        @Test
        public void nullMatcher() {
            builder.or(null);

            assertNull(builder.build());
            assertTrue(builder.isEmpty());
        }

        @Test
        public void singleMatcher() {
            builder.or(anyMatcher);

            Assertions.assertEquals(builder.build(), anyMatcher);
            assertFalse(builder.isEmpty());
        }

        @Test
        public void multipleMatcher() {
            builder.or(anyMatcher);
            builder.or(anyMatcher);

            assertEquals(builder.build(), anyMatcher.or(anyMatcher));
        }

        @Test
        public void nullOnNotEmpty() {
            builder.or(anyMatcher);
            builder.or(null);

            assertEquals(builder.build(), anyMatcher);
        }
    }

    @Nested
    public class And {

        @Test
        public void nullMatcher() {
            builder.and(null);

            assertNull(builder.build());
            assertTrue(builder.isEmpty());
        }

        @Test
        public void singleMatcher() {
            builder.and(anyMatcher);

            assertEquals(builder.build(), anyMatcher);
            assertFalse(builder.isEmpty());
        }

        @Test
        public void multipleMatcher() {
            builder.and(anyMatcher);
            builder.and(anyMatcher);

            assertEquals(builder.build(), anyMatcher.and(anyMatcher));
        }

        @Test
        public void nullOnNotEmpty() {
            builder.and(anyMatcher);
            builder.and(null);

            assertEquals(builder.build(), anyMatcher);
        }

        @Test
        public void conditionalTrue() {
            builder.and(true, anyMatcher);

            assertEquals(builder.build(), anyMatcher);
        }

        @Test
        public void conditionalFalse() {
            builder.and(false, anyMatcher);

            assertEquals(builder.build(), not(anyMatcher));
        }

        @Test
        public void conditionalFalseOnNotEmpty() {
            builder.and(anyMatcher);
            builder.and(false, anyMatcher);

            assertEquals(builder.build(), anyMatcher.and(not(anyMatcher)));
        }
    }

    @Nested
    public class IsEmpty {

        @Test
        public void empty() {
            boolean result = builder.isEmpty();

            assertTrue(result);
        }

        @Test
        public void notEmpty() {
            builder.and(ElementMatchers.any());

            boolean result = builder.isEmpty();

            assertFalse(result);
        }
    }

}