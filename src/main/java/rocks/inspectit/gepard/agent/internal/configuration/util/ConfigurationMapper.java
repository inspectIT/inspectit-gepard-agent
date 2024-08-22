package rocks.inspectit.gepard.agent.internal.configuration.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import rocks.inspectit.gepard.agent.internal.configuration.exception.CouldNotDeserializeConfigurationException;
import rocks.inspectit.gepard.agent.internal.configuration.exception.CouldNotSerializeConfigurationException;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;

/** Utility class for mapping configuration strings to configuration objects. */
public class ConfigurationMapper {

  private static final ObjectMapper mapper = new ObjectMapper();

  private ConfigurationMapper() {}

  /**
   * Transforms a raw configuration string into an {@link InspectitConfiguration} object.
   *
   * @param body the raw configuration string
   * @return the configuration object
   */
  public static InspectitConfiguration toObject(String body) throws IOException {
    try {
      return mapper.readValue(body, InspectitConfiguration.class);
    } catch (IOException e) {
      throw new CouldNotDeserializeConfigurationException(
          "Failed to deserialize inspectit configuration: " + body, e);
    }
  }

  /**
   * Transforms an {@link InspectitConfiguration} object into a string.
   *
   * @param inspectitConfiguration the inspectit configuration
   * @return the configuration as JSON string
   */
  public static String toString(InspectitConfiguration inspectitConfiguration) throws IOException {
    if (Objects.isNull(inspectitConfiguration))
      throw new IllegalArgumentException("Configuration is null");
    try {
      return mapper.writeValueAsString(inspectitConfiguration);
    } catch (IOException e) {
      throw new CouldNotSerializeConfigurationException(
          "Failed to serialize inspectit configuration: " + inspectitConfiguration, e);
    }
  }
}
