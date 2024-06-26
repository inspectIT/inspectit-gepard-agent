package rocks.inspectit.gepard.agent.notify.http;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NotificationResponseHandlerTest {

  private final NotificationResponseHandler handler = new NotificationResponseHandler();

  @Mock private ClassicHttpResponse response;

  @Test
  public void StatusCode200ReturnsTrue() {
    when(response.getCode()).thenReturn(200);

    boolean successful = handler.handleResponse(response);

    assertTrue(successful);
  }

  @Test
  public void UnexpectedStatusCodeReturnsFalse() {
    when(response.getCode()).thenReturn(404);

    boolean successful = handler.handleResponse(response);

    assertFalse(successful);
  }
}
