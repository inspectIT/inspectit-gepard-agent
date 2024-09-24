/* (C) 2024 */
package rocks.inspectit.gepard.agent.bootstrap;

import io.opentelemetry.javaagent.bootstrap.InstrumentationHolder;
import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InspectitBootstrapManager {

  private static final Logger log = LoggerFactory.getLogger(InspectitBootstrapManager.class);

  // private static final String INSPECTIT_BOOTSTRAP_JAR_PATH =
  // "/extensions/inspectit-gepard-bootstrap.jar";
  private static final String INSPECTIT_BOOTSTRAP_JAR_PATH = "/inspectit-gepard-bootstrap.jar";

  private static final String INSPECTIT_BOOTSTRAP_JAR_TEMP_PREFIX = "gepard-bootstrap-";

  private InspectitBootstrapManager() {}

  public static synchronized void appendToBootstrapClassLoader() {
    Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();
    try {
      Path bootstrapJarPath =
          copyResourceToTempJarFile(
              INSPECTIT_BOOTSTRAP_JAR_PATH, INSPECTIT_BOOTSTRAP_JAR_TEMP_PREFIX);
      JarFile bootstrapJar = new JarFile(bootstrapJarPath.toFile());
      instrumentation.appendToBootstrapClassLoaderSearch(bootstrapJar);
    } catch (Exception e) {
      log.error("Could not add inspectIT Gepard interfaces to bootstrap classloader", e);
      return;
    }
    log.info("Successfully added inspectIT Gepard interfaces to bootstrap classloader");
  }

  /**
   * Copies the given resource to a new temporary file with the ending ".jar"
   *
   * @param resourcePath the path to the resource
   * @param prefix the name of the new temporary file
   * @return the path to the generated jar file
   */
  private static Path copyResourceToTempJarFile(String resourcePath, String prefix)
      throws IOException {
    // try (InputStream inputStream = OpenTelemetryAgent.class.getResourceAsStream(resourcePath)) {
    try (InputStream inputStream =
        InspectitBootstrapManager.class.getResourceAsStream(resourcePath)) {
      Path targetFile = Files.createTempFile(prefix, ".jar");
      Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
      targetFile.toFile().deleteOnExit();
      return targetFile;
    }
  }
}
