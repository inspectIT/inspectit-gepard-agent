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
import rocks.inspectit.gepard.agent.resolver.ConfigurationResolver;
import rocks.inspectit.gepard.agent.transformation.advice.InspectitAdvice;

/**
 * Modifies the original class byte code, if necessary. The {@link ConfigurationResolver} determines
 * whether a transformation is needed.
 */
public class DynamicTransformer implements AgentBuilder.Transformer {
  private static final Logger log = LoggerFactory.getLogger(DynamicTransformer.class);

  private final ConfigurationResolver resolver;

  public DynamicTransformer(ConfigurationResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public DynamicType.Builder<?> transform(
      DynamicType.Builder<?> builder,
      TypeDescription typeDescription,
      ClassLoader classLoader,
      JavaModule module,
      ProtectionDomain protectionDomain) {
    if (resolver.shouldInstrument(typeDescription)) {
      log.debug("Transforming type: {}", typeDescription.getName());

      // Currently, all methods of the type are instrumented
      ElementMatcher.Junction<MethodDescription> elementMatcher =
          resolver.getElementMatcherForType(typeDescription);
      builder = builder.visit(Advice.to(InspectitAdvice.class).on(elementMatcher));
    }
    return builder;
  }
}
