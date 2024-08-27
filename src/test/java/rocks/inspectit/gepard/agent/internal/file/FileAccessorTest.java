package rocks.inspectit.gepard.agent.internal.file;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import org.junit.jupiter.api.*;

class FileAccessorTest {

  private static final String CONTENT = "{\"key\": \"value\"}";

  private Path tempFilePath;

  private FileAccessor fileAccessor;

  @BeforeEach
  void setUp() throws IOException {
    tempFilePath = Files.createTempFile("test-config", ".json");
    tempFilePath.toFile().deleteOnExit();

    fileAccessor = FileAccessor.create(tempFilePath);
  }

  @AfterEach
  void tearDown() throws IOException {
    tempFilePath.toFile().setWritable(true);
    Files.deleteIfExists(tempFilePath);
  }

  @Test
  void readsContentFromFile() throws IOException {
    Files.write(tempFilePath, CONTENT.getBytes());

    String actualContent = fileAccessor.readFile();

    assertEquals(CONTENT, actualContent);
  }

  @Test
  void deletedFileThrowsException() {
    tempFilePath.toFile().delete();

    assertThrows(FileNotFoundException.class, fileAccessor::readFile);
  }

  @Test
  void writesContentIntoFile() throws IOException {
    fileAccessor.writeFile(CONTENT);

    String actualContent = Files.readString(tempFilePath);
    assertEquals(CONTENT, actualContent);
  }

  @Test
  void writeFileCreatesFileIfNotExisting() throws IOException {
    Files.deleteIfExists(tempFilePath);

    fileAccessor.writeFile(CONTENT);

    assertTrue(Files.exists(tempFilePath));
    String actualContent = Files.readString(tempFilePath);
    assertEquals(CONTENT, actualContent);
  }

  @Test
  void notWritableFileThrowsException() {
    tempFilePath.toFile().setWritable(false);

    assertThrows(AccessDeniedException.class, () -> fileAccessor.writeFile(CONTENT));
  }
}
