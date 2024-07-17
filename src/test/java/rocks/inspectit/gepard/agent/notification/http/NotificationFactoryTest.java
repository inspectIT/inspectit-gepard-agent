package rocks.inspectit.gepard.agent.notification.http;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.notification.model.AgentInfo;

class NotificationFactoryTest {

  @Test
  void validUrlCreatesStartNotification() throws Exception {
    String baseUrl = "http://localhost:8080";
    String url = "http://localhost:8080/connections";
    String contentType = "application/json";
    String info = AgentInfo.getAsString();

    SimpleHttpRequest request = NotificationFactory.createStartNotification(baseUrl);

    assertEquals(url, request.getUri().toString());
    assertEquals(contentType, request.getHeader("content-type").getValue());
    assertEquals(info, request.getBodyText());
  }

  @Test
  void invalidUrlThrowsException() {
    String url = "invalid url";

    assertThrows(URISyntaxException.class, () -> NotificationFactory.createStartNotification(url));
  }
}
