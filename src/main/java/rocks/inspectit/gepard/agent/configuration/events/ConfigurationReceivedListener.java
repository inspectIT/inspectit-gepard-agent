package rocks.inspectit.gepard.agent.configuration.events;

import java.util.EventListener;

public interface ConfigurationReceivedListener extends EventListener {
  void handleConfiguration(ConfigurationReceivedEvent event);
}
