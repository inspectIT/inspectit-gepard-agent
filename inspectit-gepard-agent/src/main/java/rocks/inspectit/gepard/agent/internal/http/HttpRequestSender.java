/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.http;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility class to execute HTTP requests with callbacks */
public class HttpRequestSender {
  private static final Logger log = LoggerFactory.getLogger(HttpRequestSender.class);

  /** Status codes which are considered successful. */
  private static final List<Integer> successfulStatusCodes =
      List.of(200, 201, 202, 203, 204, 205, 206, 207, 208, 226);

  private HttpRequestSender() {}

  /**
   * Executes the provided HTTP request as well as the callback function.
   *
   * @param request the HTTP request
   * @param callback the callback function
   * @return True, if the HTTP request returned a status code in {@link #successfulStatusCodes}
   */
  public static boolean send(
      SimpleHttpRequest request, FutureCallback<SimpleHttpResponse> callback) {
    if (Objects.isNull(request)) return false;

    CloseableHttpAsyncClient client = HttpClientHolder.getClient();
    Future<SimpleHttpResponse> future = client.execute(request, callback);

    HttpResponse response = handleFuture(future);
    return Objects.nonNull(response) && successfulStatusCodes.contains(response.getCode());
  }

  private static HttpResponse handleFuture(Future<SimpleHttpResponse> future) {
    HttpResponse response = null;
    try {
      response = future.get();
    } catch (ExecutionException e) {
      log.error("Error executing HTTP request", e);
    } catch (InterruptedException e) {
      log.error("Sending of HTTP request was interrupted", e);
      Thread.currentThread().interrupt();
    }
    return response;
  }
}
