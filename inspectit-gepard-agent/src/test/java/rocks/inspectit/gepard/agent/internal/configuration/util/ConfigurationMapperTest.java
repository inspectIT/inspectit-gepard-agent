/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.configuration.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.configuration.exception.CouldNotDeserializeConfigurationException;
import rocks.inspectit.gepard.agent.testutils.InspectitConfigurationTestUtil;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.scopes.ScopeConfiguration;

class ConfigurationMapperTest {

  private static final String expectedString = InspectitConfigurationTestUtil.expectedString();

  private static final InspectitConfiguration expectedConfig = expectedConfig();

  @Test
  void validStringIsDeserialized() throws IOException {
    InspectitConfiguration result = ConfigurationMapper.toObject(expectedString);

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
    String result = ConfigurationMapper.toString(expectedConfig);

    assertEquals(result, expectedString);
  }

  @Test
  void nullConfigThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> ConfigurationMapper.toString(null));
  }

  private static InspectitConfiguration expectedConfig() {
    ScopeConfiguration scope =
        new ScopeConfiguration(true, "com.example.Application", Collections.emptyList());
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(Map.of("s_scope", scope), Map.of());
    return new InspectitConfiguration(instrumentationConfiguration);
  }
}
