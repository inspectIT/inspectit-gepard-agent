package rocks.inspectit.gepard.agent.internal.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import rocks.inspectit.gepard.agent.internal.configuration.exception.CouldNotSerializeConfigurationException;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;

public class ConfigurationUtil {

  /**
   * Transform a raw configuration string into a {@link InspectitConfiguration} object.
   *
   * @param body the raw configuration string
   * @return the configuration object
   */
  public static InspectitConfiguration deserializeConfiguration(String body) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(body, InspectitConfiguration.class);
    } catch (IOException e) {
      throw new CouldNotSerializeConfigurationException(
          "Failed to deserialize inspectit configuration: " + body, e);
    }
  }
}
