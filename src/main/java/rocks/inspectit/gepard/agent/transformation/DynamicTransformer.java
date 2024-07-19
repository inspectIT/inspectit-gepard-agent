package rocks.inspectit.gepard.agent.transformation;

import static net.bytebuddy.matcher.ElementMatchers.any;

import java.security.ProtectionDomain;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationResolver;
import rocks.inspectit.gepard.agent.transformation.advice.InspectitAdvice;

public class DynamicTransformer implements AgentBuilder.Transformer {
  private static final Logger log = LoggerFactory.getLogger(DynamicTransformer.class);

  @Override
  public DynamicType.Builder<?> transform(
      DynamicType.Builder<?> builder,
      TypeDescription typeDescription,
      ClassLoader classLoader,
      JavaModule module,
      ProtectionDomain protectionDomain) {
    if (ConfigurationResolver.shouldInstrument(typeDescription)) {
      log.info("Transforming type: {}", typeDescription.getName());

      builder = builder.visit(Advice.to(InspectitAdvice.class).on(any()));
    }
    return builder;
  }
}
