package rocks.inspectit.gepard.agent.configuration.http;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedSubject;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationUtil;

/** Callback for configuration requests to the configuration server. */
public class HttpConfigurationCallback implements FutureCallback<SimpleHttpResponse> {
  private static final Logger log = LoggerFactory.getLogger(HttpConfigurationCallback.class);

  private final ConfigurationReceivedSubject configurationSubject;

  public HttpConfigurationCallback() {
    this.configurationSubject = ConfigurationReceivedSubject.getInstance();
  }

  @Override
  public void completed(SimpleHttpResponse result) {
    log.info(
        "Fetched configuration from configuration server and received status code {}",
        result.getCode());

    // Publish Event
    if (result.getCode() == 200) {
      String body = result.getBodyText();
      InspectitConfiguration configuration = ConfigurationUtil.deserializeConfiguration(body);
      configurationSubject.notifyListeners(configuration);
    }
  }

  @Override
  public void failed(Exception ex) {
    log.error("Failed to fetch configuration from configuration server", ex);
  }

  @Override
  public void cancelled() {
    log.info("Cancelled configuration fetch");
  }
}
