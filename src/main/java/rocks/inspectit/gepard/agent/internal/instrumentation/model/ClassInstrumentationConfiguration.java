package rocks.inspectit.gepard.agent.internal.instrumentation.model;

/**
 * Stores the instrumentation configuration for a specific class. Currently, a class can only be
 * instrumented or not. Later, we could add a list of active rules for example.
 */
public class ClassInstrumentationConfiguration {

  /** Currently, only true */
  private final boolean isInstrumented;

  public ClassInstrumentationConfiguration(boolean isInstrumented) {
    this.isInstrumented = isInstrumented;
  }

  public boolean isInstrumented() {
    return isInstrumented;
  }
}
