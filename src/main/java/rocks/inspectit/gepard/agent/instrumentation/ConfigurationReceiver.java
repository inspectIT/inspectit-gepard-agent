package rocks.inspectit.gepard.agent.instrumentation;

import io.opentelemetry.javaagent.bootstrap.InstrumentationHolder;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;

public class ConfigurationReceiver implements ConfigurationReceivedObserver {

  private final Logger logger = LoggerFactory.getLogger(ConfigurationReceiver.class);
  private final Instrumentation instrumentation;

  private final ClassQueue classQueue;

  public ConfigurationReceiver(ClassQueue classQueue) {
    this.classQueue = classQueue;
    this.instrumentation = InstrumentationHolder.getInstrumentation();
  }

  @Override
  public void handleConfiguration(ConfigurationReceivedEvent event) {
    Collections.addAll(
        Arrays.asList(classQueue.getPendingClasses().toArray()),
        instrumentation.getAllLoadedClasses());
  }
}
