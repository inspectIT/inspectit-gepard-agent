/* (C) 2024 */
package rocks.inspectit.gepard.agent.integrationtest;

import static rocks.inspectit.gepard.agent.integrationtest.utils.LogUtils.countTimes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.trace.v1.Span;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.MountableFile;
import rocks.inspectit.gepard.agent.integrationtest.utils.OkHttpUtils;

/**
 * Base class for integration tests. First starts the Observability Backend Mock and Configuration
 * Server Mock as Testcontainers. We can define a target application to test by extending this class
 * and implementing the abstract methods. The target application is also started as a Testcontainer
 * and an agent is attached to it.
 */
public abstract class IntegrationTestBase {

  private static final Logger logger = LoggerFactory.getLogger(IntegrationTestBase.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  protected static OkHttpClient client = OkHttpUtils.client();

  private static final Network network = Network.newNetwork();

  protected static final String configDir = "integrationtest/configurations/";

  protected static final String agentPath =
      System.getProperty("io.opentelemetry.smoketest.agentPath");
  // Javaagent with extensions embedded inside it
  protected static final String extendedAgentPath =
      System.getProperty("io.opentelemetry.smoketest.extendedAgentPath");
  protected static final String extensionPath =
      System.getProperty("io.opentelemetry.smoketest.extensionPath");

  protected abstract String getTargetImage(int jdk);

  protected abstract WaitStrategy getTargetWaitStrategy();

  /** Subclasses can override this method to customise target application's environment */
  protected Map<String, String> getExtraEnv() {
    return Collections.emptyMap();
  }

  // The Observability Backend Mock
  private static TracingBackendMock tracingBackendMock;
  // The configuration Server Mock
  public static ConfigurationServerMock configurationServerMock;

  @BeforeAll
  static void setup() {
    tracingBackendMock = TracingBackendMock.create(network);
    tracingBackendMock.start();

    configurationServerMock = ConfigurationServerMock.create(network);
    configurationServerMock.start();
  }

  protected GenericContainer<?> target;

  protected void startTarget(String extensionLocation) {
    target = buildTargetContainer(agentPath, extensionLocation);
    target.start();
  }

  void startTargetWithExtendedAgent() {
    target = buildTargetContainer(extendedAgentPath, null);
    target.start();
  }

  private GenericContainer<?> buildTargetContainer(String agentPath, String extensionLocation) {

    GenericContainer<?> result =
        new GenericContainer<>(getTargetImage(17))
            .withExposedPorts(8080)
            .withNetwork(network)
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .withCopyFileToContainer(
                MountableFile.forHostPath(agentPath), "/opentelemetry-javaagent.jar")
            // Adds instrumentation agent with debug configuration to the target application
            .withEnv("JAVA_TOOL_OPTIONS", "-javaagent:/opentelemetry-javaagent.jar")
            .withEnv("OTEL_BSP_MAX_EXPORT_BATCH", "1")
            .withEnv("OTEL_BSP_SCHEDULE_DELAY", "10")
            // .withEnv("OTEL_PROPAGATORS", "tracecontext,baggage,demo")
            // in the meantime, force smoke tests to use grpc protocol for all exporters
            .withEnv("OTEL_EXPORTER_OTLP_ENDPOINT", "http://backend:8080")
            .withEnv("OTEL_EXPORTER_OTLP_PROTOCOL", "grpc")
            .withEnv("INSPECTIT_CONFIG_HTTP_URL", "http://config-server:1080" + "/api/v1")
            .withEnv(getExtraEnv())
            .waitingFor(getTargetWaitStrategy());
    // If external extensions are requested
    if (extensionLocation != null) {
      // Asks instrumentation agent to include extensions from given location into its runtime
      result =
          result
              .withCopyFileToContainer(
                  MountableFile.forHostPath(extensionPath), "/opentelemetry-extensions.jar")
              .withEnv("OTEL_JAVAAGENT_EXTENSIONS", extensionLocation);
    }
    return result;
  }

  @AfterEach
  void reset() throws IOException {
    tracingBackendMock.reset();
    configurationServerMock.reset();
  }

  protected void stopTarget() {
    target.stop();
  }

  @AfterAll
  static void cleanup() {
    tracingBackendMock.stop();
    configurationServerMock.stop();
  }

  protected static int countResourcesByValue(
      Collection<ExportTraceServiceRequest> traces, String resourceName, String value) {
    return (int)
        traces.stream()
            .flatMap(it -> it.getResourceSpansList().stream())
            .flatMap(it -> it.getResource().getAttributesList().stream())
            .filter(
                kv ->
                    kv.getKey().equals(resourceName)
                        && kv.getValue().getStringValue().equals(value))
            .count();
  }

  protected static int countSpansByName(
      Collection<ExportTraceServiceRequest> traces, String spanName) {
    return (int) getSpanStream(traces).filter(it -> it.getName().equals(spanName)).count();
  }

  protected static int countSpansByAttributeValue(
      Collection<ExportTraceServiceRequest> traces, String attributeName, String attributeValue) {
    return (int)
        getSpanStream(traces)
            .flatMap(it -> it.getAttributesList().stream())
            .filter(
                kv ->
                    kv.getKey().equals(attributeName)
                        && kv.getValue().getStringValue().equals(attributeValue))
            .count();
  }

  protected static Stream<Span> getSpanStream(Collection<ExportTraceServiceRequest> traces) {
    return traces.stream()
        .flatMap(it -> it.getResourceSpansList().stream())
        .flatMap(it -> it.getScopeSpansList().stream())
        .flatMap(it -> it.getSpansList().stream());
  }

  protected Collection<ExportTraceServiceRequest> waitForTraces()
      throws IOException, InterruptedException {
    String content = waitForContent();

    return StreamSupport.stream(OBJECT_MAPPER.readTree(content).spliterator(), false)
        .map(
            it -> {
              ExportTraceServiceRequest.Builder builder = ExportTraceServiceRequest.newBuilder();
              try {
                JsonFormat.parser().merge(OBJECT_MAPPER.writeValueAsString(it), builder);
              } catch (InvalidProtocolBufferException | JsonProcessingException e) {
                e.printStackTrace();
              }
              return builder.build();
            })
        .collect(Collectors.toList());
  }

  private String waitForContent() throws IOException, InterruptedException {
    long previousSize = 0;
    long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);
    String content = "[]";
    while (System.currentTimeMillis() < deadline) {

      Request request =
          new Request.Builder()
              .url(
                  String.format(
                      "http://%s:%d/get-traces",
                      tracingBackendMock.getServer().getHost(),
                      tracingBackendMock.getServer().getMappedPort(8080)))
              .build();

      try (ResponseBody body = client.newCall(request).execute().body()) {
        content = body.string();
      }

      if (content.length() > 2 && content.length() == previousSize) {
        break;
      }
      previousSize = content.length();
      System.out.printf("Current content size %d%n", previousSize);
      TimeUnit.MILLISECONDS.sleep(500);
    }

    return content;
  }

  /**
   * Waits until the instrumentation was applied in the method hooks for the specified amount of
   * times. The test should not fail here, if no further update message was found.
   */
  protected void awaitInstrumentationUpdate(int amount) {
    String updateMessage =
        "method hooks for io.opentelemetry.smoketest.springboot.controller.WebController";

    try {
      awaitUpdateMessage(updateMessage, amount);
    } catch (ConditionTimeoutException e) {
      System.out.println("No instrumentation update occurred");
    }
  }

  /**
   * Waits until the configuration was polled one more time. The test should not fail here, if no
   * further update message was found.
   */
  protected void awaitConfigurationUpdate() {
    String updateMessage =
        "Fetched configuration from configuration server and received status code 200";
    try {
      awaitUpdateMessage(updateMessage, 1);
    } catch (ConditionTimeoutException e) {
      System.out.println("No configuration update occurred");
    }
  }

  /**
   * Waits until a certain update message was logged again. This happens via checking the container
   * logs. First the method counts the current amount of update messages. If the amount of update
   * messages has increased, we assume an event has occurred.
   *
   * @param updateMessage the message, which will be waited for
   */
  private void awaitUpdateMessage(String updateMessage, int amount) {
    String logs = target.getLogs();
    int updateCount = countTimes(logs, updateMessage);

    Awaitility.await()
        .pollDelay(5, TimeUnit.SECONDS)
        .atMost(15, TimeUnit.SECONDS)
        .until(
            () -> {
              String newLogs = target.getLogs();
              int currentUpdateCount = countTimes(newLogs, updateMessage);
              return currentUpdateCount >= updateCount + amount;
            });
  }
}
