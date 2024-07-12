package rocks.inspectit.gepard.agent.config;

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
    String serverUrlEnvProperty = System.getenv(SERVER_URL_ENV_PROPERTY);

    return serverUrlSystemProperty != null
        ? serverUrlSystemProperty
        : serverUrlEnvProperty != null ? serverUrlEnvProperty : "";
  }
}
