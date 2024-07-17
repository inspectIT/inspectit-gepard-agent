package rocks.inspectit.gepard.agent.internal.exception;

/** Exception, when an unregistered inspectit service was tried to be accessed */
public class InspectitServiceNotFoundException extends RuntimeException {

  public InspectitServiceNotFoundException(Class<?> clazz) {
    super("Inspectit service " + clazz.getName() + " not found");
  }
}
