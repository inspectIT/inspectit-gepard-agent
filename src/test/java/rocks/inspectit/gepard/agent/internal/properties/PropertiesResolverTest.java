package rocks.inspectit.gepard.agent.internal.properties;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver.*;

import java.time.Duration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PropertiesResolverTest {

  private static final String TEST_URL = "https://inspectit.rocks/";

  private static final String INTERVAL = "PT1S";

  @Nested
  class ServerUrl {

    @Test
    void resolverReturnsUrlIfSystemPropertyExists() throws Exception {
      restoreSystemProperties(
          () -> {
            System.setProperty(SERVER_URL_SYSTEM_PROPERTY, TEST_URL);

            String url = PropertiesResolver.getServerUrl();

            assertEquals(TEST_URL, url);
          });
    }

    @Test
    void resolverReturnsUrlIfEnvironmentPropertyExists() throws Exception {
      String url =
          withEnvironmentVariable(SERVER_URL_ENV_PROPERTY, TEST_URL)
              .execute(PropertiesResolver::getServerUrl);

      assertEquals(TEST_URL, url);
    }

    @Test
    void resolverReturnsSystemPropertyIfSystemAndEnvPropertyExist() throws Exception {
      String envTestUrl = TEST_URL + "1";
      restoreSystemProperties(
          () -> {
            System.setProperty(SERVER_URL_SYSTEM_PROPERTY, TEST_URL);

            String url =
                withEnvironmentVariable(SERVER_URL_ENV_PROPERTY, envTestUrl)
                    .execute(PropertiesResolver::getServerUrl);

            assertEquals(TEST_URL, url);
          });
    }

    @Test
    void resolverReturnsEmptyStringIfNoPropertyExists() {
      String url = PropertiesResolver.getServerUrl();

      assertTrue(url.isEmpty());
    }
  }

  @Nested
  class PollingInterval {

    @Test
    void resolverReturnsUrlIfSystemPropertyExists() throws Exception {
      Duration expected = Duration.parse(INTERVAL);
      restoreSystemProperties(
          () -> {
            System.setProperty(POLLING_INTERVAL_SYSTEM_PROPERTY, INTERVAL);

            Duration interval = PropertiesResolver.getPollingInterval();

            assertEquals(expected, interval);
          });
    }

    @Test
    void resolverReturnsUrlIfEnvironmentPropertyExists() throws Exception {
      Duration expected = Duration.parse(INTERVAL);
      Duration interval =
          withEnvironmentVariable(POLLING_INTERVAL_ENV_PROPERTY, INTERVAL)
              .execute(PropertiesResolver::getPollingInterval);

      assertEquals(expected, interval);
    }

    @Test
    void resolverReturnsSystemPropertyIfSystemAndEnvPropertyExist() throws Exception {
      Duration expected = Duration.parse(INTERVAL);
      String envTestInterval = "PT2S";
      restoreSystemProperties(
          () -> {
            System.setProperty(POLLING_INTERVAL_SYSTEM_PROPERTY, INTERVAL);

            Duration interval =
                withEnvironmentVariable(POLLING_INTERVAL_ENV_PROPERTY, envTestInterval)
                    .execute(PropertiesResolver::getPollingInterval);

            assertEquals(expected, interval);
          });
    }

    @Test
    void resolverReturnsEmptyStringIfNoPropertyExists() {
      Duration expected = Duration.ofSeconds(10);

      Duration interval = PropertiesResolver.getPollingInterval();

      assertEquals(expected, interval);
    }
  }
}
