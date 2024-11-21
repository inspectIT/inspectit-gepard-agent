/* (C) 2024 */
package rocks.inspectit.gepard.agent.integrationtest;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import rocks.inspectit.gepard.agent.integrationtest.utils.OkHttpUtils;

/**
 * This class is used to create a mock for a tracing backend. We can write and retrieve traces
 * there.
 */
public class ObservabilityBackendMock {

  private static final Logger logger = LoggerFactory.getLogger(ObservabilityBackendMock.class);
  private static final DockerImageName MOCK_IMAGE =
      DockerImageName.parse(
              "ghcr.io/open-telemetry/opentelemetry-java-instrumentation/smoke-test-fake-backend")
          .withTag("20221127.3559314891");
  private static OkHttpClient client = OkHttpUtils.client();

  private final GenericContainer<?> server;

  private ObservabilityBackendMock(Network network) {
    server =
        new GenericContainer<>(MOCK_IMAGE)
            .withNetwork(network)
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/health").forPort(8080))
            .withNetworkAliases("backend")
            .withLogConsumer(new Slf4jLogConsumer(logger));
  }

  static ObservabilityBackendMock create(Network network) {
    return new ObservabilityBackendMock(network);
  }

  void start() {
    server.start();
  }

  void stop() {
    server.stop();
  }

  void setNetwork(Network network) {
    server.setNetwork(network);
  }

  GenericContainer<?> getServer() {
    return server;
  }

  void reset() throws IOException {
    client
        .newCall(
            new Request.Builder()
                .url(
                    String.format(
                        "http://%s:%d/clear", server.getHost(), server.getMappedPort(8080)))
                .build())
        .execute()
        .close();
  }
}
