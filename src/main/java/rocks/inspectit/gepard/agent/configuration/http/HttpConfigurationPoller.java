package rocks.inspectit.gepard.agent.configuration.http;

import com.google.common.annotations.VisibleForTesting;
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
import rocks.inspectit.gepard.agent.configuration.ConfigurationResolver;
import rocks.inspectit.gepard.agent.configuration.ConfigurationSubject;
import rocks.inspectit.gepard.agent.internal.http.HttpClientHolder;
import rocks.inspectit.gepard.agent.internal.schedule.NamedRunnable;

/** Task to poll configuration from remote configuration server */
public class HttpConfigurationPoller implements NamedRunnable {
  private static final Logger log = LoggerFactory.getLogger(HttpConfigurationPoller.class);

  private final String serverUrl;

  private final ConfigurationSubject configurationSubject;

  public HttpConfigurationPoller(String serverUrl) {
    this.serverUrl = serverUrl;
    this.configurationSubject = new ConfigurationSubject();
    ConfigurationResolver configurationResolver = new ConfigurationResolver();
    this.configurationSubject.addListener(configurationResolver);
  }

  public void run() {
    log.info("Polling configuration...");
    boolean successful;
    try {
      successful = pollConfiguration();
    } catch (Throwable e) {
      log.error("Error while polling configuration", e);
      return;
    }

    if (successful) log.info("Configuration was polled successfully");
    else log.error("Configuration polling failed");
  }

  /**
   * Sends a poll to the configuration server to receive a configuration.
   *
   * @return true, if the configuration was polled successfully
   */
  @VisibleForTesting
  boolean pollConfiguration() {
    log.debug("Fetching configuration from server...");
    SimpleHttpRequest request = null;
    try {
      request = HttpConfigurationFactory.createConfigurationRequest(serverUrl);
    } catch (URISyntaxException e) {
      log.error("Error building HTTP URI for configuration polling", e);
    }
    try {
      return doSend(request);
    } catch (ExecutionException e) {
      log.error("Error executing configuration polling", e);
    } catch (InterruptedException e) {
      log.error("Configuration polling was interrupted", e);
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
  private boolean doSend(SimpleHttpRequest request)
      throws ExecutionException, InterruptedException {
    // TODO Duplicate of NotificationManager#doSend()
    CloseableHttpAsyncClient client = HttpClientHolder.getClient();
    FutureCallback<SimpleHttpResponse> callback =
        new HttpConfigurationCallback(this.configurationSubject);
    Future<SimpleHttpResponse> future = client.execute(request, callback);
    HttpResponse response = future.get();

    return Objects.nonNull(response) && 200 == response.getCode();
  }

  @Override
  public String getName() {
    return "configuration-polling";
  }
}
