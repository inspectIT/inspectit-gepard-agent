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
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentedType;
import rocks.inspectit.gepard.agent.internal.instrumentation.model.ClassInstrumentationConfiguration;
import rocks.inspectit.gepard.agent.state.ConfigurationResolver;
import rocks.inspectit.gepard.agent.state.InstrumentationState;
import rocks.inspectit.gepard.agent.transformation.advice.InspectitAdvice;

/**
 * Modifies the original class byte code, if necessary. The {@link ConfigurationResolver} determines
 * whether a transformation is needed. The {@link InstrumentationState} will be updated after a
 * transformation.
 */
public class DynamicTransformer implements AgentBuilder.Transformer {
  private static final Logger log = LoggerFactory.getLogger(DynamicTransformer.class);

  /** The instrumentation state of the agent */
  private final InstrumentationState instrumentationState;

  DynamicTransformer(InstrumentationState instrumentationState) {
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
    ClassInstrumentationConfiguration currentConfig =
        instrumentationState.resolveClassConfiguration(currentType);
    if (currentConfig.isActive()) {
      log.debug("Adding transformation to {}", typeDescription.getName());

      ElementMatcher.Junction<MethodDescription> methodMatcher = currentConfig.methodMatcher();
      builder = builder.visit(Advice.to(InspectitAdvice.class).on(methodMatcher));

      // Mark type as instrumented
      instrumentationState.addInstrumentedType(currentType, currentConfig);
    } else if (instrumentationState.isActive(currentType)) {
      log.debug("Removing transformation from {}", typeDescription.getName());
      // Mark type as uninstrumented or deinstrumented
      instrumentationState.invalidateInstrumentedType(currentType);
    }

    return builder;
  }
}
