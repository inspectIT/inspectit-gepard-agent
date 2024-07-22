package rocks.inspectit.gepard.agent.instrumentation;

import io.opentelemetry.javaagent.bootstrap.InstrumentationHolder;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;

public class ConfigurationReceiver implements ConfigurationReceivedObserver {

  private final Logger logger = LoggerFactory.getLogger(ConfigurationReceiver.class);
  private final Instrumentation instrumentation;

  private final InstrumentationCache instrumentationCache;

  public ConfigurationReceiver(InstrumentationCache instrumentationCache) {
    this.instrumentationCache = instrumentationCache;
    this.instrumentation = InstrumentationHolder.getInstrumentation();
  }

  @Override
  public void handleConfiguration(ConfigurationReceivedEvent event) {
    // Make Map from instrumentation.getAllLoadedClasses()
    Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
    instrumentationCache.addAll(Arrays.stream(loadedClasses).collect(Collectors.toSet()));
  }
}
