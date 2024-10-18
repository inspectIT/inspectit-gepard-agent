/* (C) 2024 */
package rocks.inspectit.gepard.agent.configuration.persistence.file;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.internal.file.FileAccessor;
import rocks.inspectit.gepard.agent.testutils.InspectitConfigurationTestUtil;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.scopes.ScopeConfiguration;

@ExtendWith(MockitoExtension.class)
public class ConfigurationFileReaderTest {

  @Mock private FileAccessor fileAccessor;

  private ConfigurationFileReader reader;

  @BeforeEach
  void setUp() {
    reader = new ConfigurationFileReader(fileAccessor);
  }

  @Test
  void fileContentIsMappedToConfiguration() throws IOException {
    String expectedString = InspectitConfigurationTestUtil.expectedString();
    String expectedScope = "com.example.Application";
    when(fileAccessor.readFile()).thenReturn(expectedString);

    InspectitConfiguration configuration = reader.readConfiguration();
    Map<String, ScopeConfiguration> scopes = configuration.getInstrumentation().getScopes();

    boolean foundScope =
        scopes.values().stream().anyMatch(scope -> expectedScope.equals(scope.getFqn()));
    assertTrue(foundScope);
  }

  @Test
  void emptyFileIsMappedToNull() throws IOException {
    when(fileAccessor.readFile()).thenReturn("");

    InspectitConfiguration configuration = reader.readConfiguration();

    assertNull(configuration);
  }
}
