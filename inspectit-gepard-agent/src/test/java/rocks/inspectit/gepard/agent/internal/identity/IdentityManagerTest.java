/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.identity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    InetAddress mockInetAddress = mock(InetAddress.class);
    when(mockInetAddress.getHostName()).thenReturn("mockedHostName");

    RuntimeMXBean mockRuntimeMXBean = mock(RuntimeMXBean.class);
    when(mockRuntimeMXBean.getPid()).thenReturn(12345L);

    try (MockedStatic<InetAddress> mockedStatic = Mockito.mockStatic(InetAddress.class);
        MockedStatic<ManagementFactory> managementFactoryMockedStatic =
            Mockito.mockStatic(ManagementFactory.class)) {
      mockedStatic.when(InetAddress::getLocalHost).thenReturn(mockInetAddress);
      managementFactoryMockedStatic
          .when(ManagementFactory::getRuntimeMXBean)
          .thenReturn(mockRuntimeMXBean);

      IdentityManager identityManager = IdentityManager.getInstance();
      IdentityInfo identityInfo = identityManager.getIdentityInfo();

      assertNotNull(identityInfo);
      assertEquals("mockedHostName", identityInfo.hostname());
      assertEquals(12345L, identityInfo.pid());
      assertEquals(
          "ef138ea8d422d1df09c3f94b675a99bb475cdae966d067f58a0887ab54ab35e0",
          identityInfo.agentId());
    }
  }

  @Test
  void testCreateIdentityManagerWithUnknownHostException() {
    try (MockedStatic<InetAddress> mockedStatic = Mockito.mockStatic(InetAddress.class)) {
      mockedStatic
          .when(InetAddress::getLocalHost)
          .thenThrow(new UnknownHostException("Mocked Exception"));

      IdentityManager identityManager = IdentityManager.getInstance();
      IdentityInfo identityInfo = identityManager.getIdentityInfo();

      assertNotNull(identityInfo);
      assertEquals("0.0.0.0", identityInfo.hostname());
    }
  }
}
