package rocks.inspectit.gepard.agent.config;


import org.apache.hc.core5.http.HttpResponse;

public class ConfigurationUpdatedEvent {
    private final HttpResponse response;

    public ConfigurationUpdatedEvent(HttpResponse response) {
        this.response = response;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
