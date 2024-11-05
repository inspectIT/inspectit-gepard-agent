/* (C) 2024 */
package rocks.inspectit.gepard.bootstrap.context;

import java.util.Optional;
import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;

/**
 * This context contains internal data, which will be created while entering an instrumented method
 * and can be read while exiting the method. This context should NOT contain any extracted data from
 * the application!
 */
public class InternalInspectitContext {

  /** The hook of the current method */
  private final IMethodHook hook;

  /** The created span scope of the current method */
  private final AutoCloseable spanScope;

  public InternalInspectitContext(IMethodHook hook, AutoCloseable spanScope) {
    this.spanScope = spanScope;
    this.hook = hook;
  }

  /**
   * @return the current hook
   */
  public IMethodHook getHook() {
    return hook;
  }

  /**
   * @return the current span scope
   */
  public Optional<AutoCloseable> getSpanScope() {
    return Optional.ofNullable(spanScope);
  }
}
