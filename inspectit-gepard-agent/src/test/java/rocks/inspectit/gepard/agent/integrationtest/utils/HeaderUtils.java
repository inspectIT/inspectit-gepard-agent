/* (C) 2024 */
package rocks.inspectit.gepard.agent.integrationtest.utils;

import okhttp3.Headers;

public class HeaderUtils {
  public static Headers getConfigurationRequestHeaders() {
    return new Headers.Builder()
        .add("x-gepard-service-name", "test-service")
        .add("x-gepard-vm-id", "test-vm-id")
        .add("x-gepard-gepard-version", "test-gepard-version")
        .add("x-gepard-otel-version", "test-otel-version")
        .add("x-gepard-java-version", "test-java-version")
        .add("x-gepard-start-time", "test-start-time")
        .build();
  }
}
