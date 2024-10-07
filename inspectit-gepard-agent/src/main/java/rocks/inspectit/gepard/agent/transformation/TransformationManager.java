/* (C) 2024 */
package rocks.inspectit.gepard.agent.transformation;

import static net.bytebuddy.matcher.ElementMatchers.any;

import net.bytebuddy.agent.builder.AgentBuilder;
import rocks.inspectit.gepard.agent.instrumentation.state.InstrumentationState;

/** Responsible component for setting up class transformation for instrumentation */
public class TransformationManager {

  private final InstrumentationState instrumentationState;

  private TransformationManager(InstrumentationState instrumentationState) {
    this.instrumentationState = instrumentationState;
  }

  /**
   * Factory method to create an {@link TransformationManager}
   *
   * @return the created manager
   */
  public static TransformationManager create(InstrumentationState instrumentationState) {
    return new TransformationManager(instrumentationState);
  }

  /**
   * Modifies the provided agentBuilder and injects a {@link DynamicTransformer}.
   *
   * @param agentBuilder the original agentBuilder
   * @return the modified agentBuilder
   */
  public AgentBuilder modify(AgentBuilder agentBuilder) {
    DynamicTransformer transformer = new DynamicTransformer(instrumentationState);
    InspectitListener listener = new InspectitListener();

    // In the future, we might add a white- or black-list for types
    return agentBuilder.type(any()).transform(transformer).with(listener);
  }
}
