package rocks.inspectit.gepard.agent.instrumentation;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.discovery.ClassDiscoveryListener;

public class BatchInstrumenter implements ClassDiscoveryListener {
  private static final Logger log = LoggerFactory.getLogger(BatchInstrumenter.class);

  private final List<Class<?>> pendingClasses = new LinkedList<>();

  @Override
  public void onNewClassesDiscovered(Set<Class<?>> newClasses) {
    pendingClasses.addAll(newClasses);
  }
}
