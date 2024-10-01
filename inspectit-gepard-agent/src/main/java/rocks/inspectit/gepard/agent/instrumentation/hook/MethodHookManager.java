/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

import java.util.*;
import java.util.stream.Collectors;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.ClassHookConfiguration;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.HookedMethods;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;
import rocks.inspectit.gepard.bootstrap.Instances;
import rocks.inspectit.gepard.bootstrap.instrumentation.IHookManager;
import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;
import rocks.inspectit.gepard.bootstrap.instrumentation.noop.NoopHookManager;
import rocks.inspectit.gepard.bootstrap.instrumentation.noop.NoopMethodHook;

/**
 * Implements {@link IHookManager} and serves as singleton, which returns the {@link IMethodHook}
 * for every instrumented method. The hooks are stored within the {@link MethodHookState}.
 *
 * <p>Note: In inspectIT Ocelot we don't implement {@link IHookManager} directly, but instead pass
 * over {@link #getHook} as lambda to {@link Instances}. This should avoid issues with spring
 * annotation scanning. However, since we don't use Spring at the moment, we directly implement the
 * interface.
 */
public class MethodHookManager implements IHookManager {
  private static final Logger log = LoggerFactory.getLogger(MethodHookManager.class);

  /** Stores classes and all of their hooked methods. Will be kept up-to-date during runtime. */
  private final MethodHookState hookState;

  private MethodHookManager() {
    hookState = new MethodHookState();
  }

  /**
   * Factory method to create an {@link MethodHookManager}.
   *
   * @return the created manager
   */
  public static MethodHookManager create() {
    log.debug("Creating MethodHookManager...");
    if (!Instances.hookManager.equals(NoopHookManager.INSTANCE))
      throw new IllegalStateException("Global HookManager already set");

    MethodHookManager methodHookManager = new MethodHookManager();
    Instances.hookManager = methodHookManager;
    addShutdownHook();
    return methodHookManager;
  }

  @Override
  public IMethodHook getHook(Class<?> clazz, String methodSignature) {
    HookedMethods hookedMethods = hookState.getIfPresent(clazz);
    if (Objects.nonNull(hookedMethods)) return hookedMethods.getActiveHook(methodSignature);
    return NoopMethodHook.INSTANCE;
  }

  /**
   * @param clazz the instrumented class
   * @param configuration the instrumentation configuration for the class
   */
  public void updateHooksFor(Class<?> clazz, ClassInstrumentationConfiguration configuration) {
    String className = clazz.getName();
    log.debug("Updating hooks for {}", className);
    Set<MethodDescription.InDefinedShape> instrumentedMethods =
        getInstrumentedMethods(clazz, configuration);

    ClassHookConfiguration classConfiguration = new ClassHookConfiguration();
    instrumentedMethods.forEach(classConfiguration::putHookConfiguration);

    int removeCounter = hookState.removeObsoleteHooks(clazz, instrumentedMethods);
    log.debug("Removed {} obsolete method hooks for {}", removeCounter, className);

    int updateCounter = hookState.updateHooks(clazz, classConfiguration);
    // TODO This should be DEBUG, but should also be visible in tests
    log.info("Updated {} method hooks for {}", updateCounter, className);
  }

  /**
   * Returns all methods of the provided class, which are included in the instrumentation
   * configuration.
   *
   * @param clazz the class containing the methods
   * @param configuration the instrumentation configuration for the class
   * @return the set of all instrumented methods of the class
   */
  private Set<MethodDescription.InDefinedShape> getInstrumentedMethods(
      Class<?> clazz, ClassInstrumentationConfiguration configuration) {
    ElementMatcher.Junction<MethodDescription> methodMatcher = configuration.methodMatcher();
    TypeDescription type = TypeDescription.ForLoadedType.of(clazz);
    return type.getDeclaredMethods().stream()
        .filter(methodMatcher::matches)
        .collect(Collectors.toSet());
  }

  /** Remove at shutdown */
  private static void addShutdownHook() {
    Runtime.getRuntime()
        .addShutdownHook(new Thread(() -> Instances.hookManager = NoopHookManager.INSTANCE));
  }
}