/* (C) 2024 */
package rocks.inspectit.gepard.bootstrap.context;

import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;

/**
 * This context contains internal data, which will be created while entering an instrumented method and can be
 * read while exiting the method. This context should NOT contain any extracted data from the application!
 */
public class InternalInspectitContext {

  private final IMethodHook hook;

  private final AutoCloseable spanScope;

  public InternalInspectitContext(IMethodHook hook, AutoCloseable spanScope) {
    this.spanScope = spanScope;
    this.hook = hook;
  }

  public IMethodHook getHook() {
    return hook;
  }

  public AutoCloseable getSpanScope() {
    return spanScope;
  }
}
