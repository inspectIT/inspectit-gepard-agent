package rocks.inspectit.gepard.agent.internal.configuration.exception;

/** Exception for errors during the deserialization of a configuration string */
public class CouldNotDeserializeConfigurationException extends RuntimeException {

  public CouldNotDeserializeConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
