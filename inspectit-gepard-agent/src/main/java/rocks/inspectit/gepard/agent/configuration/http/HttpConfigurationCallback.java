/* (C) 2024 */
package rocks.inspectit.gepard.agent.configuration.http;

import java.io.IOException;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedSubject;
import rocks.inspectit.gepard.agent.internal.configuration.util.ConfigurationMapper;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;

/** Callback for configuration requests to the configuration server. */
public class HttpConfigurationCallback implements FutureCallback<SimpleHttpResponse> {
  private static final Logger log = LoggerFactory.getLogger(HttpConfigurationCallback.class);

  @Override
  public void completed(SimpleHttpResponse result) {

    logStatus(result);
    // Publish Event
    if (result.getCode() == 200 || result.getCode() == 201) {
      String body = result.getBodyText();

      try {
        InspectitConfiguration configuration = ConfigurationMapper.toObject(body);

        ConfigurationReceivedSubject configurationSubject =
            ConfigurationReceivedSubject.getInstance();
        configurationSubject.notifyObservers(configuration);
      } catch (IOException e) {
        log.error("Could not process new configuration", e);
      }
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

  private void logStatus(SimpleHttpResponse response) {
    int statusCode = response.getCode();

    if (statusCode == 200) {
      log.info("Configuration fetched successfully");
    } else if (statusCode == 201) {
      log.info(
          "Connection to configuration server was successfully established. Configuration fetched successfully");
    } else if (statusCode == 404) {
      log.error("Configuration not found on configuration server");
    } else {
      log.error(
          "Unexpected status code: {}. Please check the configuration server connection.",
          statusCode);
    }
  }
}
