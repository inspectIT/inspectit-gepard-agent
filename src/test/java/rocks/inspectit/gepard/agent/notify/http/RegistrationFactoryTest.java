package rocks.inspectit.gepard.agent.notify.http;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.config.http.registration.http.RegistrationFactory;
import rocks.inspectit.gepard.agent.config.http.registration.model.AgentInfo;

class RegistrationFactoryTest {

  @Test
  void validUrlCreatesStartNotification() throws Exception {
    String baseUrl = "http://localhost:8080";
    String url = "http://localhost:8080/connections";
    String contentType = "application/json";
    String info = AgentInfo.getAsString();

    SimpleHttpRequest request = RegistrationFactory.createStartNotification(baseUrl);

    assertEquals(url, request.getUri().toString());
    assertEquals(contentType, request.getHeader("content-type").getValue());
    assertEquals(info, request.getBodyText());
  }

  @Test
  void invalidUrlThrowsException() {
    String url = "invalid url";

    assertThrows(URISyntaxException.class, () -> RegistrationFactory.createStartNotification(url));
  }
}
