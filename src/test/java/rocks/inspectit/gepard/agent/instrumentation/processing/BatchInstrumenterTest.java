package rocks.inspectit.gepard.agent.instrumentation.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.PendingClassesCache;
import rocks.inspectit.gepard.agent.internal.configuration.ConfigurationHolder;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.Scope;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;

@ExtendWith(MockitoExtension.class)
class BatchInstrumenterTest {

  private static final int BATCH_SIZE = 10;

  private final Class<?> TEST_CLASS = getClass();

  @Mock private Instrumentation instrumentation;

  @Mock private PendingClassesCache cache;

  @Test
  void classIsRemovedFromCacheAndNotAddedToBatch() {
    Set<Class<?>> classes = new HashSet<>();
    classes.add(TEST_CLASS);
    when(cache.getKeyIterator()).thenReturn(classes.iterator());
    BatchInstrumenter instrumenter = new BatchInstrumenter(cache, instrumentation);

    Set<Class<?>> classesToBeInstrumented = instrumenter.getNextBatch(BATCH_SIZE);

    assertEquals(0, classesToBeInstrumented.size());
    assertEquals(0, classes.size());
  }

  @Test
  void classIsRemovedFromCacheAndAddedToBatch() {
    updateConfigurationHolder();
    Set<Class<?>> classes = new HashSet<>();
    classes.add(TEST_CLASS);
    when(cache.getKeyIterator()).thenReturn(classes.iterator());
    BatchInstrumenter instrumenter = new BatchInstrumenter(cache, instrumentation);

    Set<Class<?>> classesToBeInstrumented = instrumenter.getNextBatch(BATCH_SIZE);

    assertEquals(1, classesToBeInstrumented.size());
    assertEquals(0, classes.size());
  }

  @Test
  void classIsRetransformed() throws UnmodifiableClassException {
    Set<Class<?>> classes = new HashSet<>();
    classes.add(TEST_CLASS);
    BatchInstrumenter instrumenter = new BatchInstrumenter(cache, instrumentation);

    instrumenter.retransformBatch(classes.iterator());

    verify(instrumentation).retransformClasses(TEST_CLASS);
  }

  /** Updates the current {@link InspectitConfiguration} */
  private void updateConfigurationHolder() {
    Scope scope = new Scope(TEST_CLASS.getName(), true);
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(List.of(scope));
    InspectitConfiguration configuration = new InspectitConfiguration(instrumentationConfiguration);

    ConfigurationHolder holder = ConfigurationHolder.getInstance();
    ConfigurationReceivedEvent event = new ConfigurationReceivedEvent(this, configuration);
    holder.handleConfiguration(event);
  }
}
