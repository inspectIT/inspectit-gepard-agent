/* (C) 2024 */
package rocks.inspectit.gepard.agent.notification.http;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Callback for notifications to the configuration server. Currently, only used for logging. */
public class NotificationCallback implements FutureCallback<SimpleHttpResponse> {
  private static final Logger log = LoggerFactory.getLogger(NotificationCallback.class);

  @Override
  public void completed(SimpleHttpResponse result) {
    log.info("Notified configuration server and received status code {}", result.getCode());
  }

  @Override
  public void failed(Exception ex) {
    log.error("Failed to notify configuration server", ex);
  }

  @Override
  public void cancelled() {
    log.info("Cancelled notification to configuration server");
  }
}
