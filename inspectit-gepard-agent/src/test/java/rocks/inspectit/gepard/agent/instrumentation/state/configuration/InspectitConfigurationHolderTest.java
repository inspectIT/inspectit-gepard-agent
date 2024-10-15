/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.ScopeConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;

class InspectitConfigurationHolderTest {

  private static final InspectitConfigurationHolder holder = InspectitConfigurationHolder.create();

  @Test
  void configurationNotNull() {
    InspectitConfiguration configuration = holder.getConfiguration();

    assertNotNull(configuration);
  }

  @Test
  void configurationIsUpdated() {
    InspectitConfiguration configuration = createConfiguration();
    ConfigurationReceivedEvent event = new ConfigurationReceivedEvent(this, configuration);

    holder.handleConfiguration(event);

    InspectitConfiguration updatedConfiguration = holder.getConfiguration();
    assertEquals(configuration, updatedConfiguration);
  }

  private InspectitConfiguration createConfiguration() {
    ScopeConfiguration scope = new ScopeConfiguration(true, "com.example.Application", Collections.emptyList());
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(List.of(scope));
    return new InspectitConfiguration(instrumentationConfiguration);
  }
}
