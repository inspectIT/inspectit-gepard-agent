package rocks.inspectit.gepard.agent.instrumentation;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.schedule.InspectitScheduler;

class InstrumentationManagerTest {

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    Field field = InspectitScheduler.class.getDeclaredField("instance");
    field.setAccessible(true);
    field.set(null, null);
  }

  @Test
  void createCreatesNewInstance() {
    InstrumentationManager manager = InstrumentationManager.create();
    assertNotNull(manager);
  }

  @Test
  void startClassDiscovery() {
    InstrumentationManager manager = InstrumentationManager.create();
    manager.startClassDiscovery();
    assertEquals(1, InspectitScheduler.getInstance().getNumberOfScheduledFutures());
  }

  @Test
  void startBatchInstrumentation() {
    InstrumentationManager manager = InstrumentationManager.create();
    manager.startBatchInstrumentation(null, null);
    assertEquals(1, InspectitScheduler.getInstance().getNumberOfScheduledFutures());
  }
}
