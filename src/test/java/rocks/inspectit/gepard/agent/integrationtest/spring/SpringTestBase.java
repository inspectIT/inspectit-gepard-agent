package rocks.inspectit.gepard.agent.integrationtest.spring;

import java.time.Duration;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import rocks.inspectit.gepard.agent.integrationtest.IntegrationTestBase;

/** Base class for Spring integration tests. */
public abstract class SpringTestBase extends IntegrationTestBase {

  @Override
  protected String getTargetImage(int jdk) {
    return "ghcr.io/open-telemetry/opentelemetry-java-instrumentation/smoke-test-spring-boot:jdk"
        + jdk
        + "-20211213.1570880324";
  }

  @Override
  protected WaitStrategy getTargetWaitStrategy() {
    return Wait.forLogMessage(".*Started SpringbootApplication in.*", 1)
        .withStartupTimeout(Duration.ofMinutes(1));
  }
}
