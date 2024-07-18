package rocks.inspectit.gepard.agent.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.events.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.configuration.events.ConfigurationReceivedListener;
import rocks.inspectit.gepard.agent.configuration.model.InstrumentationRequest;

public class ConfigurationResolver implements ConfigurationReceivedListener {

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationResolver.class);
  private InstrumentationRequest instrumentationRequest;

  @Override
  public void handleConfiguration(ConfigurationReceivedEvent event) {
    logger.info("Received new configuration");
    instrumentationRequest = event.getInstrumentationConfiguration();
  }

  /**
   * @param clazz The original, uninstrumented Class object
   * @return
   */
  public boolean shouldRetransform(Class<?> clazz) {
    return instrumentationRequest.getScopes().stream()
        .anyMatch(scope -> scope.getFqn().equals(clazz.getName()));
  }

  /**
   * @param clazz The original, uninstrumented Class object
   * @return
   */
  public boolean shouldInstrument(Class<?> clazz) {
    return instrumentationRequest.getScopes().stream()
        .anyMatch(scope -> scope.getFqn().equals(clazz.getName()) && scope.isEnabled());
  }
}
