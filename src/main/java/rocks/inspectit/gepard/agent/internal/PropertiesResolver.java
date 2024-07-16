package rocks.inspectit.gepard.agent.internal;

import java.time.Duration;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.config.ConfigurationSource;

/**
 * This resolver provides the configured configuration server url. Currently, it is possible to
 * configure the url via system properties or environmental properties. System properties are higher
 * prioritized than environmental properties.
 */
public class PropertiesResolver {

  private static final Logger log = LoggerFactory.getLogger(PropertiesResolver.class);

  private static final String SERVER_URL_SYSTEM_PROPERTY = "inspectit.config.http.url";

  private static final String SERVER_URL_ENV_PROPERTY = "INSPECTIT_CONFIG_HTTP_URL";

  private static final String POLLING_INTERVAL_SYSTEM_PROPERTY =
      "inspectit.config.http.polling-interval";
  private static final String POLLING_INTERVAL_ENV_PROPERTY =
      "INSPECTIT_CONFIG_HTTP_POLLING_INTERVAL";

  private PropertiesResolver() {}

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

  /**
   * Get the configured polling interval for the configuration server. If no interval was
   * configured, the default interval of 15 seconds will be returned.
   *
   * @return the configured polling interval
   */
  public static Duration getPollingInterval() {
    String pollingIntervalSystemProperty = System.getProperty(POLLING_INTERVAL_SYSTEM_PROPERTY);
    if (Objects.nonNull(pollingIntervalSystemProperty))
      return Duration.parse(pollingIntervalSystemProperty);

    String pollingIntervalEnvProperty = System.getenv(POLLING_INTERVAL_ENV_PROPERTY);
    return Objects.nonNull(pollingIntervalEnvProperty)
        ? Duration.parse(pollingIntervalEnvProperty)
        : Duration.ofSeconds(15);
  }

  // This should be moved into a model as well as the rest...
  public static ConfigurationSource getConfigurationSource() {
    String url = getServerUrl();
    if (url.isEmpty()) {
      log.info("No configuration server url was provided. Falling back to local configuration");
      return ConfigurationSource.FILE;
    } else {
      log.info("Using configuration server with url: {}", url);
      return ConfigurationSource.HTTP;
    }
  }
}
