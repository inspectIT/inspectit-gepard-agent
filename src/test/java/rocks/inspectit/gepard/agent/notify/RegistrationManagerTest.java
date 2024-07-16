package rocks.inspectit.gepard.agent.notify;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import rocks.inspectit.gepard.agent.config.http.registration.RegistrationManager;
import rocks.inspectit.gepard.agent.internal.PropertiesResolver;

@ExtendWith(MockServerExtension.class)
class RegistrationManagerTest {

  private static ClientAndServer mockServer;

  /** Inside the agent we only test for HTTP */
  private static final String SERVER_URL = "http://localhost:8080/api/v1";

  private RegistrationManager registrationManager = new RegistrationManager();

  private static MockedStatic<PropertiesResolver> mockedPropertiesResolver;

  @BeforeAll
  static void startServer() {
    mockServer = ClientAndServer.startClientAndServer(8080);
  }

  @BeforeAll
  public static void setUp() {
    mockedPropertiesResolver = Mockito.mockStatic(PropertiesResolver.class);
    mockedPropertiesResolver.when(PropertiesResolver::getServerUrl).thenReturn(SERVER_URL);
  }

  @AfterEach
  void resetServer() {
    mockServer.reset();
  }

  @AfterAll
  static void stopServer() {
    mockServer.stop();
  }

  @AfterAll
  public static void tearDown() {
    mockedPropertiesResolver.close();
  }

  @Test
  void notificationIsSentSuccessfully() {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(200));

    boolean successful = registrationManager.sendStartNotification();

    assertTrue(successful);
  }

  @Test
  void serverIsNotAvailable() {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(503));

    boolean successful = registrationManager.sendStartNotification();

    assertFalse(successful);
  }

  @Test
  void serverIsNotFound() {
    boolean successful = registrationManager.sendStartNotification();

    assertFalse(successful);
  }
}
