package rocks.inspectit.gepard.agent.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetSystemProperty;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigurationResolverTest {

    private static final String SERVER_URL_SYSTEM_PROPERTY = "inspectit.config.http.url";

    private static final String SERVER_URL_ENV_PROPERTY = "INSPECTIT_CONFIG_HTTP_URL";

    private static final String TEST_URL_1 = "https://inspectit.rocks/";

    private static final String TEST_URL_2 = "https://inspectit.github.io/inspectit-ocelot";

    @BeforeAll
    @SetEnvironmentVariable(key = SERVER_URL_ENV_PROPERTY, value = TEST_URL_1)
    static void clear() {}

    @Test
    @SetSystemProperty(key = SERVER_URL_SYSTEM_PROPERTY, value= TEST_URL_1)
    void resolverReturnsUrlIfSystemPropertyExists() {
        String url = ConfigurationResolver.getServerUrl();

        assertEquals(TEST_URL_1, url);
    }

    @Test
    void resolverReturnsUrlIfEnvPropertyExists() {
        String url = ConfigurationResolver.getServerUrl();

        assertEquals(TEST_URL_1, url);
    }

    @Test
    @SetSystemProperty(key = SERVER_URL_SYSTEM_PROPERTY, value= TEST_URL_2)
    void resolverReturnsSystemPropertyIfSystemAndEnvPropertyExist() {
        String url = ConfigurationResolver.getServerUrl();

        assertEquals(TEST_URL_2, url);
    }
}
