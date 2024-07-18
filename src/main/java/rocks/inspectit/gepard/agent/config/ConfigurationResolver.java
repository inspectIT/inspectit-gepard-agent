package rocks.inspectit.gepard.agent.config;

import java.util.Objects;

/**
 * This resolver provides the configured configuration server url. Currently, it is possible to
 * configure the url via system properties or environmental properties. System properties are higher
 * prioritized than environmental properties.
 */
public class ConfigurationResolver {

  private static final String SERVER_URL_SYSTEM_PROPERTY = "inspectit.config.http.url";

  private static final String SERVER_URL_ENV_PROPERTY = "INSPECTIT_CONFIG_HTTP_URL";

  private ConfigurationResolver() {}

  /**
   * Get the configured configuration server url. If no url was configured, an empty string will be
   * returned.
   *
   * @return the configured configuration server url
   */
  public static String getServerUrl() {
    String serverUrlSystemProperty = System.getProperty(SERVER_URL_SYSTEM_PROPERTY);
    if (Objects.nonNull(serverUrlSystemProperty)) return serverUrlSystemProperty;

    String serverUrlEnvProperty = System.getenv(SERVER_URL_ENV_PROPERTY);
    return Objects.nonNull(serverUrlEnvProperty) ? serverUrlEnvProperty : "";
  }
}
