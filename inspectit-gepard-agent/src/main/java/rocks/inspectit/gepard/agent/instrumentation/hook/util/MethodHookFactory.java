/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.util;

import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHook;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.span.SpanAction;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.model.MethodHookConfiguration;

/** Creates method hook objects */
public class MethodHookFactory {

  private MethodHookFactory() {}

  /**
   * Creates an executable method hook based on the given configuration.
   *
   * @param hookConfig the configuration for the hook
   * @return the created method hook
   */
  public static MethodHook createHook(MethodHookConfiguration hookConfig) {
    MethodHook.Builder builder = MethodHook.builder().setConfiguration(hookConfig);

    if (hookConfig.getTracing().getStartSpan()) builder.setSpanAction(new SpanAction());

    return builder.build();
  }
}
