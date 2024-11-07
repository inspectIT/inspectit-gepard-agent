/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.shutdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShutdownHookManagerTest {

  private final ShutdownHookManager manager = ShutdownHookManager.getInstance();

  @BeforeEach
  void beforeEach() {
    manager.reset();
  }

  @Test
  void shouldRunShutdownHooksInOrder() {
    Runnable runnable1 = mock(Runnable.class);
    Runnable runnable2 = mock(Runnable.class);
    manager.addShutdownHookLast(runnable1);
    manager.addShutdownHook(runnable2);

    manager.executeShutdownHooks();

    InOrder inOrder = Mockito.inOrder(runnable1, runnable2);
    inOrder.verify(runnable2).run();
    inOrder.verify(runnable1).run();
    assertEquals(2, manager.getShutdownHookCount());
  }

  @Test
  void shouldRunShutdownHooksWithoutExceptions() {
    Runnable runnable1 = mock(Runnable.class);
    Runnable runnable2 = mock(Runnable.class);
    manager.addShutdownHook(runnable1);
    manager.addShutdownHookLast(runnable2);
    doThrow(RuntimeException.class).when(runnable1).run();

    manager.executeShutdownHooks();

    verify(runnable1).run();
    verify(runnable2).run();
  }
}
