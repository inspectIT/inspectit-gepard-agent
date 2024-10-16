/* (C) 2024 */
package rocks.inspectit.gepard.agent.configuration.persistence.file;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.internal.file.FileAccessor;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.ScopeConfiguration;

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
    String expectedString = expectedString();
    String expectedScope = "com.example.Application";
    when(fileAccessor.readFile()).thenReturn(expectedString);

    InspectitConfiguration configuration = reader.readConfiguration();
    List<ScopeConfiguration> scopes = configuration.getInstrumentation().getScopes();

    boolean foundScope = scopes.stream().anyMatch(scope -> expectedScope.equals(scope.getFqn()));
    assertTrue(foundScope);
  }

  @Test
  void emptyFileIsMappedToNull() throws IOException {
    when(fileAccessor.readFile()).thenReturn("");

    InspectitConfiguration configuration = reader.readConfiguration();

    assertNull(configuration);
  }

  private static String expectedString() {
    return "{\"instrumentation\":{\"scopes\":[{\"fqn\":\"com.example.Application\",\"enabled\":true}]}}";
  }
}
