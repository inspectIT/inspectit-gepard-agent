package rocks.inspectit.gepard.agent.configuration.http;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConfigurationCallback implements FutureCallback<SimpleHttpResponse> {
  private static final Logger log = LoggerFactory.getLogger(HttpConfigurationCallback.class);

  @Override
  public void completed(SimpleHttpResponse result) {
    log.info(
        "Fetched configuration from configuration server and received status code {}",
        result.getCode());

    // TODO Trigger Instrumentation
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
