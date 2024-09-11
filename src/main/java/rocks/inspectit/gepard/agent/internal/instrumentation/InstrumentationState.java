package rocks.inspectit.gepard.agent.internal.instrumentation;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import java.util.Objects;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

/** Stores the instrumentation configuration of all instrumented classes. */
public class InstrumentationState {

  /** Store for every instrumented class */
  private final Cache<InstrumentedType, ClassInstrumentationConfiguration> activeInstrumentations;

  private InstrumentationState() {
    this.activeInstrumentations = Caffeine.newBuilder().build();
  }

  /**
   * Factory method to create an {@link InstrumentationState}
   *
   * @return the created state
   */
  public static InstrumentationState create() {
    return new InstrumentationState();
  }

  /**
   * Checks, if the provided class should be retransformed. A retransformation is necessary, if the
   * new configuration differs from the current configuration.
   *
   * @param clazz the class
   * @param newConfig the new instrumentation configuration
   * @return true, if the provided class should be retransformed
   */
  public boolean shouldRetransform(Class<?> clazz, ClassInstrumentationConfiguration newConfig) {
    ClassInstrumentationConfiguration activeConfig =
        activeInstrumentations.asMap().entrySet().stream()
            .filter(entry -> entry.getKey().isEqualTo(clazz)) // find class
            .map(Map.Entry::getValue) // get configuration
            .findAny()
            .orElse(null);

    if (Objects.nonNull(activeConfig)) return !activeConfig.equals(newConfig);
    return newConfig.isActive();
  }

  /**
   * Checks, if the provided type is already instrumented.
   *
   * @param instrumentedType the class type
   * @return true, if the provided type is already instrumented.
   */
  public boolean isInstrumented(InstrumentedType instrumentedType) {
    ClassInstrumentationConfiguration config =
        activeInstrumentations.getIfPresent(instrumentedType);

    if (Objects.nonNull(config)) return config.isActive();
    return false;
  }

  /**
   * Adds the instrumented type to the active instrumentations
   *
   * @param type the instrumented type
   */
  public void addInstrumentedType(
      InstrumentedType type, ClassInstrumentationConfiguration newConfig) {
    activeInstrumentations.put(type, newConfig);
  }

  /**
   * Removes the type from the active instrumentations
   *
   * @param type the uninstrumented type
   */
  public void invalidateInstrumentedType(InstrumentedType type) {
    activeInstrumentations.invalidate(type);
  }
}
