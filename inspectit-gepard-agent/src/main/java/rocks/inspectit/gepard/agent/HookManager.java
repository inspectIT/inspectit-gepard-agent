/* (C) 2024 */
package rocks.inspectit.gepard.agent;

import rocks.inspectit.gepard.bootstrap.Instances;
import rocks.inspectit.gepard.bootstrap.instrumentation.IHookManager;
import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;
import rocks.inspectit.gepard.bootstrap.instrumentation.noop.NoopHookManager;

/**
 * TODO
 *
 * <p>Note: In inspectIT Ocelot we don't implement {@link IHookManager} directly, but instead pass
 * over {@link #getHook} as lambda to {@link Instances}. This should avoid issues with spring
 * annotation scanning. However, since we don't use Spring at the moment, we directly implement the
 * interface.
 */
public class HookManager implements IHookManager {

  private HookManager() {}

  /**
   * Factory method to create an {@link HookManager}
   *
   * @return the created manager
   */
  public static HookManager create() {
    HookManager hookManager = new HookManager();
    Instances.hookManager = hookManager;
    addShutdownHook();
    return hookManager;
  }

  @Override
  public IMethodHook getHook(Class<?> clazz, String methodSignature) {
    // TODO
    return null;
  }

  /** Destroy at shutdown */
  private static void addShutdownHook() {
    Instances.hookManager = NoopHookManager.INSTANCE;
  }
}
