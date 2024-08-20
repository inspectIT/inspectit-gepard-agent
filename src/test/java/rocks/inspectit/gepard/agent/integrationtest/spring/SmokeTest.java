package rocks.inspectit.gepard.agent.integrationtest.spring;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Smoke test for Spring Boot application. Verifies if agent is able to load and produces traces.
 */
public class SmokeTest extends SpringTestBase {

  @Test
  public void loadExtensionFromJar() throws IOException, InterruptedException {
    startTarget("/opentelemetry-extensions.jar");
    basicTracingWorks();
    stopTarget();
  }

  @Test
  public void loadExtensionFromFolder() throws IOException, InterruptedException {
    startTarget("/");
    basicTracingWorks();
    stopTarget();
  }

  private void basicTracingWorks() throws IOException, InterruptedException {
    String url = String.format("http://localhost:%d/greeting", target.getMappedPort(8080));

    // Request to the target application
    Request request = new Request.Builder().url(url).get().build();

    // Get the agent version from the jar file
    String currentAgentVersion =
        (String)
            new JarFile(agentPath)
                .getManifest()
                .getMainAttributes()
                .get(Attributes.Name.IMPLEMENTATION_VERSION);

    // Execute the request
    Response response = client.newCall(request).execute();

    Collection<ExportTraceServiceRequest> traces = waitForTraces();

    // Assertions
    Assertions.assertEquals(0, countSpansByName(traces, "WebController.greeting"));
    Assertions.assertEquals(1, countSpansByName(traces, "WebController.withSpan"));
    Assertions.assertNotEquals(
        0, countResourcesByValue(traces, "telemetry.distro.version", currentAgentVersion));
  }
}
