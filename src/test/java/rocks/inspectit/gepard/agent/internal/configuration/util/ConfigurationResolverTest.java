package rocks.inspectit.gepard.agent.internal.configuration.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.bytebuddy.description.type.TypeDescription;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import rocks.inspectit.gepard.agent.internal.configuration.ConfigurationHolder;
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.Scope;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;

class ConfigurationResolverTest {

  private final Class<?> TEST_CLASS = getClass();

  // In the future, we might use some library like powermock to mock the ConfigurationHolder
  @AfterEach
  void clearConfigurationHolder() {
    InspectitConfiguration defaultConfiguration = new InspectitConfiguration();
    ConfigurationReceivedEvent event = new ConfigurationReceivedEvent(this, defaultConfiguration);

    ConfigurationHolder holder = ConfigurationHolder.getInstance();
    holder.handleConfiguration(event);
  }

  @Test
  void classShouldNotBeRetransformed() {
    boolean shouldRetransform = ConfigurationResolver.shouldRetransform(TEST_CLASS);

    assertFalse(shouldRetransform);
  }

  @Test
  void classShouldBeRetransformed() {
    updateConfigurationHolder(true);

    boolean shouldRetransform = ConfigurationResolver.shouldRetransform(TEST_CLASS);

    assertTrue(shouldRetransform);
  }

  @Test
  void typeShouldNotBeInstrumented() {
    TypeDescription type = new TypeDescription.ForLoadedType(TEST_CLASS);
    boolean shouldInstrument = ConfigurationResolver.shouldInstrument(type);

    assertFalse(shouldInstrument);
  }

  @Test
  void typeShouldBeInstrumented() {
    updateConfigurationHolder(true);
    TypeDescription type = new TypeDescription.ForLoadedType(TEST_CLASS);
    boolean shouldInstrument = ConfigurationResolver.shouldInstrument(type);

    assertTrue(shouldInstrument);
  }

  @Test
  void typeShouldBeDeinstrumented() {
    updateConfigurationHolder(false);
    TypeDescription type = new TypeDescription.ForLoadedType(TEST_CLASS);
    boolean shouldInstrument = ConfigurationResolver.shouldInstrument(type);

    assertFalse(shouldInstrument);
  }

  /**
   * Updates the current {@link InspectitConfiguration}
   *
   * @param enabled the status of the scope for this test class
   */
  private void updateConfigurationHolder(boolean enabled) {
    Scope scope = new Scope(TEST_CLASS.getName(), enabled);
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(List.of(scope));
    InspectitConfiguration configuration = new InspectitConfiguration(instrumentationConfiguration);

    ConfigurationHolder holder = ConfigurationHolder.getInstance();
    ConfigurationReceivedEvent event = new ConfigurationReceivedEvent(this, configuration);
    holder.handleConfiguration(event);
  }
}
