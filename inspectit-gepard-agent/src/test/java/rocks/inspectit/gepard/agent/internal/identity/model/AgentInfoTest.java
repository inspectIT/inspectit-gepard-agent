/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.identity.model;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

class AgentInfoTest {

  @Test
  void agentInformationIsWritableAsJson() {
    assertDoesNotThrow(AgentInfo::getAsString);
  }

  @Test
  void agentInformationContainsProperties() throws JsonProcessingException {
    String info = AgentInfo.getAsString();

    assertTrue(info.contains("serviceName"));
    assertTrue(info.contains("inspectit-gepard-agent"));
    assertTrue(info.contains("gepardVersion"));
    assertTrue(info.contains("otelVersion"));
    assertTrue(info.contains("javaVersion"));
    assertTrue(info.contains("startTime"));
    assertTrue(info.contains("pid"));
    assertTrue(info.contains("hostname"));
    assertTrue(info.contains("agentId"));
    assertTrue(info.contains("attributes"));
  }
}
