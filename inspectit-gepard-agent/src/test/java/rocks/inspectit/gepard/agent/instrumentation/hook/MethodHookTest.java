/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.span.SpanAction;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.span.exception.CouldNotCloseSpanScopeException;
import rocks.inspectit.gepard.agent.instrumentation.hook.configuration.model.MethodHookConfiguration;
import rocks.inspectit.gepard.bootstrap.context.InternalInspectitContext;

@ExtendWith(MockitoExtension.class)
class MethodHookTest {

  @Mock private MethodHookConfiguration configuration;

  @Mock private SpanAction spanAction;

  @Mock private AutoCloseable closeable;

  @Mock private InternalInspectitContext internalContext;

  @Mock private Method method;

  private MethodHook hook;

  @BeforeEach
  void beforeEach() {
    lenient().when(configuration.getMethodName()).thenReturn("testMethod");
    hook = MethodHook.builder().setConfiguration(configuration).setSpanAction(spanAction).build();
  }

  @Test
  void shouldStartSpanAndCreateContext() {
    when(spanAction.startSpan(any())).thenReturn(closeable);

    InternalInspectitContext context = hook.onEnter(getClass(), this, method, new Object[] {});

    verify(spanAction).startSpan(any());
    assertEquals(closeable, context.getSpanScope());
    assertEquals(hook, context.getHook());
  }

  @Test
  void shouldNotReturnSpanScopeWhenExceptionThrown() {
    doThrow(CouldNotCloseSpanScopeException.class).when(spanAction).startSpan(any());

    InternalInspectitContext context = hook.onEnter(getClass(), this, method, new Object[] {});

    verify(spanAction).startSpan(any());
    assertNull(context.getSpanScope());
  }

  @Test
  void shouldEndSpan() {
    when(internalContext.getSpanScope()).thenReturn(closeable);

    hook.onExit(internalContext, null, null);

    verify(spanAction).endSpan(closeable);
  }
}
