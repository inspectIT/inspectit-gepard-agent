/* (C) 2024 */
package rocks.inspectit.gepard.agent.integrationtest.spring.scope;

import static rocks.inspectit.gepard.agent.integrationtest.utils.LogUtils.assertLogs;

import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.integrationtest.spring.SpringTestBase;

public class ScopeTest extends SpringTestBase {

  @Test
  void scopeWithoutMethodInstrumentsAllMethods() throws Exception {
    configurationServerMock.configServerSetup(configDir + "simple-config.json");
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
    configurationServerMock.configServerSetup(configDir + "empty-config.json");
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
}
