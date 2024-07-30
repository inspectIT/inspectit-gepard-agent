package rocks.inspectit.gepard.agent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;

/** Base class for every test using a mock server. */
@ExtendWith(MockServerExtension.class)
public abstract class MockServerTestBase {

  protected static ClientAndServer mockServer;

  /** Inside the agent we only test for HTTP */
  protected static final String SERVER_URL = "http://localhost:8090/api/v1";

  @BeforeAll
  static void startServer() {
    mockServer = ClientAndServer.startClientAndServer(8090);
  }

  @AfterEach
  void resetServer() {
    mockServer.reset();
  }

  @AfterAll
  static void stopServer() {
    mockServer.stop();
  }
}
