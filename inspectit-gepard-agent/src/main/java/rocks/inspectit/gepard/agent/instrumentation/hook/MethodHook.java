/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

import io.opentelemetry.api.common.Attributes;
import java.lang.reflect.Method;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.SpanAction;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.util.SpanUtil;
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
    AutoCloseable spanScope = startSpanAction(clazz, method, instrumentedMethodArgs);

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

    AutoCloseable spanScope = context.getSpanScope();
    if (Objects.nonNull(spanAction))
      try {
        spanAction.endSpan(spanScope);
      } catch (Exception e) {
        log.error("Could not execute end-span-action", e);
      }

    // Using our log4j here will not be visible in the target application...
    System.out.println("BYE GEPARD");
  }

  /**
   * @param clazz the class of the method for which a span will be started
   * @return the span name in the format 'SimpleClassName.methodName', for instance
   *     'MethodHook.getSpanName'
   */
  private String getSpanName(Class<?> clazz) {
    String methodName = configuration.getMethodName();
    return clazz.getSimpleName() + "." + methodName;
  }

  private AutoCloseable startSpanAction(Class<?> clazz, Method method, Object[] methodArgs) {
    AutoCloseable spanScope = null;
    if (Objects.nonNull(spanAction)) {
      String spanName = getSpanName(clazz);
      try {
        Attributes spanAttributes = SpanUtil.createSpanAttributes(method, methodArgs);
        spanScope = spanAction.startSpan(spanName, spanAttributes);
      } catch (Exception e) {
        log.error("Could not execute start-span-action", e);
      }
    }
    return spanScope;
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
