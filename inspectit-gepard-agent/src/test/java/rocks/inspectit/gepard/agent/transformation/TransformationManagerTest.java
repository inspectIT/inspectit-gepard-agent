/* (C) 2024 */
package rocks.inspectit.gepard.agent.transformation;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.opentelemetry.javaagent.tooling.AgentInstaller;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.VisibilityBridgeStrategy;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.state.InstrumentationState;

@ExtendWith(MockitoExtension.class)
class TransformationManagerTest {

  @Mock private InstrumentationState instrumentationState;

  @Test
  void agentBuilderDoNotEqual() {
    TransformationManager manager = TransformationManager.create(instrumentationState);

    AgentBuilder agent = createAgentBuilder();
    AgentBuilder modifiedAgent = manager.modify(agent);

    // There is no ordinary way to compare the properties of the agentBuilders
    assertNotEquals(agent, modifiedAgent);
  }

  /** Copied from {@link AgentInstaller} */
  private AgentBuilder createAgentBuilder() {
    return new AgentBuilder.Default(
            new ByteBuddy()
                .with(MethodGraph.Compiler.ForDeclaredMethods.INSTANCE)
                .with(VisibilityBridgeStrategy.Default.NEVER)
                .with(InstrumentedType.Factory.Default.FROZEN))
        .with(AgentBuilder.TypeStrategy.Default.DECORATE)
        .disableClassFormatChanges()
        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
        .with(AgentBuilder.DescriptionStrategy.Default.POOL_ONLY);
  }
}
