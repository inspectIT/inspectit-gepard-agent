package rocks.inspectit.gepard.agent.transformation;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;

import java.lang.invoke.TypeDescriptor;
import java.security.ProtectionDomain;
import java.util.*;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentationState;
import rocks.inspectit.gepard.agent.resolver.ConfigurationResolver;
import rocks.inspectit.gepard.agent.transformation.advice.InspectitAdvice;

/**
 * Modifies the original class byte code, if necessary. The {@link ConfigurationResolver} determines
 * whether a transformation is needed. The {@link InstrumentationState} will be updated after a
 * transformation.
 */
public class DynamicTransformer implements AgentBuilder.Transformer {
  private static final Logger log = LoggerFactory.getLogger(DynamicTransformer.class);

  private final ConfigurationResolver resolver;

  /** The instrumentation state of the agent */
  private final InstrumentationState instrumentationState;

  /**
   * Set of instrumented types used by this transformer to prevent to call Class.forName() for every
   * call of transform()
   */
  private final Set<TypeDescription> instrumentedTypes;

  DynamicTransformer(ConfigurationResolver resolver, InstrumentationState instrumentationState) {
    this.resolver = resolver;
    this.instrumentationState = instrumentationState;
    this.instrumentedTypes = new HashSet<>();
  }

  /**
   * Injects or removes instrumentation code.
   *
   * <ul>
   *   <li>If the type should be instrumented, we will inject instrumentation code.
   *   <li>If the type should not be instrumented, we will not inject any code.
   *   <li>If the type should be deinstrumented, we will also not inject any code, thus removing the
   *       instrumentation.
   * </ul>
   */
  @Override
  public DynamicType.Builder<?> transform(
      DynamicType.Builder<?> builder,
      TypeDescription typeDescription,
      ClassLoader classLoader,
      JavaModule module,
      ProtectionDomain protectionDomain) {
    if (resolver.shouldInstrument(typeDescription)) {
      log.info("Adding transformation to {}", typeDescription.getName()); // TODO log.debug()

      // Currently, all methods of the type are instrumented
      ElementMatcher<? super MethodDescription> elementMatcher = isMethod();
      builder = builder.visit(Advice.to(InspectitAdvice.class).on(elementMatcher));

      // Mark type as instrumented
      // TODO das macht noch Probleme im ScopeTest...
      addInstrumentation(typeDescription, classLoader);
    } else if (instrumentedTypes.contains(typeDescription)) {
      log.info("Removing transformation from {}", typeDescription.getName()); // TODO log.debug()
      // Mark type as uninstrumented or deinstrumented
      invalidateInstrumentation(typeDescription, classLoader);
    }

    return builder;
  }

  /**
   * Marks the class as instrumented.
   *
   * @param typeDescription the class type
   * @param classLoader the classloader, used for accessing the class object
   */
  private void addInstrumentation(TypeDescription typeDescription, ClassLoader classLoader) {
    Class<?> instrumentedClass = toClass(typeDescription, classLoader);
    if (Objects.nonNull(instrumentedClass)) {
      instrumentationState.addInstrumentation(instrumentedClass);
      instrumentedTypes.add(typeDescription);
    }
  }

  /**
   * Removes the class from marked instrumentations.
   *
   * @param typeDescription the class type description
   * @param classLoader the classloader, used for accessing the class object
   */
  private void invalidateInstrumentation(TypeDescription typeDescription, ClassLoader classLoader) {
    Class<?> deinstrumentedClass = toClass(typeDescription, classLoader);
    if (Objects.nonNull(deinstrumentedClass)) {
      instrumentationState.invalidateInstrumentation(deinstrumentedClass);
      instrumentedTypes.remove(typeDescription);
    }
  }

  /**
   * Converts a bytebuddy {@link TypeDescription} to a {@link Class} object. The class is not
   * initialized.
   *
   * @param type the type description
   * @param classLoader the classloader, used for accessing the class object
   * @return the class object of the provided type description
   */
  private Class<?> toClass(TypeDescription type, ClassLoader classLoader) {
    Class<?> clazz = null;
    try {
      clazz = Class.forName(type.getName(), false, classLoader);
    } catch (Exception e) {
      log.error("Could not update instrumentation state for {}", type.getName(), e);
    }
    return clazz;
  }
}
