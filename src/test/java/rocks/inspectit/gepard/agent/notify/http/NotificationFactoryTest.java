package rocks.inspectit.gepard.agent.notify.http;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.notify.model.AgentInfo;

public class NotificationFactoryTest {

  @Test
  public void validUrlCreatesStartNotification() throws Exception {
    String url = "http://localhost:8080/";
    String contentType = "application/json";
    String info = AgentInfo.getAsString();

    SimpleHttpRequest request = NotificationFactory.createStartNotification(url);

    assertEquals(url, request.getUri().toString());
    assertEquals(contentType, request.getHeader("content-type").getValue());
    assertEquals(info, request.getBodyText());
  }

  @Test
  public void invalidUrlThrowsException() {
    String url = "invalid url";

    assertThrows(URISyntaxException.class, () -> NotificationFactory.createStartNotification(url));
  }
}
