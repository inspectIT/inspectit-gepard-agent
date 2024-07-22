package rocks.inspectit.gepard.agent.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpError;
import rocks.inspectit.gepard.agent.MockServerTestBase;

class StartNotifierTest extends MockServerTestBase {

  private final StartNotifier notifier = new StartNotifier();

  @Test
  void notificationIsSentSuccessfully() {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(200));

    boolean successful = notifier.sendNotification(SERVER_URL);

    assertTrue(successful);
  }

  @Test
  void serverIsNotAvailable() {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(503));

    boolean successful = notifier.sendNotification(SERVER_URL);

    assertFalse(successful);
  }

  @Test
  void serverReturnsError() {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .error(HttpError.error().withDropConnection(true));

    boolean successful = notifier.sendNotification(SERVER_URL);

    assertFalse(successful);
  }

  @Test
  void serverIsNotFound() {
    boolean successful = notifier.sendNotification(SERVER_URL);

    assertFalse(successful);
  }
}
