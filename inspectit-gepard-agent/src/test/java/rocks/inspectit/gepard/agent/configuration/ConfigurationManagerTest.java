/* (C) 2024 */
package rocks.inspectit.gepard.agent.configuration;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver.SERVER_URL_ENV_PROPERTY;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.schedule.InspectitScheduler;

class ConfigurationManagerTest {

  private final InspectitScheduler scheduler = InspectitScheduler.getInstance();

  @BeforeEach
  void beforeEach() {
    scheduler.clearScheduledFutures();
  }

  @Test
  void createCreatesNewInstance() {
    ConfigurationManager manager = ConfigurationManager.create();
    assertNotNull(manager);
  }

  @Test
  void startConfigurationLoading() throws Exception {
    String url = "http://localhost:8080";
    ConfigurationManager manager = ConfigurationManager.create();

    withEnvironmentVariable(SERVER_URL_ENV_PROPERTY, url).execute(manager::loadConfiguration);

    assertEquals(1, scheduler.getNumberOfScheduledFutures());
  }
}
