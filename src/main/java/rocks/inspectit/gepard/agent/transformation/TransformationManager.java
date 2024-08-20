package rocks.inspectit.gepard.agent.transformation;

import static net.bytebuddy.matcher.ElementMatchers.any;

import net.bytebuddy.agent.builder.AgentBuilder;
import rocks.inspectit.gepard.agent.internal.instrumentation.InstrumentationState;
import rocks.inspectit.gepard.agent.resolver.ConfigurationResolver;

/** Responsible component for setting up class transformation for instrumentation */
public class TransformationManager {

  private final ConfigurationResolver resolver;

  private final InstrumentationState instrumentationState;

  private TransformationManager(
      ConfigurationResolver resolver, InstrumentationState instrumentationState) {
    this.resolver = resolver;
    this.instrumentationState = instrumentationState;
  }

  /**
   * Factory method to create an {@link TransformationManager}
   *
   * @return the created manager
   */
  public static TransformationManager create(
      ConfigurationResolver resolver, InstrumentationState instrumentationState) {
    return new TransformationManager(resolver, instrumentationState);
  }

  /**
   * Modifies the provided agentBuilder and injects a {@link DynamicTransformer}.
   *
   * @param agentBuilder the original agentBuilder
   * @return the modified agentBuilder
   */
  public AgentBuilder modify(AgentBuilder agentBuilder) {
    DynamicTransformer transformer = new DynamicTransformer(resolver, instrumentationState);
    // In the future, we might add a white- or black-list for types
    return agentBuilder.type(any()).transform(transformer);
  }
}
