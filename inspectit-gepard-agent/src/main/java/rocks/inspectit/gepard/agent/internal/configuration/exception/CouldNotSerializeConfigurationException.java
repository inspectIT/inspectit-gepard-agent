/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.configuration.exception;

import java.io.IOException;

/** Exception for errors during the serialization of a configuration string */
public class CouldNotSerializeConfigurationException extends IOException {

  public CouldNotSerializeConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
