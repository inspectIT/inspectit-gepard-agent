/* (C) 2024 */
package rocks.inspectit.gepard.agent.integrationtest.spring;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Request;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.Test;

public class ScopeTest extends SpringTestBase {

  private static final String configDir = "integrationtest/configurations/";

  @Test
  void scopeWithoutMethodInstrumentsAllMethods() throws Exception {
    configurationServerMock.configServerSetup(configDir + "simple-scope.json");
    startTarget("/opentelemetry-extensions.jar");
    awaitInstrumentationUpdate(1);

    sendRequestToTarget("/greeting");
    String logs = target.getLogs();
    stopTarget();

    assertLogs(logs, 2);
  }

  @Test
  void scopeWithOneMethodInstrumentsOneMethod() throws Exception {
    configurationServerMock.configServerSetup(configDir + "scope-with-method.json");
    startTarget("/opentelemetry-extensions.jar");
    awaitInstrumentationUpdate(1);

    sendRequestToTarget("/greeting");
    String logs = target.getLogs();
    stopTarget();

    assertLogs(logs, 1);
  }

  @Test
  void scopeWithTwoMethodsInstrumentsTwoMethods() throws Exception {
    configurationServerMock.configServerSetup(configDir + "scope-with-multiple-methods.json");
    startTarget("/opentelemetry-extensions.jar");
    awaitInstrumentationUpdate(1);

    sendRequestToTarget("/greeting");
    String logs = target.getLogs();
    stopTarget();

    assertLogs(logs, 2);
  }

  @Test
  void emptyConfigurationDoesntInstrument() throws Exception {
    configurationServerMock.configServerSetup(configDir + "empty-configuration.json");
    startTarget("/opentelemetry-extensions.jar");
    awaitInstrumentationUpdate(1);

    sendRequestToTarget("/greeting");
    String logs = target.getLogs();
    stopTarget();

    assertLogs(logs, 0);
  }

  @Test
  void multipleScopesInstrumentAllSelectedMethods() throws Exception {
    configurationServerMock.configServerSetup(configDir + "multiple-scopes.json");
    startTarget("/opentelemetry-extensions.jar");
    // We need to instrument 2 classes
    awaitInstrumentationUpdate(2);

    sendRequestToTarget("/greeting");
    sendRequestToTarget("/front");

    String logs = target.getLogs();
    stopTarget();

    assertLogs(logs, 4);
  }

  @Test
  void configurationUpdatesAreApplied() throws Exception {
    // Set up config server to instrument multiple methods
    configurationServerMock.configServerSetup(configDir + "scope-with-multiple-methods.json");
    startTarget("/opentelemetry-extensions.jar");
    awaitInstrumentationUpdate(1);

    sendRequestToTarget("/greeting");
    String logs = target.getLogs();

    assertLogs(logs, 2);

    // Update configuration to only instrument one method
    configurationServerMock.reset();
    configurationServerMock.configServerSetup(configDir + "scope-with-method.json");
    awaitConfigurationUpdate();
    awaitInstrumentationUpdate(1);

    sendRequestToTarget("/greeting");
    logs = target.getLogs();
    stopTarget();

    // 2 logs before update + 1 log after update
    assertLogs(logs, 3);
  }

  private void sendRequestToTarget(String path) throws Exception {
    String url = String.format("http://localhost:%d%s", target.getMappedPort(8080), path);
    Call call = client.newCall(new Request.Builder().url(url).get().build());
    call.execute();

    // wait for logs
    Thread.sleep(1000);
  }

  /**
   * Checks, if the logs contain "HELLO GEPARD" and "BYE GEPARD" for a specific number of times
   *
   * @param logs the logs
   * @param times the amount of times "HELLO GEPARD" and "BYE GEPARD" should be present in the logs
   */
  private void assertLogs(String logs, int times) {
    boolean loggedHelloGepardTwice = containsTimes(logs, "HELLO GEPARD", times);
    boolean loggedByeGepardTwice = containsTimes(logs, "BYE GEPARD", times);

    assertTrue(loggedHelloGepardTwice);
    assertTrue(loggedByeGepardTwice);
  }

  /**
   * Checks, if a specific message can be found for a specific amount of times inside the provided
   * logs.
   *
   * @return true, if the message appears the expected amount of times in the logs
   */
  private boolean containsTimes(String logs, String message, int times) {
    int count = countTimes(logs, message);
    return count == times;
  }

  /**
   * Counts how many times a specific message can be found inside the provided logs
   *
   * @param logs the logs
   * @param message the message to look for
   * @return the amount of times the message appears in the logs
   */
  private int countTimes(String logs, String message) {
    int count = 0;
    int index = 0;
    while (index != -1) {
      index = logs.indexOf(message, index);
      if (index != -1) {
        count++;
        index += message.length();
      }
    }
    return count;
  }

  /**
   * Waits until the instrumentation was applied in the method hooks for the specified amount of
   * times. The test should not fail here, if no further update message was found.
   */
  private void awaitInstrumentationUpdate(int amount) {
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
  private void awaitConfigurationUpdate() {
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
   * messages has increased, it is assumed that a new configuration has been pooled.
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
