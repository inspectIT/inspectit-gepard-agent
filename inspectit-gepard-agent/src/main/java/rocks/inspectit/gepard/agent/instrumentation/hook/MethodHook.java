/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

import java.lang.reflect.Method;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.MethodExecutionContext;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.span.SpanAction;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.model.MethodHookConfiguration;
import rocks.inspectit.gepard.bootstrap.context.InternalInspectitContext;
import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;

/**
 * Each {@link MethodHook} instance defines for a single method which actions are performed. This
 * defines for example which generic actions are executed or which metrics are collected.
 */
public class MethodHook implements IMethodHook {
  private static final Logger log = LoggerFactory.getLogger(MethodHook.class);

  /** The configuration of this method hook */
  private final MethodHookConfiguration configuration;

  private final SpanAction spanAction;

  // Later: entryActions, exitActions

  public MethodHook(Builder builder) {
    this.configuration = builder.configuration;
    this.spanAction = builder.spanAction;
  }

  /**
   * @return builder for method hooks
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * @return the configuration of this method hook
   */
  public MethodHookConfiguration getConfiguration() {
    return configuration;
  }

  @Override
  public InternalInspectitContext onEnter(
      Class<?> clazz, Object thiz, Method method, Object[] instrumentedMethodArgs) {
    MethodExecutionContext executionContext =
        new MethodExecutionContext(clazz, method, instrumentedMethodArgs);
    AutoCloseable spanScope = startSpanAction(executionContext);

    // Using our log4j here will not be visible in the target application...
    System.out.println("HELLO GEPARD : " + configuration.getMethodName());
    return new InternalInspectitContext(this, spanScope);
  }

  @Override
  public void onExit(InternalInspectitContext context, Object returnValue, Throwable thrown) {
    String exceptionMessage = Objects.nonNull(thrown) ? thrown.getMessage() : "no exception";
    String returnMessage = Objects.nonNull(returnValue) ? returnValue.toString() : "nothing";
    String message =
        String.format(
            "inspectIT: Exit MethodHook who returned %s and threw %s",
            returnMessage, exceptionMessage);
    System.out.println(message);

    endSpanAction(context);

    // Using our log4j here will not be visible in the target application...
    System.out.println("BYE GEPARD");
  }

  /**
   * Executes the startSpan-action, if existing
   *
   * @param executionContext the context of the current method
   * @return the scope of the started span or null, if no span was started
   */
  private AutoCloseable startSpanAction(MethodExecutionContext executionContext) {
    AutoCloseable spanScope = null;
    if (Objects.nonNull(spanAction)) {
      try {
        spanScope = spanAction.startSpan(executionContext);
      } catch (Exception e) {
        log.error("Could not execute start-span-action", e);
      }
    }
    return spanScope;
  }

  /**
   * Executes the endSpan-action, if existing
   *
   * @param context the internal inspectIT context of the method
   */
  private void endSpanAction(InternalInspectitContext context) {
    AutoCloseable spanScope = context.getSpanScope();
    if (Objects.nonNull(spanAction))
      try {
        spanAction.endSpan(spanScope);
      } catch (Exception e) {
        log.error("Could not execute end-span-action", e);
      }
  }

  /** Builder-pattern for method hooks, because not all properties have to be initialized. */
  public static class Builder {
    private MethodHookConfiguration configuration;

    private SpanAction spanAction;

    private Builder() {}

    public Builder setConfiguration(MethodHookConfiguration configuration) {
      this.configuration = configuration;
      return this;
    }

    public Builder setSpanAction(SpanAction spanAction) {
      this.spanAction = spanAction;
      return this;
    }

    public MethodHook build() {
      return new MethodHook(this);
    }
  }
}
