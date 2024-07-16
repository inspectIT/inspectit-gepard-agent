package rocks.inspectit.gepard.agent.agentconfiguration.http;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.notify.http.NotificationCallback;

public class ConfigurationCallback implements FutureCallback<SimpleHttpResponse> {
    private static final Logger log = LoggerFactory.getLogger(ConfigurationCallback.class);

    @Override
    public void completed(SimpleHttpResponse result) {
        log.info("Asked configuration server for configuration and received status code {}", result.getCode());
    }

    @Override
    public void failed(Exception ex) {
        log.error("Failed to ask configuration server for configuration.", ex);
    }

    @Override
    public void cancelled() {
        log.info("Cancelled configuration request.");
    }

}
