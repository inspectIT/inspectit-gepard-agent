package rocks.inspectit.gepard.agent.internal.schedule;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class InspectitSchedulerTest {

  private final InspectitScheduler scheduler = InspectitScheduler.getInstance();

  @Test
  void runnableCanBeScheduledOnlyOnce() {
    Duration duration = Duration.ofMillis(1);

    NamedRunnable dummyRunnable =
        new NamedRunnable() {
          @Override
          public String getName() {
            return "dummy";
          }

          @Override
          public void run() {}
        };

    boolean scheduled1 = scheduler.startRunnable(dummyRunnable, duration);
    boolean scheduled2 = scheduler.startRunnable(dummyRunnable, duration);

    assertTrue(scheduled1);
    assertFalse(scheduled2);
  }

  @Test
  void illegalRunnableNameThrowsException() {
    Duration duration = Duration.ofMillis(1);

    NamedRunnable illegalRunnable =
        new NamedRunnable() {
          @Override
          public String getName() {
            return null;
          }

          @Override
          public void run() {}
        };

    assertThrows(
        IllegalArgumentException.class, () -> scheduler.startRunnable(illegalRunnable, duration));
  }
}
