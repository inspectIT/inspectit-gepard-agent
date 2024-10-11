/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action.exception;

/** Exception errors, while trying to find the SdkSpan class */
public class SdkSpanClassNotFoundException extends RuntimeException {

  public SdkSpanClassNotFoundException(String className, Throwable cause) {
    super("Could not find the class '" + className + "'", cause);
  }
}
