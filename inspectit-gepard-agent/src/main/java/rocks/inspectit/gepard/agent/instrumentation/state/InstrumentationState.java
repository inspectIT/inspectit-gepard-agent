/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHookManager;
import rocks.inspectit.gepard.agent.instrumentation.state.configuration.resolver.ConfigurationResolver;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;

/** Stores the instrumentation configuration of all instrumented classes. */
public class InstrumentationState {
  private static final Logger log = LoggerFactory.getLogger(InstrumentationState.class);

  /** Store for every instrumented class */
  private final Cache<InstrumentedType, ClassInstrumentationConfiguration> activeInstrumentations;

  private final ConfigurationResolver configurationResolver;

  private final MethodHookManager methodHookManager;

  private InstrumentationState(
      ConfigurationResolver configurationResolver, MethodHookManager methodHookManager) {
    this.activeInstrumentations = Caffeine.newBuilder().build();
    this.configurationResolver = configurationResolver;
    this.methodHookManager = methodHookManager;
  }

  /**
   * Factory method to create an {@link InstrumentationState}
   *
   * @return the created state
   */
  public static InstrumentationState create(
      ConfigurationResolver configurationResolver, MethodHookManager methodHookManager) {
    return new InstrumentationState(configurationResolver, methodHookManager);
  }

  /**
   * Checks, if the provided class should be retransformed. A retransformation is necessary, if the
   * new configuration differs from the current configuration. Additionally, we trigger the update
   * of the classes method hooks, if necessary.
   *
   * @param clazz the class
   * @return true, if the provided class should be retransformed
   */
  public boolean shouldRetransform(Class<?> clazz) {
    InstrumentedType type = new InstrumentedType(clazz, clazz.getClassLoader());
    ClassInstrumentationConfiguration currentConfig = activeInstrumentations.getIfPresent(type);
    ClassInstrumentationConfiguration newConfig =
        configurationResolver.getClassInstrumentationConfiguration(type);

    updateMethodHooks(clazz, currentConfig, newConfig);

    if (Objects.nonNull(currentConfig)) return !currentConfig.equals(newConfig);
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

  /**
   * Checks, if the instrumentation configurations of the provided class require method hooks. If
   * one of the configurations is active, we require method hooks for them. Then we should update
   * the method hooks.
   *
   * @param clazz the current class
   * @param currentConfig the current instrumentation configuration of the class
   * @param newConfig the new instrumentation configuration of the class
   */
  private void updateMethodHooks(
      Class<?> clazz,
      ClassInstrumentationConfiguration currentConfig,
      ClassInstrumentationConfiguration newConfig) {
    boolean newConfigRequiresHooks = newConfig.isActive();
    boolean currentConfigRequiresHooks = Objects.nonNull(currentConfig) && currentConfig.isActive();

    if (newConfigRequiresHooks || currentConfigRequiresHooks)
      try {
        methodHookManager.updateHooksFor(clazz, newConfig);
      } catch (Exception e) {
        log.error("There was an error while updating the hooks of class {}", clazz.getName(), e);
      }
  }
}
