/* (C) 2024 */
package rocks.inspectit.gepard.agent.configuration.http;

import java.io.IOException;
import java.util.Objects;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ProtocolException;
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
    log.info("Received status code {}", statusCode);

    if (statusCode == 404) {
      log.error("Configuration not found on configuration server");
    } else if (statusCode == 200) {
      try {
        Header registrationHeader = response.getHeader("x-gepard-service-registered");
        if (Objects.isNull(registrationHeader)) {
          log.error("Configuration server did not return registration header!");
        } else if (registrationHeader.getValue().equals("true")) {
          log.info("Connection to configuration server was successfully established.");
        } else {
          log.debug("Connection to configuration server reused.");
        }
      } catch (ProtocolException e) {
        log.error(
            "Error reading response header. There might be an issue with the config-server!", e);
      }
      log.info("Configuration fetched successfully");
    }
  }
}
