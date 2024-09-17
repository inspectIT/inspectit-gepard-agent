package rocks.inspectit.gepard.agent.state;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import java.util.Objects;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

/** Stores the instrumentation configuration of all instrumented classes. */
public class InstrumentationState {

  /** Store for every instrumented class */
  private final Cache<InstrumentedType, ClassInstrumentationConfiguration> activeInstrumentations;

  private final ConfigurationResolver configurationResolver;

  private InstrumentationState(ConfigurationResolver configurationResolver) {
    this.activeInstrumentations = Caffeine.newBuilder().build();
    this.configurationResolver = configurationResolver;
  }

  /**
   * Factory method to create an {@link InstrumentationState}
   *
   * @return the created state
   */
  public static InstrumentationState create(ConfigurationResolver configurationResolver) {
    return new InstrumentationState(configurationResolver);
  }

  /**
   * Checks, if the provided class should be retransformed. A retransformation is necessary, if the
   * new configuration differs from the current configuration.
   *
   * @param clazz the class
   * @return true, if the provided class should be retransformed
   */
  public boolean shouldRetransform(Class<?> clazz) {
    ClassInstrumentationConfiguration activeConfig =
        activeInstrumentations.asMap().entrySet().stream()
            .filter(entry -> entry.getKey().isEqualTo(clazz)) // find class
            .map(Map.Entry::getValue) // get configuration
            .findAny()
            .orElse(null);

    ClassInstrumentationConfiguration newConfig =
        configurationResolver.getClassInstrumentationConfiguration(clazz);

    if (Objects.nonNull(activeConfig)) return !activeConfig.equals(newConfig);
    return newConfig.isActive();
  }

  /**
   * Checks, if the provided type is already instrumented.
   *
   * @param instrumentedType the class type
   * @return true, if the provided type is already instrumented.
   */
  public boolean isActive(InstrumentedType instrumentedType) {
    ClassInstrumentationConfiguration config =
        activeInstrumentations.getIfPresent(instrumentedType);

    if (Objects.nonNull(config)) return config.isActive();
    return false;
  }

  /**
   * @param type the type to instrument
   * @return The active configuration or {@link
   *     ClassInstrumentationConfiguration#NO_INSTRUMENTATION}
   */
  public ClassInstrumentationConfiguration resolveClassConfiguration(InstrumentedType type) {
    return configurationResolver.getClassInstrumentationConfiguration(type);
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
