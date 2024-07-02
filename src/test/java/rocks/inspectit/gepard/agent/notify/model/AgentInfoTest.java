package rocks.inspectit.gepard.agent.notify.model;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

public class AgentInfoTest {

  @Test
  public void agentInformationIsWritableAsJson() {
    assertDoesNotThrow(AgentInfo::getAsString);
  }

  @Test
  public void agentInformationContainsProperties() throws JsonProcessingException {
    String info = AgentInfo.getAsString();

    assertTrue(info.contains("serviceName"));
    assertTrue(info.contains("inspectit-gepard-agent"));
    assertTrue(info.contains("gepardVersion"));
    assertTrue(info.contains("otelVersion"));
    assertTrue(info.contains("javaVersion"));
    assertTrue(info.contains("startTime"));
    assertTrue(info.contains("pid"));
  }
}
