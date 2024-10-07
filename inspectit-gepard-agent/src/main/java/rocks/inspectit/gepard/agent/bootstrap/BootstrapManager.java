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
 * This manager should append our bootstrap classes to the bootstrap classloader, so they are
 * accessible globally in the target application as well as this agent.
 */
public class BootstrapManager {
  private static final Logger log = LoggerFactory.getLogger(BootstrapManager.class);

  private static final String INSPECTIT_BOOTSTRAP_JAR_PATH = "/inspectit-gepard-bootstrap.jar";

  private static final String INSPECTIT_BOOTSTRAP_JAR_TEMP_PREFIX = "gepard-bootstrap-";

  private BootstrapManager() {}

  /**
   * Factory method to create an {@link BootstrapManager}
   *
   * @return the created manager
   */
  public static BootstrapManager create() {
    return new BootstrapManager();
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
   * @return the copied jar file
   */
  @VisibleForTesting
  JarFile copyJarFile(String resourcePath, String prefix) throws IOException {
    try (InputStream inputStream = BootstrapManager.class.getResourceAsStream(resourcePath)) {

      File targetFile = prepareTempFile(prefix);
      Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

      return new JarFile(targetFile);
    }
  }

  /**
   * Creates and set-ups a new temporary file with the ending ".jar"
   *
   * @param prefix the name of the new temporary file
   * @return the created temporary file
   */
  private File prepareTempFile(String prefix) throws IOException {
    Path tempDir = Files.createTempDirectory("inspectit-gepard-");
    Path targetPath = Files.createTempFile(tempDir, prefix, ".jar");
    File targetFile = targetPath.toFile();

    targetFile.setReadable(true, true);
    targetFile.setWritable(true, true);
    targetFile.setExecutable(true, true);
    targetFile.deleteOnExit();

    return targetFile;
  }
}
