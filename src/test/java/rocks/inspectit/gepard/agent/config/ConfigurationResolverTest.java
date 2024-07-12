package rocks.inspectit.gepard.agent.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigurationResolverTest {

    private static final String SERVER_URL_SYSTEM_PROPERTY = "inspectit.config.http.url";

    private static final String TEST_URL = "https://inspectit.rocks/";

    @Test
    void resolverReturnsUrlIfSystemPropertyExists() {
        System.setProperty(SERVER_URL_SYSTEM_PROPERTY, TEST_URL);

        String url = ConfigurationResolver.getServerUrl();

        assertEquals(TEST_URL, url);
    }
}
