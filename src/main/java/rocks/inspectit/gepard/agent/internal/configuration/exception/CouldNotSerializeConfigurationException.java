package rocks.inspectit.gepard.agent.internal.configuration.exception;

/** Exception for errors during the serialization of a configuration string */
public class CouldNotSerializeConfigurationException extends RuntimeException {

  public CouldNotSerializeConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
