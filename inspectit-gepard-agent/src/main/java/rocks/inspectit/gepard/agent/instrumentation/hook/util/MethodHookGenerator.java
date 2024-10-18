/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.util;

import rocks.inspectit.gepard.agent.instrumentation.hook.MethodHook;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.SpanAction;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.MethodHookConfiguration;

/** Creates method hook objects */
public class MethodHookGenerator {

  private MethodHookGenerator() {}

  /**
   * Creates an executable method hook based on the given configuration.
   *
   * @param hookConfig the configuration for the hook
   * @return the created method hook
   */
  public static MethodHook createHook(MethodHookConfiguration hookConfig) {
    MethodHook.Builder builder = MethodHook.builder().setConfiguration(hookConfig);

    if (hookConfig.tracing().getStartSpan()) builder.setSpanAction(new SpanAction());

    return builder.build();
  }
}
