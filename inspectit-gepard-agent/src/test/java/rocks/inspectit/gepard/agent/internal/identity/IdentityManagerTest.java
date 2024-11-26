/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.identity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import rocks.inspectit.gepard.agent.internal.identity.model.IdentityInfo;

class IdentityManagerTest {

  @BeforeEach
  public void setup() throws NoSuchFieldException, IllegalAccessException {
    Field instance = IdentityManager.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
  }

  @Test
  void testCreateIdentityManagerSuccessfully() {
    RuntimeMXBean mockRuntimeMXBean = mock(RuntimeMXBean.class);
    when(mockRuntimeMXBean.getName()).thenReturn("12345@mockedHostName");

    try (MockedStatic<ManagementFactory> managementFactoryMockedStatic =
        Mockito.mockStatic(ManagementFactory.class)) {
      managementFactoryMockedStatic
          .when(ManagementFactory::getRuntimeMXBean)
          .thenReturn(mockRuntimeMXBean);

      IdentityManager identityManager = IdentityManager.getInstance();
      IdentityInfo identityInfo = identityManager.getIdentityInfo();

      assertNotNull(identityInfo);
      assertEquals("12345@mockedHostName", identityInfo.vmId());
      assertNotNull(identityInfo.agentId());
    }
  }
}
