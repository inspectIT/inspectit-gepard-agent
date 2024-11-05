/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.instrumentation.hook.action.span.SpanAction;
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
    when(spanAction.startSpan(any())).thenReturn(Optional.of(closeable));

    InternalInspectitContext context = hook.onEnter(getClass(), this, method, new Object[] {});

    verify(spanAction).startSpan(any());
    assertTrue(context.getSpanScope().isPresent());
    assertEquals(closeable, context.getSpanScope().get());
    assertEquals(hook, context.getHook());
  }

  @Test
  void shouldNotReturnSpanScopeWhenExceptionThrown() {
    doThrow(IllegalStateException.class).when(spanAction).startSpan(any());

    InternalInspectitContext context = hook.onEnter(getClass(), this, method, new Object[] {});

    verify(spanAction).startSpan(any());
    assertTrue(context.getSpanScope().isEmpty());
  }

  @Test
  void shouldEndSpan() {
    when(internalContext.getSpanScope()).thenReturn(Optional.of(closeable));

    hook.onExit(internalContext, null, null);

    verify(spanAction).endSpan(closeable);
  }
}
