/* (C) 2024 */
package rocks.inspectit.gepard.agent.transformation;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Callbacks, which are executed before or after {@link DynamicTransformer#transform}. Currently,
 * just used for debugging transformation.
 */
public class InspectitListener implements AgentBuilder.Listener {
  private static final Logger log = LoggerFactory.getLogger(InspectitListener.class);

  @Override
  public void onError(
      String typeName,
      ClassLoader classLoader,
      JavaModule module,
      boolean loaded,
      Throwable throwable) {
    log.debug("Dynamic transformation failed for type '{}': {}", typeName, throwable.getMessage());
  }

  @Override
  public void onDiscovery(
      String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
    // unused
  }

  @Override
  public void onTransformation(
      TypeDescription typeDescription,
      ClassLoader classLoader,
      JavaModule module,
      boolean loaded,
      DynamicType dynamicType) {
    // unused
  }

  @Override
  public void onIgnored(
      TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
    // unused
  }

  @Override
  public void onComplete(
      String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
    // unused
  }
}
