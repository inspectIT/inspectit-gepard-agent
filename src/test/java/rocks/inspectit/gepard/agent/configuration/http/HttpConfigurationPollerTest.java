package rocks.inspectit.gepard.agent.configuration.http;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
public class HttpConfigurationPollerTest {

  private static ClientAndServer mockServer;

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
  void configurationRequestIsSentSuccessfully() {
    mockServer
        .when(request().withMethod("GET").withPath("/api/v1/agent-configuration"))
        .respond(response().withStatusCode(200));

    HttpConfigurationPoller poller = new HttpConfigurationPoller(SERVER_URL);
    boolean successful = poller.pollConfiguration();

    assertTrue(successful);
  }

  @Test
  void serverIsNotAvailable() {
    mockServer
        .when(request().withMethod("GET").withPath("/api/v1/agent-configuration"))
        .respond(response().withStatusCode(503));

    HttpConfigurationPoller poller = new HttpConfigurationPoller(SERVER_URL);
    boolean successful = poller.pollConfiguration();

    assertFalse(successful);
  }

  @Test
  void serverIsNotFound() {
    HttpConfigurationPoller poller = new HttpConfigurationPoller(SERVER_URL);
    boolean successful = poller.pollConfiguration();

    assertFalse(successful);
  }
}
