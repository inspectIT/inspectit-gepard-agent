package rocks.inspectit.gepard.agent.notify;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.io.CloseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.notify.http.HttpClientHolder;
import rocks.inspectit.gepard.agent.notify.http.NotificationCallback;
import rocks.inspectit.gepard.agent.notify.http.NotificationFactory;

/** This manager should notify the configuration server about the agent itself and its status. */
public class NotificationManager {
  private static final Logger log = LoggerFactory.getLogger(NotificationManager.class);

  private NotificationManager() {}

  /**
   * Sends a message to the configuration server, to notify it about this agent starting
   *
   * @return True, if the notification was executed successfully
   */
  public static boolean sendStartNotification(String serverUrl) {
    SimpleHttpRequest notification = null;
    try {
      notification = NotificationFactory.createStartNotification(serverUrl);
    } catch (URISyntaxException e) {
      log.error("Error building HTTP URI for configuration server notification", e);
    } catch (JsonProcessingException e) {
      log.error("Could not process agent information for configuration server notification", e);
    }

    try {
      return doSend(notification);
    } catch (ExecutionException e) {
      log.error("Error executing start notification for configuration server", e);
    } catch (InterruptedException e) {
      log.error("Start notification for configuration server was interrupted", e);
      Thread.currentThread().interrupt();
    }
    return false;
  }

  /**
   * Executes the provided HTTP request.
   *
   * @param request the HTTP request
   * @return True, if the HTTP request returned the status code 200
   */
  private static boolean doSend(SimpleHttpRequest request)
      throws ExecutionException, InterruptedException {
    if (Objects.isNull(request)) return false;

    CloseableHttpAsyncClient client = HttpClientHolder.getClient();
    FutureCallback<SimpleHttpResponse> callback = new NotificationCallback();
    Future<SimpleHttpResponse> future = client.execute(request, callback);
    HttpResponse response = future.get();

    return Objects.nonNull(response) && 200 == response.getCode();
  }
}
