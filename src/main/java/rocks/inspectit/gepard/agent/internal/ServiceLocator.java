package rocks.inspectit.gepard.agent.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.exception.InspectitServiceNotFoundException;

/**
 * Service locator pattern for the agent, since currently we don't use any dependency-injection
 * framework. This provides a central place to register and retrieve services.
 */
public class ServiceLocator {
  private static final Logger log = LoggerFactory.getLogger(ServiceLocator.class);

  /** Map of services registered in the ServiceLocator */
  private static final ConcurrentMap<Class<?>, Object> services = new ConcurrentHashMap<>();

  private ServiceLocator() {}

  /**
   * Registers a service implementation with the ServiceLocator.
   *
   * @param service the service implementation instance
   */
  public static <T> void registerService(Class<T> serviceClass, T service) {
    String className = serviceClass.getName();
    if (services.containsKey(serviceClass))
      log.debug("Service already registered for class {}", className);
    else {
      services.put(serviceClass, service);
      log.debug("Service registered for class {}", className);
    }
  }

  /**
   * Retrieves a registered service instance from the ServiceLocator.
   *
   * @param clazz the class type of the service
   * @return the registered service instance
   */
  public static <T> T getService(Class<T> clazz) {
    if (!services.containsKey(clazz)) throw new InspectitServiceNotFoundException(clazz);
    return clazz.cast(services.get(clazz));
  }
}
