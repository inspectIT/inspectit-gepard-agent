package rocks.inspectit.gepard.agent.transformation;

import static net.bytebuddy.matcher.ElementMatchers.any;

import net.bytebuddy.agent.builder.AgentBuilder;

public class TransformationManager {

  private TransformationManager() {}

  public static TransformationManager create() {
    return new TransformationManager();
  }

  public AgentBuilder modify(AgentBuilder agentBuilder) {
    return agentBuilder.type(any()).transform(new DynamicTransformer());
  }
}
