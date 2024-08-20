package rocks.inspectit.gepard.agent.internal.configuration.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.configuration.exception.CouldNotDeserializeConfigurationException;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;

class ConfigurationMapperTest {

  @Test
  void validStringIsDeserialized() {
    String body =
        """
                    {
                      "instrumentationConfiguration": {
                        "scopes": [
                          {
                            "fqn": "com.example.Application",
                            "enabled": true
                          }
                        ]
                      }
                    }
                    """;

    InspectitConfiguration result = ConfigurationMapper.toObject(body);

    assertNotNull(result);
  }

  @Test
  void invalidStringThrowsException() {
    String body = "invalid";
    assertThrows(
        CouldNotDeserializeConfigurationException.class, () -> ConfigurationMapper.toObject(body));
  }
}
