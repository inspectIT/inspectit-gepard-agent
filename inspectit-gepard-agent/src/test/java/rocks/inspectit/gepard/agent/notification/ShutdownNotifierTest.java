/* (C) 2024 */
package rocks.inspectit.gepard.agent.notification;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpError;
import rocks.inspectit.gepard.agent.MockServerTestBase;

class ShutdownNotifierTest extends MockServerTestBase {

  private final ShutdownNotifier notifier = new ShutdownNotifier();

  @Test
  void notificationIsSentSuccessfully() {
    mockServer
        .when(request().withMethod("PUT").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(200));

    boolean successful = notifier.sendNotification(SERVER_URL);

    assertTrue(successful);
  }

  @Test
  void serverIsNotAvailable() {
    mockServer
        .when(request().withMethod("PUT").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(503));

    boolean successful = notifier.sendNotification(SERVER_URL);

    assertFalse(successful);
  }

  @Test
  void serverReturnsError() {
    mockServer
        .when(request().withMethod("PUT").withPath("/api/v1/connections"))
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
