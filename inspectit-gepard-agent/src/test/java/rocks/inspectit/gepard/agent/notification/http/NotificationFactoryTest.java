/* (C) 2024 */
package rocks.inspectit.gepard.agent.notification.http;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URISyntaxException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.identity.model.AgentInfo;
import rocks.inspectit.gepard.agent.notification.http.model.ShutdownNotification;

class NotificationFactoryTest {

  private static final ObjectMapper mapper =
      new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

  @Test
  void validUrlCreatesStartNotification() throws Exception {
    String baseUrl = "http://localhost:8080";
    String url = "http://localhost:8080/connections";
    String contentType = "application/json";
    String info = mapper.writeValueAsString(AgentInfo.INFO);

    SimpleHttpRequest request = NotificationFactory.createStartNotification(baseUrl);

    assertEquals(url, request.getUri().toString());
    assertEquals(contentType, request.getHeader("content-type").getValue());
    assertEquals(info, request.getBodyText());
  }

  @Test
  void invalidStartNotificationUrlThrowsException() {
    String url = "invalid url";

    assertThrows(URISyntaxException.class, () -> NotificationFactory.createStartNotification(url));
  }

  @Test
  void validUrlCreatesShutdownNotification() throws Exception {
    String baseUrl = "http://localhost:8080";
    String url = "http://localhost:8080/connections";
    String contentType = "application/json";
    String body = mapper.writeValueAsString(ShutdownNotification.INSTANCE);

    SimpleHttpRequest request = NotificationFactory.createShutdownNotification(baseUrl);

    assertEquals(url, request.getUri().toString());
    assertEquals(contentType, request.getHeader("content-type").getValue());
    assertEquals(body, request.getBodyText());
  }

  @Test
  void invalidShutdownUrlThrowsException() {
    String url = "invalid url";

    assertThrows(
        URISyntaxException.class, () -> NotificationFactory.createShutdownNotification(url));
  }
}
