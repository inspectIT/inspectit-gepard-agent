/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.shutdown;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShutdownHookManagerTest {

  private final ShutdownHookManager manager = ShutdownHookManager.getInstance();

  @BeforeEach
  void beforeEach() {
    manager.clearShutdownHooks();
  }

  @Test
  void shouldAddShutdownHook() {
    Runnable shutdownHook = () -> System.out.println("shutdown");

    manager.addShutdownHook(shutdownHook);
    manager.addShutdownHookLast(shutdownHook);

    assertEquals(2, manager.getShutdownHookCount());
  }
}
