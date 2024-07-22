package rocks.inspectit.gepard.agent.internal.http;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.time.Duration;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockserver.model.HttpError;
import rocks.inspectit.gepard.agent.MockServerTestBase;

@ExtendWith(MockitoExtension.class)
class HttpRequestSenderTest extends MockServerTestBase {

  @Mock private FutureCallback<SimpleHttpResponse> callback;

  @Test
  void requestIsSentSuccessfullyWithCallback() {
    mockServer
        .when(request().withMethod("GET").withPath("/api/v1"))
        .respond(response().withStatusCode(200));
    SimpleHttpRequest request = SimpleRequestBuilder.get(SERVER_URL).build();

    boolean successful = HttpRequestSender.send(request, callback);

    assertTrue(successful);
    Awaitility.await()
        .atMost(Duration.ofSeconds(10))
        .until(
            () -> {
              verify(callback, times(1)).completed(any());
              return true;
            });
  }

  @Test
  void requestFailsAfterServerError() {
    mockServer
        .when(request().withMethod("GET").withPath("/api/v1"))
        .error(HttpError.error().withDropConnection(true));
    SimpleHttpRequest request = SimpleRequestBuilder.get(SERVER_URL).build();

    boolean successful = HttpRequestSender.send(request, callback);

    assertFalse(successful);
    Awaitility.await()
        .atMost(Duration.ofSeconds(10))
        .until(
            () -> {
              verify(callback, times(1)).failed(any());
              return true;
            });
  }

  @Test
  void serverIsNotFound() {
    SimpleHttpRequest request = SimpleRequestBuilder.get(SERVER_URL).build();

    boolean successful = HttpRequestSender.send(request, callback);

    assertFalse(successful);
    Awaitility.await()
        .atMost(Duration.ofSeconds(10))
        .until(
            () -> {
              verify(callback, times(1)).completed(any());
              return true;
            });
  }
}
