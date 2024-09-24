/* (C) 2024 */
package rocks.inspectit.gepard.agent.bootstrap;

import com.google.common.annotations.VisibleForTesting;
import io.opentelemetry.javaagent.bootstrap.InstrumentationHolder;
import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This manager should append our bootstrap classes to the bootstrap classloader, so there are
 * accessible globally in the target application as well as this agent.
 */
public class InspectitBootstrapManager {
  private static final Logger log = LoggerFactory.getLogger(InspectitBootstrapManager.class);

  private static final String INSPECTIT_BOOTSTRAP_JAR_PATH = "/inspectit-gepard-bootstrap.jar";

  private static final String INSPECTIT_BOOTSTRAP_JAR_TEMP_PREFIX = "gepard-bootstrap-";

  private InspectitBootstrapManager() {}

  /**
   * Factory method to create an {@link InspectitBootstrapManager}
   *
   * @return the created manager
   */
  public static InspectitBootstrapManager create() {
    return new InspectitBootstrapManager();
  }

  /** Appends our inspectit-gepard-bootstrap.jar to the bootstrap-classloader */
  public synchronized void appendToBootstrapClassLoader() {
    Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();
    try {
      JarFile bootstrapJar =
          copyJarFile(INSPECTIT_BOOTSTRAP_JAR_PATH, INSPECTIT_BOOTSTRAP_JAR_TEMP_PREFIX);
      instrumentation.appendToBootstrapClassLoaderSearch(bootstrapJar);
    } catch (Exception e) {
      log.error("Could not append inspectIT Gepard interfaces to bootstrap classloader", e);
      return;
    }
    log.info("Successfully appended inspectIT Gepard interfaces to bootstrap classloader");
  }

  /**
   * Copies the given resource to a new temporary jar file
   *
   * @param resourcePath the path to the resource
   * @param prefix the name of the new temporary file
   * @return the path to the generated jar file
   */
  @VisibleForTesting
  JarFile copyJarFile(String resourcePath, String prefix) throws IOException {
    try (InputStream inputStream =
        InspectitBootstrapManager.class.getResourceAsStream(resourcePath)) {
      Path targetPath = Files.createTempFile(prefix, ".jar");
      Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
      File targetFile = targetPath.toFile();
      targetFile.deleteOnExit();

      return new JarFile(targetFile);
    }
  }
}
