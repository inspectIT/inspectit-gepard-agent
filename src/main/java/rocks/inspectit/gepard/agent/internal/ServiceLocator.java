package rocks.inspectit.gepard.agent.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Service locator for the agent. This provides a central place to register and retrieve services.
 * Great alternative to using a DI framework...
 */
public class ServiceLocator {


    /**
     * The singleton instance of the Service Locator.
     */
    private static ServiceLocator instance;

    /**
        * Map of services registered in the ServiceLocator
     */
    private Map<Class<?>, Object> services = new HashMap<>();

    /**
        * Private constructor to prevent instantiation
     */
    private ServiceLocator() {
    }

    /**
     * Gets the singleton instance of the Service Locator.
     *
     * @return the singleton instance of ServiceLocator
     */
    public static synchronized ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }

    /**
     * Registers a service implementation with the Service Locator.
     *
     * @param clazz   the class type of the service
     * @param service the service implementation instance
     * @param <T>     the type of the service
     */
    public <T> void registerService(Class<T> clazz, T service) {
        services.put(clazz, service);
    }

    /**
     * Retrieves a registered service instance from the Service Locator.
     *
     * @param clazz the class type of the service
     * @param <T>   the type of the service
     * @return the registered service instance
     */
    public <T> T getService(Class<T> clazz) {
        return clazz.cast(services.get(clazz));
    }



}
