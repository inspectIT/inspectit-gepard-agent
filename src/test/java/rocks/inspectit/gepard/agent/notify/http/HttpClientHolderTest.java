package rocks.inspectit.gepard.agent.notify.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.junit.jupiter.api.Test;

public class HttpClientHolderTest {

  @Test
  public void getClientReturnsOnlyOneInstance() {
    CloseableHttpAsyncClient client1 = HttpClientHolder.getClient();

    CloseableHttpAsyncClient client2 = HttpClientHolder.getClient();

    assertEquals(client1, client2);
  }
}
