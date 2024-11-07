/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.shutdown;

import com.google.common.annotations.VisibleForTesting;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for executing the agents shutdown hooks in the order they are added. We should try to
 * keep the amount of shutdown hooks as well as the logic inside them minimal to prevent long
 * shutdown time.
 */
public class ShutdownHookManager {
  private static final Logger log = LoggerFactory.getLogger(ShutdownHookManager.class);

  private static ShutdownHookManager instance;

  /** The registered shutdown hooks */
  private final Set<ShutdownHook> shutdownHooks;

  private final AtomicBoolean isShutdown;

  private ShutdownHookManager() {
    shutdownHooks = Collections.synchronizedSet(new HashSet<>());
    isShutdown = new AtomicBoolean(false);
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

  /**
   * Adds the runnable to the shutdown hooks at the beginning of the set. During shutdown, no new
   * hooks can be added.
   */
  public void addShutdownHook(Runnable runnable) {
    if (!isShutdown.get()) {
      ShutdownHook shutdownHook = new ShutdownHook(runnable, 0);
      shutdownHooks.add(shutdownHook);
    }
  }

  /**
   * Adds the runnable to the shutdown hooks at the end of the set. During shutdown, no new hooks
   * can be added.
   */
  public void addShutdownHookLast(Runnable runnable) {
    if (!isShutdown.get()) {
      ShutdownHook shutdownHook = new ShutdownHook(runnable, Integer.MAX_VALUE);
      shutdownHooks.add(shutdownHook);
    }
  }

  /** Sets up the registered shutdown hooks, to be executed at shutdown */
  private void setUpShutdownHooks() {
    Runtime.getRuntime()
        .addShutdownHook(new Thread(this::executeShutdownHooks, "inspectit-shutdown"));
  }

  /** Executes all registered shutdown hooks by order */
  @VisibleForTesting
  void executeShutdownHooks() {
    if (!isShutdown.compareAndSet(false, true)) log.info("Cannot execute shutdown hooks twice");
    else
      shutdownHooks.stream()
          .sorted(Comparator.comparingInt(ShutdownHook::getOrder))
          .forEach(this::tryRunShutdownHook);
  }

  /** Try-catch-wrapper to run a shutdown hook */
  private void tryRunShutdownHook(ShutdownHook shutdownHook) {
    try {
      shutdownHook.run();
    } catch (Exception e) {
      log.error("Error while executing shutdown hook", e);
    }
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
  public void reset() {
    shutdownHooks.clear();
    isShutdown.set(false);
  }

  /** Internal class for ordered shutdown hooks */
  private static class ShutdownHook {

    private final Runnable shutdownHook;

    private final int order;

    ShutdownHook(Runnable shutdownHook, int order) {
      this.shutdownHook = shutdownHook;
      this.order = order;
    }

    void run() {
      shutdownHook.run();
    }

    int getOrder() {
      return order;
    }
  }
}
