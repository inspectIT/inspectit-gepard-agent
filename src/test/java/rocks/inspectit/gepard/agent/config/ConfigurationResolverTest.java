package rocks.inspectit.gepard.agent.config;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.List;

class ConfigurationResolverTest {

  private static final String SERVER_URL_SYSTEM_PROPERTY = "inspectit.config.http.url";

  private static final String SERVER_URL_ENV_PROPERTY = "INSPECTIT_CONFIG_HTTP_URL";

  private static final String TEST_URL = "https://inspectit.rocks/";

  @Test
  void resolverReturnsUrlIfSystemPropertyExists() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty(SERVER_URL_SYSTEM_PROPERTY, TEST_URL);

          String url = ConfigurationResolver.getServerUrl();

          assertEquals(TEST_URL, url);
        });
  }

  @Test
  void resolverReturnsUrlIfEnvironmentPropertyExists() throws Exception {
      String url = withEnvironmentVariable(SERVER_URL_ENV_PROPERTY, TEST_URL)
              .execute(ConfigurationResolver::getServerUrl);

      assertEquals(TEST_URL, url);
  }

  @Test
    void resolverReturnsSystemPropertyIfSystemAndEnvPropertyExist() throws Exception {
      String envTestUrl = TEST_URL + "1";
      restoreSystemProperties(
              () -> {
                  System.setProperty(SERVER_URL_SYSTEM_PROPERTY, TEST_URL);

                  String url = withEnvironmentVariable(SERVER_URL_ENV_PROPERTY, envTestUrl)
                          .execute(ConfigurationResolver::getServerUrl);

                  assertEquals(TEST_URL, url);
              });
  }

    @Test
    void resolverReturnsEmptyStringIfNoPropertyExists() {
        String url = ConfigurationResolver.getServerUrl();

        assertTrue(url.isEmpty());
    }
}
