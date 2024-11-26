/* (C) 2024 */
package rocks.inspectit.gepard.agent.notification;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.*;
import static rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver.SERVER_URL_ENV_PROPERTY;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.MockServerTestBase;
import rocks.inspectit.gepard.agent.internal.shutdown.ShutdownHookManager;

class NotificationManagerTest extends MockServerTestBase {

  private NotificationManager manager;

  private final ShutdownHookManager shutdownHookManager = ShutdownHookManager.getInstance();

  @BeforeEach
  void beforeEach() {
    shutdownHookManager.reset();
  }

  @Test
  void shouldSetUpShutdownNotificationWithProvidedServerUrl() throws Exception {
    withEnvironmentVariable(SERVER_URL_ENV_PROPERTY, SERVER_URL)
        .execute(
            () -> {
              manager = NotificationManager.create();
            });

    manager.setUpShutdownNotification();

    assertEquals(1, shutdownHookManager.getShutdownHookCount());
  }

  @Test
  void shouldNotSetUpShutdownNotificationWithoutServerUrl() {
    manager = NotificationManager.create();

    manager.setUpShutdownNotification();

    assertEquals(0, shutdownHookManager.getShutdownHookCount());
  }
}
