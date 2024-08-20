package rocks.inspectit.gepard.agent.internal.configuration.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import rocks.inspectit.gepard.agent.internal.configuration.exception.CouldNotDeserializeConfigurationException;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;

/** Utility class for mapping configuration strings to configuration objects. */
public class ConfigurationMapper {

  private ConfigurationMapper() {}

  /**
   * Transform a raw configuration string into a {@link InspectitConfiguration} object.
   *
   * @param body the raw configuration string
   * @return the configuration object
   */
  public static InspectitConfiguration toObject(String body) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(body, InspectitConfiguration.class);
    } catch (IOException e) {
      throw new CouldNotDeserializeConfigurationException(
          "Failed to deserialize inspectit configuration: " + body, e);
    }
  }
}
