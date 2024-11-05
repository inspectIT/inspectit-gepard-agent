/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.shutdown;

import java.util.*;

/**
 * Responsible for executing the agents shutdown hooks in the order they are added. We should try to
 * keep the amount of shutdown hooks as well as the logic inside them minimal to prevent long
 * shutdown time.
 */
public class ShutdownHookManager {

  private static ShutdownHookManager instance;

  /** The registered shutdown hooks */
  private final List<Runnable> shutdownHooks;

  private ShutdownHookManager() {
    shutdownHooks = new LinkedList<>();
  }

  /**
   * @return the singleton instance
   */
  public static ShutdownHookManager getInstance() {
    if (Objects.isNull(instance)) {
      instance = new ShutdownHookManager();
      instance.setUpShutdownHooks();
    }
    return instance;
  }

  /** Adds the runnable to the shutdown hooks at the beginning of the list */
  public void addShutdownHook(Runnable runnable) {
    shutdownHooks.add(0, runnable);
  }

  /** Adds the runnable to the shutdown hooks at the end of the list */
  public void addShutdownHookLast(Runnable runnable) {
    shutdownHooks.add(shutdownHooks.size(), runnable);
  }

  /** Sets up the registered shutdown hooks, to be executed at shutdown */
  private void setUpShutdownHooks() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(() -> shutdownHooks.forEach(Runnable::run), "inspectit-shutdown"));
  }

  /**
   * Method for testing.
   *
   * @return the current amount of registered shutdown hooks
   */
  public int getShutdownHookCount() {
    return shutdownHooks.size();
  }

  /** Method for testing. */
  public void clearShutdownHooks() {
    shutdownHooks.clear();
  }
}
