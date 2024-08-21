package rocks.inspectit.gepard.agent.internal.properties;

import io.opentelemetry.javaagent.bootstrap.JavaagentFileHolder;
import java.io.File;
import java.time.Duration;
import java.util.Objects;

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
}
