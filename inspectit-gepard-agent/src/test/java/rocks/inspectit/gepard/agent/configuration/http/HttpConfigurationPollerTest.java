/* (C) 2024 */
package rocks.inspectit.gepard.agent.configuration.http;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpError;
import rocks.inspectit.gepard.agent.MockServerTestBase;
import rocks.inspectit.gepard.agent.configuration.persistence.ConfigurationPersistence;
import rocks.inspectit.gepard.agent.internal.identity.model.AgentInfo;

class HttpConfigurationPollerTest extends MockServerTestBase {

  private static HttpConfigurationPoller poller;

  private static ConfigurationPersistence persistence;

  private static String AGENT_ID = AgentInfo.INFO.getAgentId();

  @BeforeEach
  void beforeEach() {
    persistence = mock(ConfigurationPersistence.class);
    poller = new HttpConfigurationPoller(SERVER_URL, persistence);
  }

  @AfterEach
  void afterEach() {
    mockServer.reset();
  }

  @Test
  void runDoesntLoadLocalConfigurationWhenPollingIsSuccessful() {
    mockServer
        .when(request().withMethod("GET").withPath("/api/v1/agent-configuration/" + AGENT_ID))
        .respond(response().withStatusCode(200));
    poller.run();
    verify(persistence, never()).loadLocalConfiguration();
  }

  @Test
  void runLoadsLocalConfigurationOnlyOnceWhenConnectionFailed() {
    mockServer
        .when(request().withMethod("GET").withPath("/api/v1/agent-configuration"))
        .respond(response().withStatusCode(503));
    poller.run();
    poller.run();
    verify(persistence, times(1)).loadLocalConfiguration();
  }

  @Test
  void configurationRequestIsSentSuccessfully() {
    mockServer
        .when(request().withMethod("GET").withPath("/api/v1/agent-configuration/" + AGENT_ID))
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
