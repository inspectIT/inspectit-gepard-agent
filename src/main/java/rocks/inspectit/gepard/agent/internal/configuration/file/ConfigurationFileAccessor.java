package rocks.inspectit.gepard.agent.internal.configuration.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConfigurationFileAccessor {

  private static ConfigurationFileAccessor instance;

  private final Lock readLock;

  private final Lock writeLock;

  private ConfigurationFileAccessor(ReadWriteLock readWriteLock) {
    this.readLock = readWriteLock.readLock();
    this.writeLock = readWriteLock.writeLock();
  }

  public static ConfigurationFileAccessor getInstance() {
    if (Objects.isNull(instance)) {
      ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
      instance = new ConfigurationFileAccessor(readWriteLock);
    }
    return instance;
  }

  public byte[] readFile(Path path) throws IOException {
    readLock.lock();
    try {
      if (!Files.exists(path))
        throw new FileNotFoundException("Configuration file not found: " + path);

      if (!Files.isReadable(path))
        throw new AccessDeniedException("Configuration file is not readable: " + path);

      return Files.readAllBytes(path);
    } finally {
      readLock.unlock();
    }
  }

  public void writeFile(Path path, String content) throws IOException {
    writeLock.lock();
    try {
      if (!Files.exists(path))
        throw new FileNotFoundException("Configuration file not found: " + path);

      if (!Files.isWritable(path))
        throw new AccessDeniedException("Configuration file is not writable: " + path);

      Files.write(path, content.getBytes());
    } finally {
      writeLock.unlock();
    }
  }
}
