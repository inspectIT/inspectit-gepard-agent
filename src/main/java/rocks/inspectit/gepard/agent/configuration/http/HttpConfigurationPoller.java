package rocks.inspectit.gepard.agent.configuration.http;

import com.google.common.annotations.VisibleForTesting;
import java.net.URISyntaxException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.persistence.ConfigurationPersistence;
import rocks.inspectit.gepard.agent.internal.http.HttpRequestSender;
import rocks.inspectit.gepard.agent.internal.schedule.NamedRunnable;

/** Task to poll configuration from remote configuration server */
public class HttpConfigurationPoller implements NamedRunnable {
  private static final Logger log = LoggerFactory.getLogger(HttpConfigurationPoller.class);

  private final String serverUrl;

  private final ConfigurationPersistence persistence;

  /** Flag to mark first polling attempt */
  private boolean isFirstAttempt = true;

  public HttpConfigurationPoller(String serverUrl, ConfigurationPersistence persistence) {
    this.serverUrl = serverUrl;
    this.persistence = persistence;
  }

  public void run() {
    log.debug("Polling configuration...");
    boolean successful;
    try {
      successful = pollConfiguration();
    } catch (Exception e) {
      log.error("Error while polling configuration", e);
      successful = false;
    }

    if (!successful && isFirstAttempt) {
      log.info("Trying to load local configuration...");
      persistence.loadLocalConfiguration();
    }

    isFirstAttempt = false;
  }

  /**
   * Sends a poll to the configuration server to receive a configuration.
   *
   * @return true, if the configuration was polled successfully
   */
  @VisibleForTesting
  boolean pollConfiguration() {
    SimpleHttpRequest request = createConfigurationRequest();
    HttpConfigurationCallback callback = new HttpConfigurationCallback();

    return HttpRequestSender.send(request, callback);
  }

  /**
   * @return the created polling request
   */
  private SimpleHttpRequest createConfigurationRequest() {
    SimpleHttpRequest request = null;
    try {
      request = HttpConfigurationFactory.createConfigurationRequest(serverUrl);
    } catch (URISyntaxException e) {
      log.error("Error building HTTP URI for configuration polling", e);
    }
    return request;
  }

  @Override
  public String getName() {
    return "configuration-polling";
  }
}
