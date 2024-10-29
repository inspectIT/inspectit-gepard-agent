/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.properties;

import io.opentelemetry.javaagent.bootstrap.JavaagentFileHolder;
import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This resolver provides configurable properties or their default values. Currently, it is possible
 * to configure the properties via system properties or environmental properties. System properties
 * are higher prioritized than environmental properties.
 */
public class PropertiesResolver {

  public static final String SERVER_URL_SYSTEM_PROPERTY = "inspectit.config.http.url";

  public static final String SERVER_URL_ENV_PROPERTY = "INSPECTIT_CONFIG_HTTP_URL";

  public static final String PERSISTENCE_FILE_SYSTEM_PROPERTY =
      "inspectit.config.http.persistence-file";

  public static final String PERSISTENCE_FILE_ENV_PROPERTY =
      "INSPECTIT_CONFIG_HTTP_PERSISTENCE_FILE";

  public static final String POLLING_INTERVAL_SYSTEM_PROPERTY =
      "inspectit.config.http.polling-interval";
  public static final String POLLING_INTERVAL_ENV_PROPERTY =
      "INSPECTIT_CONFIG_HTTP_POLLING_INTERVAL";

  public static final String ATTRIBUTES_SYSTEM_PROPERTY_PREFIX =
      "inspectit.config.http.attributes.";
  public static final String ATTRIBUTES_ENV_PROPERTY_PREFIX = "INSPECTIT_CONFIG_HTTP_ATTRIBUTES_";

  private static final Logger log = LoggerFactory.getLogger(PropertiesResolver.class);

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
   * Get the configured name of the configuration persistence file. If no file was configured, a
   * default value is returned.
   *
   * @return the configured persistence file name
   */
  public static String getPersistenceFile() {
    String persistenceFileSystemProperty = System.getProperty(PERSISTENCE_FILE_SYSTEM_PROPERTY);
    if (Objects.nonNull(persistenceFileSystemProperty)) return persistenceFileSystemProperty;

    String persistenceEnvProperty = System.getenv(PERSISTENCE_FILE_ENV_PROPERTY);
    return Objects.nonNull(persistenceEnvProperty)
        ? persistenceEnvProperty
        : getDefaultPersistenceFile();
  }

  /**
   * Get the default path of the configuration persistence file, which is the directory where the
   * current agent jar is placed. If no agent jar location is found, a relative path is returned.
   *
   * @return the default persistence file name
   */
  private static String getDefaultPersistenceFile() {
    String suffix = "inspectit-gepard/last-http-config.json";
    File agentFile = JavaagentFileHolder.getJavaagentFile();

    if (Objects.nonNull(agentFile)) return agentFile.getParent() + "/" + suffix;
    return suffix;
  }

  /**
   * Get the configured polling interval for the configuration server. If no interval was
   * configured, the default interval of 10 seconds will be returned.
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
        : Duration.ofSeconds(10);
  }

  /**
   * Get the attributes which should be sent to the configuration server. When an attribute is
   * configured via both system properties and environmental properties, the system property will be
   * used.
   *
   * @return the attributes to be sent to the configuration server
   */
  public static Map<String, String> getAttributes() {

    try {
      Map<String, String> attributes = new HashMap<>(getAttributesFromEnv());

      // Override with system properties where available
      attributes.putAll(getAttributesFromSystemProperties());

      return attributes;
    } catch (Exception e) {
      log.error(
          "Failed to load agent attributes. Continuing with empty attributes for failsafe initialization. Error: {}",
          e.getMessage(),
          e);
      return new HashMap<>();
    }
  }

  /**
   * Retrieves environment variables that start with {@code ATTRIBUTES_ENV_PROPERTY_PREFIX} and maps
   * them to a new {@link Map} with transformed keys.
   *
   * <p>The method filters all environment variables to include only those with keys that start with
   * {@code ATTRIBUTES_ENV_PROPERTY_PREFIX}. The prefix is removed, and the keys are converted to
   * lowercase in the resulting map.
   *
   * @return a {@link Map} containing environment variables with the prefix removed and keys
   *     transformed to lowercase.
   */
  private static Map<String, String> getAttributesFromEnv() {
    return System.getenv().entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(ATTRIBUTES_ENV_PROPERTY_PREFIX))
        .collect(
            Collectors.toMap(
                entry ->
                    entry.getKey().substring(ATTRIBUTES_ENV_PROPERTY_PREFIX.length()).toLowerCase(),
                Map.Entry::getValue));
  }

  /**
   * Retrieves system properties that start with {@code ATTRIBUTES_ENV_PROPERTY_PREFIX} and maps
   * them to a new {@link Map} with transformed keys.
   *
   * <p>This method filters the system properties to include only those with keys that start with
   * {@code ATTRIBUTES_SYSTEM_PROPERTY_PREFIX}. The prefix is removed in the resulting map, with
   * keys unchanged in case.
   *
   * @return a {@link Map} containing system properties with the prefix removed.
   */
  private static Map<String, String> getAttributesFromSystemProperties() {
    return System.getProperties().entrySet().stream()
        .filter(entry -> entry.getKey().toString().startsWith(ATTRIBUTES_SYSTEM_PROPERTY_PREFIX))
        .collect(
            Collectors.toMap(
                entry ->
                    entry.getKey().toString().substring(ATTRIBUTES_SYSTEM_PROPERTY_PREFIX.length()),
                entry -> entry.getValue().toString()));
  }
}
