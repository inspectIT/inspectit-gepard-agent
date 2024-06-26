package rocks.inspectit.gepard.agent.notify.http;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import java.util.Objects;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.junit.jupiter.api.Test;

public class NotificationFactoryTest {

  @Test
  public void validUrlCreatesStartNotification() throws Exception {
    String url = "http://localhost:8080/";
    String contentType = "application/json";

    HttpPost httpPost = NotificationFactory.createStartNotification(url);

    assertEquals(url, httpPost.getUri().toString());
    assertEquals(contentType, httpPost.getHeader("content-type").getValue());
    assertTrue(Objects.nonNull(httpPost.getEntity()));
  }

  @Test
  public void invalidUrlThrowsException() {
    String url = "invalid url";

    assertThrows(URISyntaxException.class, () -> NotificationFactory.createStartNotification(url));
  }
}
