/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.schedule.InspectitScheduler;

class InstrumentationManagerTest {

  private final InspectitScheduler scheduler = InspectitScheduler.getInstance();

  @BeforeEach
  void beforeEach() {
    scheduler.clearScheduledFutures();
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
    assertEquals(1, scheduler.getNumberOfScheduledFutures());
  }

  @Test
  void startBatchInstrumentation() {
    InstrumentationManager manager = InstrumentationManager.create();
    manager.startBatchInstrumentation(null);
    assertEquals(1, scheduler.getNumberOfScheduledFutures());
  }
}
