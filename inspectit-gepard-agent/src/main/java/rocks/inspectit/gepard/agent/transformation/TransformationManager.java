/* (C) 2024 */
package rocks.inspectit.gepard.agent.transformation;

import static net.bytebuddy.matcher.ElementMatchers.*;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
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

    return agentBuilder.type(typeMatcher()).transform(transformer).with(listener);
  }

  /**
   * Defines all types, which should (or should not) be transformed. We don't want to transform our
   * own classes.
   *
   * @return the type matcher for transformation
   */
  private ElementMatcher.Junction<TypeDescription> typeMatcher() {
    return not(nameStartsWith("rocks.inspectit.gepard.agent"));
  }
}
