package rocks.inspectit.gepard.agent.integrationtest.spring;

import okhttp3.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.output.WaitingConsumer;
import rocks.inspectit.gepard.agent.transformation.advice.InspectitAdvice;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tests the correct behavior of the agent, when receiving a scope with a basic fqn.
 * All Methods of the class "io.opentelemetry.smoketest.springboot.controller.WebController"
 * should be instrumented with the example {@link InspectitAdvice}, which just logs "HELLO GEPARD" and "BYE GEPARD".
 */
public class ScopeTest extends SpringTestBase {

    @Test
    public void advice_is_executed() throws IOException, InterruptedException {
        configurationServerMock.configServerSetup("integrationtest/configurations/simple-scope.json");
        startTarget("/opentelemetry-extensions.jar");
        sendRequestToTarget();
        waitFor("HELLO GEPARD");
        waitFor("BYE GEPARD");
        stopTarget();
    }

    private void sendRequestToTarget() throws IOException {
        String url = String.format("http://localhost:%d/greeting", target.getMappedPort(8080));
        client.newCall(new Request.Builder().url(url).get().build()).execute();
    }

    private void waitFor(String message) {
        WaitingConsumer consumer = new WaitingConsumer();

        target.followOutput(consumer);

        boolean logged = false;

        try {
            consumer.waitUntil(frame -> frame.getUtf8String().contains(message), 60, TimeUnit.SECONDS);
            logged = true;
        } catch(TimeoutException e) {
            Assertions.fail("Did not log 'BYE GEPARD' within 60 seconds");
        }
        Assertions.assertTrue(logged);
    }
}
