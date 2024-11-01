/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action.span.exception;

import io.opentelemetry.context.Scope;

/** Exception errors, while trying to close a {@link Scope} */
public class CouldNotCloseSpanScopeException extends RuntimeException {

  public CouldNotCloseSpanScopeException(Throwable cause) {
    super("Could not close span scope", cause);
  }
}
