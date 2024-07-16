package rocks.inspectit.gepard.agent.agentconfiguration.http;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class AgentConfigurationPollerTest {

  private static ClientAndServer mockServer;

  /** Inside the agent we only test for HTTP */
  private static final String SERVER_URL = "http://localhost:8080/api/v1/agent-configuration";

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
  void configurationRequestIsSentSuccessfully() throws Exception {
    mockServer
        .when(request().withMethod("GET").withPath("/api/v1/agent-configuration"))
        .respond(response().withStatusCode(200));

    restoreSystemProperties(
        () -> {
          System.setProperty("inspectit.config.http.url", "http://localhost:8080/api/v1");

          int status_code = HttpAgentConfigurer.fetchConfiguration();

          assertEquals(200, status_code);
        });
  }
}
