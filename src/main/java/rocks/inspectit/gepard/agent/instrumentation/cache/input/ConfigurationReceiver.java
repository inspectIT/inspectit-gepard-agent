package rocks.inspectit.gepard.agent.instrumentation.cache.input;

import java.lang.instrument.Instrumentation;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.cache.PendingClassesCache;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;

/**
 * Listens to {@link ConfigurationReceivedEvent}s and updates the {@link PendingClassesCache}
 * afterward.
 */
public class ConfigurationReceiver implements ConfigurationReceivedObserver {
  private final Logger log = LoggerFactory.getLogger(ConfigurationReceiver.class);

  private final PendingClassesCache pendingClassesCache;

  private final Instrumentation instrumentation;

  public ConfigurationReceiver(
      PendingClassesCache pendingClassesCache, Instrumentation instrumentation) {
    this.pendingClassesCache = pendingClassesCache;
    this.instrumentation = instrumentation;
  }

  @Override
  public void handleConfiguration(ConfigurationReceivedEvent event) {
    log.debug("Received new configuration. Filling instrumentation cache...");
    Class<?>[] classes = instrumentation.getAllLoadedClasses();
    pendingClassesCache.fill(Set.of(classes));
  }
}
