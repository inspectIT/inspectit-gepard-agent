package rocks.inspectit.gepard.agent.instrumentation;

import com.google.common.eventbus.Subscribe;
import org.apache.hc.core5.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.config.ConfigurationUpdatedEvent;

public class ClassDiscoveryService implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(ClassDiscoveryService.class);

  @Override
  public void run() {
    // TODO
  }

  @Subscribe
  public void handleDataUpdate(ConfigurationUpdatedEvent event) {
    HttpResponse response = event.getResponse();
    // Process the response data
    log.info("Received configuration update: {}", response);
  }
}
