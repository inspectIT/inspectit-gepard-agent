package rocks.inspectit.gepard.agent.configuration.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.ConfigurationSubject;
import rocks.inspectit.gepard.agent.configuration.model.InstrumentationRequest;

public class HttpConfigurationCallback implements FutureCallback<SimpleHttpResponse> {
  private static final Logger log = LoggerFactory.getLogger(HttpConfigurationCallback.class);

  private final ConfigurationSubject configurationSubject;

  public HttpConfigurationCallback(ConfigurationSubject configurationSubject) {
    this.configurationSubject = configurationSubject;
  }

  @Override
  public void completed(SimpleHttpResponse result) {
    log.info(
        "Fetched configuration from configuration server and received status code {}",
        result.getCode());

    if (result.getCode() != 200) {
      return;
    }

    InstrumentationRequest configuration = serializeConfiguration(result.getBodyText());
    configurationSubject.notifyListeners(configuration);
  }

  private InstrumentationRequest serializeConfiguration(String body) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(body, InstrumentationRequest.class);
    } catch (Exception e) {
      log.error("Failed to deserialize configuration", e);
      return null;
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
