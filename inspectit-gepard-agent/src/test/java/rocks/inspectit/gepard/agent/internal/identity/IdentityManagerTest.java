/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.identity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.internal.identity.model.IdentityInfo;

@ExtendWith(MockitoExtension.class)
class IdentityManagerTest {

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
      assertEquals(
          "d29aca592fc2071bcef6577d649071d4d54a8ae6cd5c0be0e51f28af2867f207",
          identityInfo.agentId());
    }
  }
}
