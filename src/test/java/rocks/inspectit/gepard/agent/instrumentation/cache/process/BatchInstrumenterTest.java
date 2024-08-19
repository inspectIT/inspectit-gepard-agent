package rocks.inspectit.gepard.agent.instrumentation.cache.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.cache.PendingClassesCache;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentationState;
import rocks.inspectit.gepard.agent.resolver.ConfigurationResolver;

@ExtendWith(MockitoExtension.class)
class BatchInstrumenterTest {

  private static final int BATCH_SIZE = 10;

  private final Class<?> TEST_CLASS = BatchInstrumenterTest.class;

  @Mock private Instrumentation instrumentation;

  @Mock private PendingClassesCache cache;

  @Mock private ConfigurationResolver configurationResolver;

  @Mock private InstrumentationState instrumentationState;

  @Test
  void classIsRemovedFromCacheAndNotAddedToBatch() {
    Set<Class<?>> classes = new HashSet<>();
    classes.add(TEST_CLASS);
    when(cache.getKeyIterator()).thenReturn(classes.iterator());
    when(configurationResolver.shouldInstrument(TEST_CLASS)).thenReturn(false);
    when(instrumentationState.isInstrumented(TEST_CLASS)).thenReturn(false);

    BatchInstrumenter instrumenter =
        new BatchInstrumenter(cache, instrumentation, configurationResolver, instrumentationState);
    Set<Class<?>> classesToBeInstrumented = instrumenter.getNextBatch(BATCH_SIZE);

    assertEquals(0, classesToBeInstrumented.size());
    assertEquals(0, classes.size());
  }

  @Test
  void classIsRemovedFromCacheAndAddedToBatch() {
    Set<Class<?>> classes = new HashSet<>();
    classes.add(TEST_CLASS);
    when(cache.getKeyIterator()).thenReturn(classes.iterator());
    when(configurationResolver.shouldInstrument(TEST_CLASS)).thenReturn(true);
    when(instrumentationState.isInstrumented(TEST_CLASS)).thenReturn(false);

    BatchInstrumenter instrumenter =
        new BatchInstrumenter(cache, instrumentation, configurationResolver, instrumentationState);
    Set<Class<?>> classesToBeInstrumented = instrumenter.getNextBatch(BATCH_SIZE);

    assertEquals(1, classesToBeInstrumented.size());
    assertEquals(0, classes.size());
  }

  @Test
  void classIsRetransformed() throws UnmodifiableClassException {
    Set<Class<?>> classes = new HashSet<>();
    classes.add(TEST_CLASS);

    BatchInstrumenter instrumenter =
        new BatchInstrumenter(cache, instrumentation, configurationResolver, instrumentationState);
    instrumenter.retransformBatch(classes);

    verify(instrumentation).retransformClasses(TEST_CLASS);
  }
}