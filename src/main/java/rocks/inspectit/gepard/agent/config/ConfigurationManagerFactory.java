package rocks.inspectit.gepard.agent.config;

import com.google.common.eventbus.EventBus;
import rocks.inspectit.gepard.agent.config.http.HttpConfigurationManager;

public class ConfigurationManagerFactory {
  public static ConfigurationManager create(ConfigurationSource source) {
    ConfigurationManager manager = createConfigurationManager(source);
    manager.manageConfiguration();
    return manager;
  }

  protected static ConfigurationManager createConfigurationManager(ConfigurationSource source) {
    return switch (source) {
      case HTTP -> new HttpConfigurationManager();
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
