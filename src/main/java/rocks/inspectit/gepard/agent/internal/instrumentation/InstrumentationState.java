package rocks.inspectit.gepard.agent.internal.instrumentation;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Objects;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

/** Stores the instrumentation configuration of all instrumented classes. */
public class InstrumentationState {

  /** Store for every instrumented class */
  private final Cache<Class<?>, ClassInstrumentationConfiguration> activeInstrumentations;

  private InstrumentationState() {
    this.activeInstrumentations = Caffeine.newBuilder().weakKeys().build();
  }

  public static InstrumentationState create() {
    return new InstrumentationState();
  }

  /**
   * Checks, if the provided class type needs to be instrumented.
   *
   * @param clazz the class object
   * @return true, if the provided type needs an instrumentation
   */
  public boolean isInstrumented(Class<?> clazz) {
    ClassInstrumentationConfiguration activeConfig = activeInstrumentations.getIfPresent(clazz);

    if (Objects.nonNull(activeConfig)) return activeConfig.isInstrumented();
    return false;
  }

  /**
   * Adds the provided class type to the active instrumentations
   *
   * @param clazz the class object
   */
  public void addInstrumentation(Class<?> clazz) {
    ClassInstrumentationConfiguration newConfig = new ClassInstrumentationConfiguration(true);
    activeInstrumentations.put(clazz, newConfig);
  }

  /**
   * Removes the provided class type from the active instrumentations
   *
   * @param clazz the class object
   */
  public void invalidateInstrumentation(Class<?> clazz) {
    activeInstrumentations.invalidate(clazz);
  }
}
