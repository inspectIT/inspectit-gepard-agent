/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.state.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.testutils.InspectitConfigurationTestUtil;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;

class InspectitConfigurationHolderTest {

  private static final InspectitConfigurationHolder holder = InspectitConfigurationHolder.create();

  @Test
  void configurationNotNull() {
    InspectitConfiguration configuration = holder.getConfiguration();

    assertNotNull(configuration);
  }

  @Test
  void configurationIsUpdated() {
    InspectitConfiguration configuration = InspectitConfigurationTestUtil.expectedConfiguration();
    ConfigurationReceivedEvent event = new ConfigurationReceivedEvent(this, configuration);

    holder.handleConfiguration(event);

    InspectitConfiguration updatedConfiguration = holder.getConfiguration();
    assertEquals(configuration, updatedConfiguration);
  }
}
