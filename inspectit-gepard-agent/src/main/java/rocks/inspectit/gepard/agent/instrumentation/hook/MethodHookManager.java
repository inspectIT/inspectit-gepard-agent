/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
 * TODO implement, add java doc
 *
 * <p>Note: In inspectIT Ocelot we don't implement {@link IHookManager} directly, but instead pass
 * over {@link #getHook} as lambda to {@link Instances}. This should avoid issues with spring
 * annotation scanning. However, since we don't use Spring at the moment, we directly implement the
 * interface.
 */
public class MethodHookManager implements IHookManager {
  private static final Logger log = LoggerFactory.getLogger(MethodHookManager.class);

  /** Stores classes and all of their hooked methods. Will be kept up-to-date during runtime. */
  private final Cache<Class<?>, HookedMethods> hooks;

  private MethodHookManager() {
    hooks = Caffeine.newBuilder().weakKeys().build();
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
    HookedMethods hookedMethods = hooks.getIfPresent(clazz);
    if (Objects.nonNull(hookedMethods)) return hookedMethods.getActiveHook(methodSignature);
    return NoopMethodHook.INSTANCE;
  }

  public void updateHooksFor(Class<?> clazz, ClassInstrumentationConfiguration configuration) {
    log.info("Updating hooks for {}", clazz.getName());
    ElementMatcher.Junction<MethodDescription> methodMatcher = configuration.methodMatcher();
    TypeDescription type = TypeDescription.ForLoadedType.of(clazz);
    Set<MethodDescription.InDefinedShape> matchedMethods =
        type.getDeclaredMethods().stream()
            .filter(methodMatcher::matches)
            .collect(Collectors.toSet());

    ClassHookConfiguration classConfiguration = new ClassHookConfiguration();
    if (!matchedMethods.isEmpty()) matchedMethods.forEach(classConfiguration::putHookConfiguration);

    removeObsoleteHooks(clazz, matchedMethods);
    updateHooks(clazz, classConfiguration);
  }

  private void removeObsoleteHooks(
      Class<?> clazz, Set<MethodDescription.InDefinedShape> matchedMethods) {
    Set<String> matchedSignatures =
        matchedMethods.stream().map(this::getSignature).collect(Collectors.toSet());

    HookedMethods hookedMethods = hooks.get(clazz, c -> new HookedMethods());
    Set<String> methodSignatures = hookedMethods.getMethodSignatures();

    AtomicInteger operationCounter = new AtomicInteger(0);
    methodSignatures.stream()
        .filter(signature -> !matchedSignatures.contains(signature))
        .forEach(
            signature -> {
              removeHook(clazz, signature);
              operationCounter.addAndGet(1);
            });
    log.debug("Removed {} obsolete method hooks for {}", operationCounter.get(), clazz.getName());
  }

  private void updateHooks(Class<?> clazz, ClassHookConfiguration classConfiguration) {
    AtomicInteger operationCounter = new AtomicInteger(0);
    classConfiguration
        .asMap()
        .forEach(
            (method, active) -> {
              // Currently always true, later we should compare the current with the new config
              if (active) {
                String signature = getSignature(method);
                Optional<MethodHook> maybeHook = getCurrentHook(clazz, signature);
                if (maybeHook.isEmpty()) {
                  setHook(clazz, signature, new MethodHook());
                  operationCounter.addAndGet(1);
                }
              }
            });
    log.info("Updated {} method hooks for {}", operationCounter.get(), clazz.getName());
  }

  // TODO Alternativen suchen + Methode auslagern
  // Wichtig, dass die Signaturen mit denen von ByteBuddy passen!
  private String getSignature(MethodDescription methodDescription) {
    String methodName = methodDescription.getName();
    String parameters =
        methodDescription.getParameters().asTypeList().stream()
            .map(type -> type.asErasure().getTypeName())
            .collect(Collectors.joining(","));
    return methodName + "(" + parameters + ")";
  }

  private void setHook(Class<?> declaringClass, String methodSignature, MethodHook newHook) {
    hooks
        .asMap()
        .computeIfAbsent(declaringClass, (v) -> new HookedMethods())
        .putMethod(methodSignature, newHook);
  }

  private void removeHook(Class<?> declaringClass, String methodSignature) {
    HookedMethods hookedMethods = hooks.getIfPresent(declaringClass);
    if (Objects.nonNull(hookedMethods)) {
      hookedMethods.removeMethod(methodSignature);
      if (hookedMethods.noActiveHooks()) hooks.invalidate(declaringClass);
    }
  }

  private Optional<MethodHook> getCurrentHook(Class<?> clazz, String methodSignature) {
    HookedMethods hookedMethods = hooks.getIfPresent(clazz);
    return Optional.ofNullable(hookedMethods)
        .map(methods -> methods.getActiveHook(methodSignature));
  }

  /** Remove at shutdown */
  private static void addShutdownHook() {
    Runtime.getRuntime()
        .addShutdownHook(new Thread(() -> Instances.hookManager = NoopHookManager.INSTANCE));
  }
}
