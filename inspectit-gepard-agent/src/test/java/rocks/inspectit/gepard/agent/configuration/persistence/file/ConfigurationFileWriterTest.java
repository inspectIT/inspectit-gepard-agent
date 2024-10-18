/* (C) 2024 */
package rocks.inspectit.gepard.agent.configuration.persistence.file;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.internal.file.FileAccessor;
import rocks.inspectit.gepard.agent.testutils.InspectitConfigurationTestUtil;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.scopes.ScopeConfiguration;

@ExtendWith(MockitoExtension.class)
public class ConfigurationFileWriterTest {

  @Mock private FileAccessor fileAccessor;

  private ConfigurationFileWriter writer;

  @BeforeEach
  void setUp() {
    writer = new ConfigurationFileWriter(fileAccessor);
  }

  @Test
  void configurationIsWrittenToFile() throws IOException {
    InspectitConfiguration configuration = createConfiguration();
    String expectedString = InspectitConfigurationTestUtil.expectedString();

    writer.writeConfiguration(configuration);

    verify(fileAccessor).writeFile(expectedString);
  }

  @Test
  void nullIsNotWrittenToFile() throws IOException {
    assertThrows(IllegalArgumentException.class, () -> writer.writeConfiguration(null));

    verify(fileAccessor, never()).writeFile(anyString());
  }

  private static InspectitConfiguration createConfiguration() {
    ScopeConfiguration scope =
        new ScopeConfiguration(true, "com.example.Application", Collections.emptyList());
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(Map.of("s_scope", scope), Map.of());
    return new InspectitConfiguration(instrumentationConfiguration);
  }
}
