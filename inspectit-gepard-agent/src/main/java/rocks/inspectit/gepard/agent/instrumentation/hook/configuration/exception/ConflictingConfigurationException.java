/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.configuration.exception;

/** Exception errors, while finding conflicts inside the instrumentation configuration. */
public class ConflictingConfigurationException extends RuntimeException {

  public ConflictingConfigurationException(String message) {
    super(message);
  }
}
