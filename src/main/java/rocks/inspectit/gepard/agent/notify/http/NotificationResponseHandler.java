package rocks.inspectit.gepard.agent.notify.http;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This handler should process HTTP responses and return a boolean, indicating a successful
 * handling.
 */
public class NotificationResponseHandler implements HttpClientResponseHandler<Boolean> {
  private static final Logger log = LoggerFactory.getLogger(NotificationResponseHandler.class);

  @Override
  public Boolean handleResponse(ClassicHttpResponse response) {
    int statusCode = response.getCode();

    if (statusCode == 200) {
      // process response
      return true;
    } else {
      log.warn("Server returned an unexpected response status: " + statusCode);
      return false;
    }
  }
}
