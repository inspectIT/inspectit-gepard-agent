package rocks.inspectit.gepard.agent.config;

import com.google.common.eventbus.EventBus;
import rocks.inspectit.gepard.agent.config.http.HttpConfigurationManager;

public class ConfigurationManagerFactory {
  public static ConfigurationManager create(ConfigurationSource source, EventBus eventBus) {
    ConfigurationManager manager = createConfigurationManager(source, eventBus);
    manager.manageConfiguration();
    return manager;
  }

  protected static ConfigurationManager createConfigurationManager(ConfigurationSource source, EventBus eventBus) {
    return switch (source) {
      case HTTP -> new HttpConfigurationManager(eventBus);
      case FILE ->
          throw new UnsupportedOperationException(
              "File configuration source is not yet implemented");
      case WHATEVER ->
          throw new UnsupportedOperationException(
              "Whatever configuration source is not yet implemented");
    };
  }
  ;
}
