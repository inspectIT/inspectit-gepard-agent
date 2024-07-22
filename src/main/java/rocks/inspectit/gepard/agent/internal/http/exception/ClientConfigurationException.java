package rocks.inspectit.gepard.agent.internal.http.exception;

/** Exception for errors in the configuration of the HTTP client */
public class ClientConfigurationException extends RuntimeException {

  public ClientConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
