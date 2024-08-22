package rocks.inspectit.gepard.agent.configuration.http;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpError;
import rocks.inspectit.gepard.agent.MockServerTestBase;

class HttpConfigurationPollerTest extends MockServerTestBase {

  private static HttpConfigurationPoller poller;

  @BeforeAll
  static void beforeAll() {
    poller = new HttpConfigurationPoller(SERVER_URL, null);
  }

  @Test
  void configurationRequestIsSentSuccessfully() {
    mockServer
        .when(request().withMethod("GET").withPath("/api/v1/agent-configuration"))
        .respond(response().withStatusCode(200));

    boolean successful = poller.pollConfiguration();

    assertTrue(successful);
  }

  @Test
  void serverIsNotAvailable() {
    mockServer
        .when(request().withMethod("GET").withPath("/api/v1/agent-configuration"))
        .respond(response().withStatusCode(503));

    boolean successful = poller.pollConfiguration();

    assertFalse(successful);
  }

  @Test
  void serverReturnsError() {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/agent-configuration"))
        .error(HttpError.error().withDropConnection(true));

    boolean successful = poller.pollConfiguration();

    assertFalse(successful);
  }

  @Test
  void serverIsNotFound() {
    boolean successful = poller.pollConfiguration();

    assertFalse(successful);
  }
}
