package rocks.inspectit.gepard.agent.config;

/**
 * This resolver provides the configured configuration server url. Currently, it is possible to
 * configure the url via system properties or environmental properties. System properties are higher
 * prioritized than environmental properties.
 */
public class ConfigurationResolver {

  private static final String SERVER_URL_SYSTEM_PROPERTY = "inspectit.config.http.url";

  private static final String SERVER_URL_ENV_PROPERTY = "INSPECTIT_CONFIG_HTTP_URL";

  private static final String POLLING_INTERVAL_SYSTEM_PROPERTY =
      "inspectit.config.http.polling-interval";
  private static final String POLLING_INTERVAL_ENV_PROPERTY =
      "INSPECTIT_CONFIG_HTTP_POLLING_INTERVAL";

  private ConfigurationResolver() {}

  /**
   * Get the configured configuration server url. If no url was configured, an empty string will be
   * returned.
   *
   * @return the configured configuration server url
   */
  public static String getServerUrl() {
    String serverUrlSystemProperty = System.getProperty(SERVER_URL_SYSTEM_PROPERTY);
    if (serverUrlSystemProperty != null) return serverUrlSystemProperty;

    String serverUrlEnvProperty = System.getenv(SERVER_URL_ENV_PROPERTY);
    return serverUrlEnvProperty != null ? serverUrlEnvProperty : "";
  }

  /**
   * Get the configured polling interval for the configuration server. If no interval was
   * configured, the default interval of 15 seconds will be returned.
   *
   * @return the configured polling interval
   */
  public static long getPollingInterval() {
    String pollingIntervalSystemProperty = System.getProperty(POLLING_INTERVAL_SYSTEM_PROPERTY);
    if (pollingIntervalSystemProperty != null) return Long.parseLong(pollingIntervalSystemProperty);

    String pollingIntervalEnvProperty = System.getenv(POLLING_INTERVAL_ENV_PROPERTY);
    return pollingIntervalEnvProperty != null ? Long.parseLong(pollingIntervalEnvProperty) : 15;
  }
}
