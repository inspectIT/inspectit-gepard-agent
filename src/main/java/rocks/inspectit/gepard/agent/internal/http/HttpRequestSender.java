package rocks.inspectit.gepard.agent.internal.http;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpResponse;

/** Utility class to execute HTTP requests with callbacks */
public class HttpRequestSender {

  /**
   * Executes the provided HTTP request as well as the callback function.
   *
   * @param request the HTTP request
   * @param callback the callback function
   * @return True, if the HTTP request returned the status code 200
   */
  public static boolean send(SimpleHttpRequest request, FutureCallback<SimpleHttpResponse> callback)
      throws ExecutionException, InterruptedException {
    CloseableHttpAsyncClient client = HttpClientHolder.getClient();
    Future<SimpleHttpResponse> future = client.execute(request, callback);
    HttpResponse response = future.get();

    return Objects.nonNull(response) && 200 == response.getCode();
  }
}
