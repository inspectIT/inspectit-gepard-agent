/* (C) 2024 */
package rocks.inspectit.gepard.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.opentelemetry.javaagent.tooling.AgentExtension;
import java.util.List;
import java.util.ServiceLoader;
import org.junit.jupiter.api.Test;

class InspectitAgentExtensionTest {

  @Test
  void inspectitAgentExtensionIsLoadable() {
    // ServiceLoader is used by the OpenTelemetry AgentInstaller to load all AgentExtensions
    ServiceLoader<AgentExtension> services = ServiceLoader.load(AgentExtension.class);
    List<AgentExtension> extensions =
        services.stream()
            .map(ServiceLoader.Provider::get)
            .filter(extension -> extension.extensionName().equals("inspectit-gepard"))
            .toList();

    assertEquals(1, extensions.size());
    assertEquals("inspectit-gepard", extensions.get(0).extensionName());
  }
}
