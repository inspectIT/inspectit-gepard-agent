package rocks.inspectit.gepard.agent.notify;

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

@ExtendWith(MockServerExtension.class)
public class NotificationManagerTest {

  private static ClientAndServer mockServer;

  /** Inside the agent we only test for HTTP */
  private static final String SERVER_URL = "http://localhost:8080/api/v1/connections";

  @BeforeAll
  public static void startServer() {
    mockServer = ClientAndServer.startClientAndServer(8080);
  }

  @AfterEach
  public void resetServer() {
    mockServer.reset();
  }

  @AfterAll
  public static void stopServer() {
    mockServer.stop();
  }

  @Test
  public void notificationIsSentSuccessfully() {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(200));

    boolean successful = NotificationManager.sendStartNotification(SERVER_URL);

    assertTrue(successful);
  }

  @Test
  public void serverIsNotAvailable() {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(503));

    boolean successful = NotificationManager.sendStartNotification(SERVER_URL);

    assertFalse(successful);
  }

  @Test
  public void serverIsNotFound() {
    boolean successful = NotificationManager.sendStartNotification(SERVER_URL);

    assertFalse(successful);
  }
}
