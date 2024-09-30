/* (C) 2024 */
package rocks.inspectit.gepard.bootstrap;

import rocks.inspectit.gepard.bootstrap.instrumentation.IHookManager;
import rocks.inspectit.gepard.bootstrap.instrumentation.noop.NoopHookManager;

/**
 * Accessor for implementations of shared interfaces. These interfaces should be accessible in the
 * target application as well as the gepard agent. The values will be replaced by the actual
 * implementations when a gepard-agent is started.
 */
public class Instances {

  private Instances() {}

  // Must not be final
  public static IHookManager hookManager = NoopHookManager.INSTANCE;
}
