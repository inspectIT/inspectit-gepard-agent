package rocks.inspectit.gepard.agent.internal.schedule;

import java.util.concurrent.ScheduledFuture;

/** Wrapper class for ScheduledFuture, which also stores the future's name. */
public class NamedScheduledFuture {

  private final ScheduledFuture<?> future;

  private final String name;

  public NamedScheduledFuture(ScheduledFuture<?> future, String name) {
    this.future = future;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void cancel(boolean mayInterruptIfRunning) {
    this.future.cancel(mayInterruptIfRunning);
  }
}
