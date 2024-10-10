/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.util;

import net.bytebuddy.description.method.MethodDescription;
import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHook;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.SpanAction;

public class MethodHookGenerator {

  private MethodHookGenerator() {}

  /**
   * Creates an executable method hook based on the given configuration.
   *
   * @param method the hooked method
   * @return the created method hook
   */
  public static MethodHook createHook(MethodDescription method) {
    SpanAction spanAction = new SpanAction();
    return new MethodHook(method.getName(), spanAction);
  }
}
