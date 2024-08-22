package rocks.inspectit.gepard.agent.integrationtest.spring;

import static org.junit.jupiter.api.Assertions.assertTrue;

import okhttp3.Call;
import okhttp3.Request;
import org.junit.jupiter.api.Test;

public class ScopeTest extends SpringTestBase {

  @Test
  void scopeWithoutMethodInstrumentsAllMethods() throws Exception {
    configurationServerMock.configServerSetup("integrationtest/configurations/simple-scope.json");
    startTarget("/opentelemetry-extensions.jar");
    sendRequestToTarget();
    Thread.sleep(2000);
    String logs = target.getLogs();
    stopTarget();

    boolean loggedHelloGepardTwice = containsTimes(logs, "HELLO GEPARD", 2);
    boolean loggedByeGepardTwice = containsTimes(logs, "BYE GEPARD", 2);

    assertTrue(loggedHelloGepardTwice);
    assertTrue(loggedByeGepardTwice);
  }

  @Test
  void scopeWithOneMethodInstrumentsOneMethod() throws Exception {
    configurationServerMock.configServerSetup(
        "integrationtest/configurations/scope-with-method.json");
    startTarget("/opentelemetry-extensions.jar");
    sendRequestToTarget();

    Thread.sleep(2000);
    String logs = target.getLogs();
    stopTarget();

    boolean loggedHelloGepardTwice = containsTimes(logs, "HELLO GEPARD", 1);
    boolean loggedByeGepardTwice = containsTimes(logs, "BYE GEPARD", 1);

    assertTrue(loggedHelloGepardTwice);
    assertTrue(loggedByeGepardTwice);
  }

  @Test
  void scopeWithTwoMethodsInstrumentsTwoMethods() throws Exception {
    configurationServerMock.configServerSetup(
        "integrationtest/configurations/scope-with-multiple-methods.json");
    startTarget("/opentelemetry-extensions.jar");
    sendRequestToTarget();

    Thread.sleep(2000);
    String logs = target.getLogs();
    stopTarget();

    boolean loggedHelloGepardTwice = containsTimes(logs, "HELLO GEPARD", 2);
    boolean loggedByeGepardTwice = containsTimes(logs, "BYE GEPARD", 2);

    assertTrue(loggedHelloGepardTwice);
    assertTrue(loggedByeGepardTwice);
  }

  private void sendRequestToTarget() throws Exception {
    String url = String.format("http://localhost:%d/greeting", target.getMappedPort(8080));
    Call call = client.newCall(new Request.Builder().url(url).get().build());
    // Wait for instrumentation
    Thread.sleep(5000);
    call.execute();
  }

  private boolean containsTimes(String logs, String message, int times) {
    int count = 0;
    int index = 0;
    while (index != -1) {
      index = logs.indexOf(message, index);
      if (index != -1) {
        count++;
        index += message.length();
      }
    }
    return count == times;
  }
}
