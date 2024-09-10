package rocks.inspectit.gepard.agent.configuration.http;

import static org.mockito.Mockito.*;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedSubject;

class HttpConfigurationCallbackTest {

  @Mock private SimpleHttpResponse response;

  @Mock private ConfigurationReceivedObserver observer;

  private HttpConfigurationCallback callback;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ConfigurationReceivedSubject.getInstance().addObserver(observer);
    callback = new HttpConfigurationCallback();
  }

  @Test
  void testCompleted() {
    when(response.getCode()).thenReturn(200);
    when(response.getBodyText()).thenReturn("{}");

    callback.completed(response);

    verify(observer, times(1)).handleConfiguration(any(ConfigurationReceivedEvent.class));
  }

  @Test
  void testFailed() {
    Exception exception = new Exception("Test exception");
    callback.failed(exception);
  }

  @Test
  void testCancelled() {
    callback.cancelled();
  }
}
