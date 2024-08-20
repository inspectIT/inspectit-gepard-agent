package rocks.inspectit.gepard.agent.integrationtest.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import okhttp3.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.output.WaitingConsumer;

/**
 * Tests the correct behavior of the agent, when receiving a scope with a basic fqn. All Methods of
 * the class "io.opentelemetry.smoketest.springboot.controller.WebController" should be instrumented
 * with the example InspectIT Advice, which just logs "HELLO GEPARD" and "BYE GEPARD".
 */
public class ScopeTest extends SpringTestBase {

  @Test
  public void advice_is_executed() throws IOException, InterruptedException {
    configurationServerMock.configServerSetup("integrationtest/configurations/simple-scope.json");
    startTarget("/opentelemetry-extensions.jar");
    // Two methods are instrumented, so we expect two log entries.
    // This spins up two threads, waiting for the log entries.
    waitFor("HELLO GEPARD", 2);
    waitFor("BYE GEPARD", 2);
    // Send a request to the target to trigger the advice.
    sendRequestToTarget();
    stopTarget();
  }

  private void sendRequestToTarget() throws IOException {
    String url = String.format("http://localhost:%d/greeting", target.getMappedPort(8080));
    client.newCall(new Request.Builder().url(url).get().build()).execute();
  }

  // also a lambda is passed to this method
  private void waitFor(String message, int times) {
    WaitingConsumer consumer = new WaitingConsumer();

    target.followOutput(consumer);

    // We need to create a new thread to wait for the logs, as the consumer is blocking.
    Thread newThread =
        new Thread(
            () -> {
              int loggedTimes = 0;

              while (loggedTimes < times) {
                try {
                  consumer.waitUntil(
                      frame -> frame.getUtf8String().contains(message), 60, TimeUnit.SECONDS);
                  loggedTimes++;
                } catch (TimeoutException e) {
                  Assertions.fail("Did not log 'HELLO GEPARD' within 60 seconds");
                }
              }

              assertEquals(times, loggedTimes);
            });
    newThread.start();
  }

  @Test
  public void advice_is_executed_only_in_greeting() throws IOException, InterruptedException {
    configurationServerMock.configServerSetup(
        "integrationtest/configurations/scope-with-method.json");
    startTarget("/opentelemetry-extensions.jar");
    sendRequestToTarget();
    // Now only one method should be instrumented, so we expect only one log entry.
    waitFor("HELLO GEPARD", 1);
    waitFor("BYE GEPARD", 1);
    stopTarget();
  }
}
