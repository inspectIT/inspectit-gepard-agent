/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.configuration.ConfigurationManager;

public class FileAccessor {
  private static final Logger log = LoggerFactory.getLogger(FileAccessor.class);

  private final Path filePath;

  private final Lock readLock;

  private final Lock writeLock;

  private FileAccessor(Path filePath, ReadWriteLock readWriteLock) {
    this.filePath = filePath;
    this.readLock = readWriteLock.readLock();
    this.writeLock = readWriteLock.writeLock();
  }

  /**
   * Factory method to create a {@link ConfigurationManager}
   *
   * @param filePath the path of the accessible file
   * @return the created accessor
   */
  public static FileAccessor create(Path filePath) {
    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    return new FileAccessor(filePath, readWriteLock);
  }

  /**
   * Tries to read data from a file.
   *
   * @return the content of the file
   */
  public String readFile() throws IOException {
    readLock.lock();
    try {
      if (Files.notExists(filePath))
        throw new FileNotFoundException("Configuration file not found: " + filePath);

      if (!Files.isReadable(filePath))
        throw new AccessDeniedException("Configuration file is not readable: " + filePath);

      byte[] rawFileContent = Files.readAllBytes(filePath);
      return new String(rawFileContent);
    } finally {
      readLock.unlock();
    }
  }

  /**
   * Tries to write data into a file.
   *
   * @param content the data, which should be written into the file
   */
  public void writeFile(String content) throws IOException {
    writeLock.lock();
    try {
      if (Files.notExists(filePath)) {
        log.info("Creating local configuration file at {}", filePath);
        Files.createDirectories(filePath.getParent());
        Files.createFile(filePath);
      }

      if (!Files.isWritable(filePath))
        throw new AccessDeniedException("Configuration file is not writable: " + filePath);

      Files.write(filePath, content.getBytes());
    } finally {
      writeLock.unlock();
    }
  }
}
