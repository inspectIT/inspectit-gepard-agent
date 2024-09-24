/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.schedule;

/** Extends the Runnable interface, so they can be differentiated */
public interface NamedRunnable extends Runnable {

  /**
   * @return the name of this runnable. Should be unique.
   */
  String getName();
}
