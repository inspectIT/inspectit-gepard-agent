package rocks.inspectit.gepard.agent.integrationtest.spring;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import okhttp3.Call;
import okhttp3.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.output.WaitingConsumer;
import rocks.inspectit.gepard.agent.transformation.advice.InspectitAdvice;

/**
 * Tests the correct behavior of the agent, when receiving a scope with a basic fqn. All Methods of
 * the class "io.opentelemetry.smoketest.springboot.controller.WebController" should be instrumented
 * with the example {@link InspectitAdvice}, which just logs "HELLO GEPARD" and "BYE GEPARD".
 */
class ScopeTest extends SpringTestBase {

  @Test
  void adviceIsExecuted() throws Exception {
    configurationServerMock.configServerSetup("integrationtest/configurations/simple-scope.json");
    startTarget("/opentelemetry-extensions.jar");
    sendRequestToTarget();
    waitFor("HELLO GEPARD");
    waitFor("BYE GEPARD");
    stopTarget();
  }

  private void sendRequestToTarget() throws Exception {
    String url = String.format("http://localhost:%d/greeting", target.getMappedPort(8080));
    Call call = client.newCall(new Request.Builder().url(url).get().build());
    // Wait for instrumentation
    Thread.sleep(5000);
    call.execute();
  }

  private void waitFor(String message) {
    WaitingConsumer consumer = new WaitingConsumer();

    target.followOutput(consumer);

    boolean logged = false;

    try {
      consumer.waitUntil(frame -> frame.getUtf8String().contains(message), 60, TimeUnit.SECONDS);
      logged = true;
    } catch (TimeoutException e) {
      Assertions.fail("Did not log 'BYE GEPARD' within 60 seconds");
    }
    Assertions.assertTrue(logged);
  }
}
