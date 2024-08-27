package rocks.inspectit.gepard.agent.notification;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver.SERVER_URL_ENV_PROPERTY;

import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpError;
import rocks.inspectit.gepard.agent.MockServerTestBase;

class NotificationManagerTest extends MockServerTestBase {

  private NotificationManager manager;

  @Test
  void sendsStartNotificationIfServerUrlWasProvided() throws Exception {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(201));

    withEnvironmentVariable(SERVER_URL_ENV_PROPERTY, SERVER_URL)
        .execute(
            () -> {
              manager = NotificationManager.create();
            });

    boolean successful = manager.sendStartNotification();

    assertTrue(successful);
  }

  @Test
  void startNotificationFailsWithServerError() throws Exception {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .error(HttpError.error().withDropConnection(true));

    withEnvironmentVariable(SERVER_URL_ENV_PROPERTY, SERVER_URL)
        .execute(
            () -> {
              manager = NotificationManager.create();
            });

    boolean successful = manager.sendStartNotification();

    assertFalse(successful);
  }

  @Test
  void sendsNoStartNotificationWithoutProvidedServerUrl() {
    mockServer
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(201));

    manager = NotificationManager.create();

    boolean successful = manager.sendStartNotification();

    assertFalse(successful);
  }
}
