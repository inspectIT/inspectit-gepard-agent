package rocks.inspectit.gepard.agent.agentconfiguration.http;

import java.net.URISyntaxException;
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
import rocks.inspectit.gepard.agent.notify.http.HttpClientHolder;

public class HttpAgentConfigurer {

  private static final Logger log = LoggerFactory.getLogger(HttpAgentConfigurer.class);

  public static boolean fetchConfiguration() {
    log.debug("Fetching configuration from server...");
    try {
      SimpleHttpRequest request = HttpConfigurationFactory.createConfigurationRequest();
      log.info(request.getRequestUri());
      return doSend(request);
    } catch (URISyntaxException e) {
      log.error("Error creating configuration request.", e);
    } catch (ExecutionException | InterruptedException e) {
      log.error("Configuration Polling was interrupted", e);
      Thread.currentThread().interrupt();
    }
      return false;
  }

  private static boolean doSend(SimpleHttpRequest request)
      throws ExecutionException, InterruptedException {
    if (Objects.isNull(request)) return false;

    CloseableHttpAsyncClient client = HttpClientHolder.getClient();
    FutureCallback<SimpleHttpResponse> callback = new ConfigurationCallback();
    Future<SimpleHttpResponse> future = client.execute(request, callback);
    HttpResponse response = future.get();

    return Objects.nonNull(response) && 200 == response.getCode();
  }
}
