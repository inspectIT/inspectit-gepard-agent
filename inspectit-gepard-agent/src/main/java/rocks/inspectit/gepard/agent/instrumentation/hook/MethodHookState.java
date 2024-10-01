/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import net.bytebuddy.description.method.MethodDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.ClassHookConfiguration;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.HookedMethods;

/** Stores the method hook configurations of all instrumented classes. */
public class MethodHookState {
  private static final Logger log = LoggerFactory.getLogger(MethodHookState.class);

  private final Cache<Class<?>, HookedMethods> hooks;

  public MethodHookState() {
    this.hooks = Caffeine.newBuilder().weakKeys().build();
  }

  /**
   * @param clazz the class containing the methods
   * @return the hooked methods for the provided class
   */
  public HookedMethods getIfPresent(Class<?> clazz) {
    return hooks.getIfPresent(clazz);
  }

  /**
   * Removes all methods hooks of the provided class, which are no longer necessary. This is done by
   * comparing the current instrumented methods with the already hooked methods. If a hooked method
   * is not part of the instrumented methods, it will be removed.
   *
   * @param clazz the class containing the methods
   * @param instrumentedMethods the methods, which should be hooked
   * @return the amount of hooks removed
   */
  public int removeObsoleteHooks(
      Class<?> clazz, Set<MethodDescription.InDefinedShape> instrumentedMethods) {
    Set<String> matchedSignatures =
        instrumentedMethods.stream().map(this::getSignature).collect(Collectors.toSet());

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
    return operationCounter.get();
  }

  /**
   * Updates the method hooks by overwriting the previous hooks. Currently, there is no difference
   * between method hooks, thus we don't need to compare them. This might change in the future.
   *
   * @param clazz the class containing the methods
   * @param classConfiguration the hook configuration for the provided class
   * @return the amount of updated hooks
   */
  public int updateHooks(Class<?> clazz, ClassHookConfiguration classConfiguration) {
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
    return operationCounter.get();
  }

  /**
   * Creates the signature for the provided method. <br>
   * The signature of this method would be: {@code
   * getSignature(net.bytebuddy.description.method.MethodDescription)} <br>
   * It should match with the method signatures used by ByteBuddy. Multiple method parameter will be
   * concatenated with "," and without spaces.
   *
   * @param methodDescription the method description
   * @return the signature of the provided method
   */
  private String getSignature(MethodDescription methodDescription) {
    String methodName = methodDescription.getName();
    String parameters =
        methodDescription.getParameters().asTypeList().stream()
            .map(type -> type.asErasure().getTypeName())
            .collect(Collectors.joining(","));
    return methodName + "(" + parameters + ")";
  }

  /**
   * Overwrite the hook for a specific method of the provided class.
   *
   * @param declaringClass the class containing the method
   * @param methodSignature the method signature to be hooked
   * @param newHook the hook for the method
   */
  private void setHook(Class<?> declaringClass, String methodSignature, MethodHook newHook) {
    hooks
        .asMap()
        .computeIfAbsent(declaringClass, (v) -> new HookedMethods())
        .putMethod(methodSignature, newHook);
  }

  /**
   * Removes the hook for the specific method of the provided class
   *
   * @param declaringClass the class containing the method
   * @param methodSignature the method, whose hook should be removed
   */
  private void removeHook(Class<?> declaringClass, String methodSignature) {
    HookedMethods hookedMethods = hooks.getIfPresent(declaringClass);
    if (Objects.nonNull(hookedMethods)) {
      hookedMethods.removeMethod(methodSignature);
      if (hookedMethods.noActiveHooks()) hooks.invalidate(declaringClass);
    }
  }

  /**
   * Returns the hook for the specific method of the provided class.
   *
   * @param clazz the class containing the method
   * @param methodSignature the method, which might be hooked
   * @return the hook of the method, if existing
   */
  private Optional<MethodHook> getCurrentHook(Class<?> clazz, String methodSignature) {
    HookedMethods hookedMethods = hooks.getIfPresent(clazz);
    return Optional.ofNullable(hookedMethods)
        .map(methods -> methods.getActiveHook(methodSignature));
  }
}
