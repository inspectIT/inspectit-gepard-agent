package rocks.inspectit.gepard.agent.configuration.http;

import com.google.common.annotations.VisibleForTesting;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.http.HttpRequestSender;
import rocks.inspectit.gepard.agent.internal.schedule.NamedRunnable;

/** Task to poll configuration from remote configuration server */
public class HttpConfigurationPoller implements NamedRunnable {
  private static final Logger log = LoggerFactory.getLogger(HttpConfigurationPoller.class);

  private final String serverUrl;

  public HttpConfigurationPoller(String serverUrl) {
    this.serverUrl = serverUrl;
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
    SimpleHttpRequest request = null;
    // TODO try-catch in eigene Methode auslagern
    try {
      request = HttpConfigurationFactory.createConfigurationRequest(serverUrl);
    } catch (URISyntaxException e) {
      log.error("Error building HTTP URI for configuration polling", e);
    }
    // TODO try-catch in eigene Methode auslagern
    try {
      return HttpRequestSender.send(request, new HttpConfigurationCallback());
    } catch (ExecutionException e) {
      log.error("Error executing configuration polling", e);
    } catch (InterruptedException e) {
      log.error("Configuration polling was interrupted", e);
      Thread.currentThread().interrupt();
    }
    return false;
  }

  @Override
  public String getName() {
    return "configuration-polling";
  }
}
