package rocks.inspectit.gepard.agent.internal.eventbus;

import com.google.common.eventbus.EventBus;
import rocks.inspectit.gepard.agent.internal.ServiceLocator;

/**
 * Initializes the {@link EventBus} and registers it in the {@link ServiceLocator}.
 */
public class EventBusInitializer {

    private EventBusInitializer() {
    }

    public static void initialize() {
        ServiceLocator.getInstance().registerService(EventBus.class, new EventBus());
    }
}
