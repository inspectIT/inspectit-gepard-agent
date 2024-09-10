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
    sendRequestToTarget("/greeting");
    Thread.sleep(2000);
    String logs = target.getLogs();
    stopTarget();

    assertLogs(logs, 2);
  }

  @Test
  void scopeWithOneMethodInstrumentsOneMethod() throws Exception {
    configurationServerMock.configServerSetup(
        "integrationtest/configurations/scope-with-method.json");
    startTarget("/opentelemetry-extensions.jar");
    sendRequestToTarget("/greeting");

    Thread.sleep(2000);
    String logs = target.getLogs();
    stopTarget();

    assertLogs(logs, 1);
  }

  @Test
  void scopeWithTwoMethodsInstrumentsTwoMethods() throws Exception {
    configurationServerMock.configServerSetup(
        "integrationtest/configurations/scope-with-multiple-methods.json");
    startTarget("/opentelemetry-extensions.jar");
    sendRequestToTarget("/greeting");

    Thread.sleep(2000);
    String logs = target.getLogs();
    stopTarget();

    assertLogs(logs, 2);
  }

  @Test
  void emptyConfigurationDoesntInstrument() throws Exception {
    configurationServerMock.configServerSetup(
        "integrationtest/configurations/empty-configuration.json");
    startTarget("/opentelemetry-extensions.jar");
    sendRequestToTarget("/greeting");

    Thread.sleep(2000);
    String logs = target.getLogs();
    stopTarget();

    assertLogs(logs, 0);
  }

  @Test
  void multipleScopesInstrumentAllSelectedMethods() throws Exception {
    configurationServerMock.configServerSetup(
        "integrationtest/configurations/multiple-scopes.json");
    startTarget("/opentelemetry-extensions.jar");
    sendRequestToTarget("/greeting");
    sendRequestToTarget("/front");

    String logs = target.getLogs();
    stopTarget();

    assertLogs(logs, 4);
  }

  private void sendRequestToTarget(String path) throws Exception {
    String url = String.format("http://localhost:%d%s", target.getMappedPort(8080), path);
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

  private void assertLogs(String logs, int i) {
    boolean loggedHelloGepardTwice = containsTimes(logs, "HELLO GEPARD", i);
    boolean loggedByeGepardTwice = containsTimes(logs, "BYE GEPARD", i);

    assertTrue(loggedHelloGepardTwice);
    assertTrue(loggedByeGepardTwice);
  }
}
