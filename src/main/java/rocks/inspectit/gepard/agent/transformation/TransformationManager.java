package rocks.inspectit.gepard.agent.transformation;

import static net.bytebuddy.matcher.ElementMatchers.any;

import net.bytebuddy.agent.builder.AgentBuilder;

/** Responsible component for setting up class transformation for instrumentation */
public class TransformationManager {

  private TransformationManager() {}

  public static TransformationManager create() {
    return new TransformationManager();
  }

  /**
   * Modifies the provided agentBuilder and injects a {@link DynamicTransformer}.
   *
   * @param agentBuilder the original agentBuilder
   * @return the modified agentBuilder
   */
  public AgentBuilder modify(AgentBuilder agentBuilder) {
    // In the future, we might add a white- or black-list for types
    return agentBuilder.type(any()).transform(new DynamicTransformer());
  }
}
