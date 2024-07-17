package rocks.inspectit.gepard.agent.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.model.Not;

@ExtendWith(MockServerExtension.class)
class NotificationManagerTest {

  private static ClientAndServer mockServer;

  private final NotificationManager manager = new NotificationManager();

  /** Inside the agent we only test for HTTP */
  private static final String SERVER_URL = "http://localhost:8080/api/v1";

  @BeforeAll
  static void startServer() {
    mockServer = ClientAndServer.startClientAndServer(8080);
  }

  @AfterEach
  void resetServer() {
    mockServer.reset();
  }

  @AfterAll
  static void stopServer() {
    mockServer.stop();
  }

  @Test
  void notificationIsSentSuccessfully() {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(200));

    boolean successful = manager.sendStartNotification(SERVER_URL);

    assertTrue(successful);
  }

  @Test
  void serverIsNotAvailable() {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(503));

    boolean successful = manager.sendStartNotification(SERVER_URL);

    assertFalse(successful);
  }

  @Test
  void serverIsNotFound() {
    boolean successful = manager.sendStartNotification(SERVER_URL);

    assertFalse(successful);
  }
}