/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.configuration.exception;

import java.io.IOException;

/** Exception for errors during the deserialization of a configuration string */
public class CouldNotDeserializeConfigurationException extends IOException {

  public CouldNotDeserializeConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
