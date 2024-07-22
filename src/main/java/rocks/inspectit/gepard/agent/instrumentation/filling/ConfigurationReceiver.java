package rocks.inspectit.gepard.agent.instrumentation.filling;

import io.opentelemetry.javaagent.bootstrap.InstrumentationHolder;
import java.lang.instrument.Instrumentation;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.InstrumentationCache;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;

public class ConfigurationReceiver implements ConfigurationReceivedObserver {
  private final Logger log = LoggerFactory.getLogger(ConfigurationReceiver.class);

  private final InstrumentationCache instrumentationCache;

  private final Instrumentation instrumentation;

  public ConfigurationReceiver(InstrumentationCache instrumentationCache) {
    this.instrumentationCache = instrumentationCache;
    this.instrumentation = InstrumentationHolder.getInstrumentation();
  }

  @Override
  public void handleConfiguration(ConfigurationReceivedEvent event) {
    log.debug("Received new configuration. Filling instrumentation cache...");
    Class<?>[] classes = instrumentation.getAllLoadedClasses();
    instrumentationCache.fill(List.of(classes));
  }
}
