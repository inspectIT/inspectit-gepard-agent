package rocks.inspectit.gepard.agent.notification.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.http.HttpClientHolder;

class HttpClientHolderTest {

  @Test
  void getClientReturnsOnlyOneInstance() {
    CloseableHttpAsyncClient client1 = HttpClientHolder.getClient();

    CloseableHttpAsyncClient client2 = HttpClientHolder.getClient();

    assertEquals(client1, client2);
  }
}