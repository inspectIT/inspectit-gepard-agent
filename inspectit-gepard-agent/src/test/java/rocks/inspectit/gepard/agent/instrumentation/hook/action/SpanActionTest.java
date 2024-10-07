/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.exception.CouldNotCloseSpanScopeException;

@ExtendWith(MockitoExtension.class)
class SpanActionTest {

  @Mock private AutoCloseable closeable;

  private final SpanAction action = new SpanAction();

  @Test
  void shouldCreateScope() {
    String spanName = "Test.method";

    AutoCloseable scope = action.startSpan(spanName);

    assertNotNull(scope);
  }

  @Test
  void shouldCloseScope() throws Exception {
    action.endSpan(closeable);

    verify(closeable).close();
  }

  @Test
  void shouldThrowExceptionWhenScopeNotClosable() throws Exception {
    doThrow(Exception.class).when(closeable).close();

    assertThrows(CouldNotCloseSpanScopeException.class, () -> action.endSpan(closeable));
  }
}
