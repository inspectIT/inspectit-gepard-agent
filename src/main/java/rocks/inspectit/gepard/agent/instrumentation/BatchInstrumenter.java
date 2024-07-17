package rocks.inspectit.gepard.agent.instrumentation;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.discovery.ClassDiscoveryListener;

public class BatchInstrumenter implements ClassDiscoveryListener {
  private static final Logger log = LoggerFactory.getLogger(BatchInstrumenter.class);

  private static BatchInstrumenter instance;

  private final List<Class<?>> pendingClasses = new LinkedList<>();

  private BatchInstrumenter() {}

  public static BatchInstrumenter getInstance() {
    if (Objects.isNull(instance)) {
      instance = new BatchInstrumenter();
    }
    return instance;
  }

  @Override
  public void onNewClassesDiscovered(Set<Class<?>> newClasses) {
    pendingClasses.addAll(newClasses);
  }
}
