/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.configuration.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.configuration.exception.CouldNotDeserializeConfigurationException;
import rocks.inspectit.gepard.agent.testutils.InspectitConfigurationTestUtil;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;

class ConfigurationMapperTest {

  private static final String EXPECTED_STRING = InspectitConfigurationTestUtil.expectedString();

  private static final InspectitConfiguration EXPECTED_CONFIG =
      InspectitConfigurationTestUtil.expectedConfiguration();

  @Test
  void validStringIsDeserialized() throws IOException {
    InspectitConfiguration result = ConfigurationMapper.toObject(EXPECTED_STRING);

    assertNotNull(result);
  }

  @Test
  void invalidStringThrowsException() {
    String body = "invalid";
    assertThrows(
        CouldNotDeserializeConfigurationException.class, () -> ConfigurationMapper.toObject(body));
  }

  @Test
  void emptyObjectIsSerialized() throws IOException {
    String emptyObject = "{}";

    InspectitConfiguration result = ConfigurationMapper.toObject(emptyObject);

    assertNotNull(result);
  }

  @Test
  void nullAsStringThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> ConfigurationMapper.toObject(null));
  }

  @Test
  void validObjectIsSerialized() throws IOException {
    String result = ConfigurationMapper.toString(EXPECTED_CONFIG);

    assertEquals(result, EXPECTED_STRING);
  }

  @Test
  void nullConfigThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> ConfigurationMapper.toString(null));
  }
}
