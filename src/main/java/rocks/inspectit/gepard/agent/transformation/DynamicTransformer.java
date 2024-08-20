package rocks.inspectit.gepard.agent.transformation;

import java.security.ProtectionDomain;
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
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
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

  DynamicTransformer(ConfigurationResolver resolver, InstrumentationState instrumentationState) {
    this.resolver = resolver;
    this.instrumentationState = instrumentationState;
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
    InstrumentedType currentType = new InstrumentedType(typeDescription.getName(), classLoader);
    if (resolver.shouldInstrument(typeDescription)) {
      log.debug("Adding transformation to {}", typeDescription.getName());

      // Currently, all methods of the type are instrumented
      ElementMatcher.Junction<MethodDescription> elementMatcher =
          resolver.getElementMatcherForType(typeDescription);
      builder = builder.visit(Advice.to(InspectitAdvice.class).on(elementMatcher));

      // Mark type as instrumented
      instrumentationState.addInstrumentedType(currentType);
    } else if (instrumentationState.isInstrumented(currentType)) {
      log.debug("Removing transformation from {}", typeDescription.getName());
      // Mark type as uninstrumented or deinstrumented
      instrumentationState.invalidateInstrumentedType(currentType);
    }

    return builder;
  }
}
