/* (C) 2024 */
package rocks.inspectit.gepard.agent.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.Scope;
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
    Scope scope = new Scope(true, "com.example.Application", Collections.emptyList());
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(List.of(scope));
    return new InspectitConfiguration(instrumentationConfiguration);
  }
}
