package rocks.inspectit.gepard.agent.agentconfiguration.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;

import java.net.URI;
import java.net.URISyntaxException;

public class AgentConfigurationPollingFactory {
    /**
     * Create an HTTP post request to ask the configuration server for new configurations.
     *
     * @return the HTTP post request, containing agent information
     * @throws URISyntaxException invalid uri
     * @throws JsonProcessingException corrupted agent information
     */
    public static SimpleHttpRequest createPollingRequest(String url)
            throws URISyntaxException {
        URI uri = new URI(url);

        return SimpleRequestBuilder.get(uri).build();
    }
}
