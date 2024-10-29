/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.properties;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver.*;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PropertiesResolverTest {

  private static final String TEST_URL = "https://inspectit.rocks/";

  private static final String INTERVAL = "PT1S";

  private static final String PERSISTENCE_FILE = "/path/to/file.json";

  private static final String TEST_ATTRIBUTE_HOST = "localhost";
  private static final String TEST_ATTRIBUTE_PORT = "8080";

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

  @Nested
  class PersistenceFile {

    @Test
    void resolverReturnsFileIfSystemPropertyExists() throws Exception {
      restoreSystemProperties(
          () -> {
            System.setProperty(PERSISTENCE_FILE_SYSTEM_PROPERTY, PERSISTENCE_FILE);

            String file = PropertiesResolver.getPersistenceFile();

            assertEquals(PERSISTENCE_FILE, file);
          });
    }

    @Test
    void resolverReturnsFileIfEnvironmentPropertyExists() throws Exception {
      String file =
          withEnvironmentVariable(PERSISTENCE_FILE_ENV_PROPERTY, PERSISTENCE_FILE)
              .execute(PropertiesResolver::getPersistenceFile);

      assertEquals(PERSISTENCE_FILE, file);
    }

    @Test
    void resolverReturnsSystemPropertyIfSystemAndEnvPropertyExist() throws Exception {
      String envTestFile = PERSISTENCE_FILE + "1";
      restoreSystemProperties(
          () -> {
            System.setProperty(PERSISTENCE_FILE_SYSTEM_PROPERTY, PERSISTENCE_FILE);

            String file =
                withEnvironmentVariable(PERSISTENCE_FILE_ENV_PROPERTY, envTestFile)
                    .execute(PropertiesResolver::getPersistenceFile);

            assertEquals(PERSISTENCE_FILE, file);
          });
    }

    @Test
    void resolverReturnsDefaultFileIfNoPropertyExists() {
      String expected = "inspectit-gepard/last-http-config.json";
      String file = PropertiesResolver.getPersistenceFile();

      assertEquals(expected, file);
    }
  }

  @Nested
  class Attributes {
    @Test
    void resolverReturnsAttributesIfSystemPropertyExists() throws Exception {
      restoreSystemProperties(
          () -> {
            System.setProperty(ATTRIBUTES_SYSTEM_PROPERTY_PREFIX + "host", TEST_ATTRIBUTE_HOST);

            Map<String, String> attributes = PropertiesResolver.getAttributes();
            assertTrue(attributes.containsKey("host"));
            assertEquals(TEST_ATTRIBUTE_HOST, attributes.get("host"));
          });
    }

    @Test
    void resolverReturnsAttributesIfEnvironmentPropertyExists() throws Exception {
      Map<String, String> attributes =
          withEnvironmentVariable(ATTRIBUTES_ENV_PROPERTY_PREFIX + "PORT", TEST_ATTRIBUTE_PORT)
              .execute(PropertiesResolver::getAttributes);
      assertTrue(attributes.containsKey("port"));
      assertEquals(TEST_ATTRIBUTE_PORT, attributes.get("port"));
    }

    @Test
    void resolverReturnsSystemPropertyIfSystemAndEnvPropertyExist() throws Exception {
      String envTestHost = TEST_ATTRIBUTE_HOST + "1";
      restoreSystemProperties(
          () -> {
            System.setProperty(ATTRIBUTES_SYSTEM_PROPERTY_PREFIX + "host", TEST_ATTRIBUTE_HOST);

            Map<String, String> attributes =
                withEnvironmentVariable(ATTRIBUTES_ENV_PROPERTY_PREFIX + "HOST", envTestHost)
                    .execute(PropertiesResolver::getAttributes);

            assertTrue(attributes.containsKey("host"));
            assertEquals(TEST_ATTRIBUTE_HOST, attributes.get("host"));
          });
    }
  }
}
