package rocks.inspectit.gepard.agent.integrationtest;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.mockserver.client.MockServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

/**
 * This Class is used to setup a configuration server mock for the agent integration tests. It
 * utilizes the MockServerContainer from the testcontainers library to create a mock server The
 * Expectations can be configured by calling the configServerSetup method. This should be done
 * before the target container where the agent is running is started. (e.g. in every test method)
 */
public class ConfigurationServerMock {

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationServerMock.class);

  private MockServerContainer server;
  private MockServerClient serverClient;

  private ConfigurationServerMock(Network network) {
    DockerImageName mockServerImage =
        DockerImageName.parse("mockserver/mockserver:mockserver-5.15.0");
    server =
        new MockServerContainer(mockServerImage)
            .withNetwork(network)
            .withNetworkAliases("config-server");
    server.start();
    serverClient = new MockServerClient(server.getHost(), server.getServerPort());
  }

  public static ConfigurationServerMock create(Network network) {
    return new ConfigurationServerMock(network);
  }

  public void start() {
    server.start();
  }

  public void stop() {
    server.stop();
  }

  public void configServerSetup(String config_path) throws IOException {
    ClassLoader loader = getClass().getClassLoader();
    File file = new File(loader.getResource(config_path).getFile());
    String body = FileUtils.readFileToString(file, "UTF-8");

    serverClient
        .when(request().withMethod("GET").withPath("/api/v1/agent-configuration"))
        .respond(response().withStatusCode(200).withBody(body));

    serverClient
        .when(request().withMethod("POST").withPath("/api/v1/connections"))
        .respond(response().withStatusCode(200));
  }

  public void reset() {
    serverClient.reset();
  }
}
