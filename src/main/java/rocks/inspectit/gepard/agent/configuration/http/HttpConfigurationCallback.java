package rocks.inspectit.gepard.agent.configuration.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedSubject;

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

    if (result.getCode() != 200) return;

    log.debug("Notifying about new inspectit configuration...");
    InspectitConfiguration configuration = serializeConfiguration(result.getBodyText());
    configurationSubject.notifyListeners(configuration);
  }

  // TODO Auslagern, vllt Util-Klasse?
  private InspectitConfiguration serializeConfiguration(String body) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(body, InspectitConfiguration.class);
    } catch (IOException e) {
      log.error("Failed to deserialize inspectit configuration", e);
      // TODO Custom exception
      throw new RuntimeException();
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
