package org.apache.commons.net.tftp;
import org.apache.commons.io.FileUtils;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.apache.commons.net.tftp.TFTPServer.ServerMode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.AfterEach;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Basic tests to ensure that the TFTP Server is honoring its read/write mode, and preventing files from being read or written from outside of the assigned
 * roots.
 */
public class TFTPServerPathTest {
  private static final int SERVER_PORT = 6901;

  private static final String FILE_PREFIX = "TFTPServerPathTest_";

  private static final String FILE_TO_READ_NAME = FILE_PREFIX + "source.txt";

  private Path serverDirectory;

  private static final String FILE_TO_WRITE_NAME = FILE_PREFIX + "out";

  private static Path createFileInDir(final Path directory, final String fileName) throws IOException {
    final Path filePath = directory.resolve(fileName);
    if (Files.exists(filePath)) {
      Files.delete(filePath);
    }
    return Files.createFile(filePath);
  }

  private static void deleteFile(final Path path) throws IOException {
    if (path != null) {
      Files.deleteIfExists(path);
    }
  }

  private TFTPServer tftpServer;

  private TFTPClient tftpClient;

  private Path fileToRead;

  private Path fileToWrite;

  @BeforeEach public void beforeEach() throws IOException {
    serverDirectory = FileUtils.getTempDirectory().toPath();
    fileToRead = createFileInDir(serverDirectory, FILE_TO_READ_NAME);
    fileToWrite = createFileInDir(serverDirectory, FILE_TO_WRITE_NAME);
  }

  @AfterEach public void afterEach() throws IOException {
    if (tftpClient != null) {
      tftpClient.close();
    }
    if (tftpServer != null) {
      tftpServer.close();
    }
    deleteFile(fileToRead);
    deleteFile(fileToWrite);
  }

  private TFTPServer startTftpServer(final ServerMode serverMode) throws IOException {
    return new TFTPServer(serverDirectory.toFile(), serverDirectory.toFile(), SERVER_PORT, serverMode, null, null);
  }

  @Test public void testReadOnly() throws IOException {
    tftpServer = startTftpServer(ServerMode.GET_ONLY);
    final String serverAddress = "localhost";
    final int serverPort = tftpServer.getPort();
    Files.write(fileToRead, "The quick brown fox jumps over the lazy dog".getBytes(StandardCharsets.UTF_8));
    final long fileToReadContentLength = Files.size(fileToRead);
    tftpClient = new TFTPClient();
    tftpClient.open();
    tftpClient.setSoTimeout(Duration.ofMillis(2000));
    try (OutputStream os = Files.newOutputStream(fileToWrite)) {
      final String writeFileName = fileToRead.getFileName().toString();
      final int bytesRead = tftpClient.receiveFile(writeFileName, TFTP.BINARY_MODE, os, serverAddress, serverPort);
      assertEquals(fileToReadContentLength, bytesRead);
    }
    try (InputStream is = Files.newInputStream(fileToRead)) {

<<<<<<< /usr/src/app/output/apache/commons-net/e3013d784b0b6c93a7624a36870f2dd73e6ecc41/src/test/java/org/apache/commons/net/tftp/TFTPServerPathTest.java/left.java
      final String readFileName = fileToRead.getFileName().toString();
=======
      try (TFTPClient tftp = new TFTPClient()) {
        tftp.open();
        tftp.setSoTimeout(2000);
        try {
          assertFalse(out.exists(), () -> "Couldn\'t clear output location, deleted=");
          try (FileOutputStream output = new FileOutputStream(out)) {
            tftp.receiveFile(file.getName(), TFTP.BINARY_MODE, output, "localhost", SERVER_PORT);
          }
          assertTrue(out.exists(), "file not created");
          out.delete();
          assertThrows(IOException.class, () -> {
            try (FileInputStream fis = new FileInputStream(file)) {
              tftp.sendFile(out.getName(), TFTP.BINARY_MODE, fis, "localhost", SERVER_PORT);
              fail("Server allowed write");
            }
          });
        }  finally {
          deleteFixture(file);
          deleteFixture(out);
        }
      }
>>>>>>> /usr/src/app/output/apache/commons-net/e3013d784b0b6c93a7624a36870f2dd73e6ecc41/src/test/java/org/apache/commons/net/tftp/TFTPServerPathTest.java/right.java

      final IOException exception = assertThrows(IOException.class, () -> tftpClient.sendFile(readFileName, TFTP.BINARY_MODE, is, serverAddress, serverPort));
      assertEquals("Error code 4 received: Write not allowed by server.", exception.getMessage());
    }
  }

  @Test public void testWriteOnly() throws IOException {
    tftpServer = startTftpServer(ServerMode.PUT_ONLY);
    final String serverAddress = "localhost";
    final int serverPort = tftpServer.getPort();
    tftpClient = new TFTPClient();
    tftpClient.open();
    tftpClient.setSoTimeout(Duration.ofMillis(2000));
    try (OutputStream os = Files.newOutputStream(fileToWrite)) {
      final String readFileName = fileToRead.getFileName().toString();
      final IOException exception = assertThrows(IOException.class, () -> tftpClient.receiveFile(readFileName, TFTP.BINARY_MODE, os, serverAddress, serverPort));
      assertEquals("Error code 4 received: Read not allowed by server.", exception.getMessage());
    }
    try (InputStream is = Files.newInputStream(fileToRead)) {

<<<<<<< /usr/src/app/output/apache/commons-net/e3013d784b0b6c93a7624a36870f2dd73e6ecc41/src/test/java/org/apache/commons/net/tftp/TFTPServerPathTest.java/left.java
      deleteFile(fileToWrite);
=======
      try (TFTPClient tftp = new TFTPClient()) {
        tftp.open();
        tftp.setSoTimeout(2000);
        try {
          assertFalse(out.exists(), () -> "Couldn\'t clear output location, deleted=");
          assertThrows(IOException.class, () -> {
            try (FileOutputStream output = new FileOutputStream(out)) {
              tftp.receiveFile(file.getName(), TFTP.BINARY_MODE, output, "localhost", SERVER_PORT);
              fail("Server allowed read");
            }
          });
          out.delete();
          try (FileInputStream fis = new FileInputStream(file)) {
            tftp.sendFile(out.getName(), TFTP.BINARY_MODE, fis, "localhost", SERVER_PORT);
          }
          assertTrue(out.exists(), "file not created");
        }  finally {
          deleteFixture(file);
          deleteFixture(out);
        }
      }
>>>>>>> /usr/src/app/output/apache/commons-net/e3013d784b0b6c93a7624a36870f2dd73e6ecc41/src/test/java/org/apache/commons/net/tftp/TFTPServerPathTest.java/right.java

      final String writeFileName = fileToWrite.getFileName().toString();
      tftpClient.sendFile(writeFileName, TFTP.BINARY_MODE, is, serverAddress, serverPort);
    }
  }

  @Test public void testWriteVerifyContents() throws IOException {
    tftpServer = startTftpServer(ServerMode.GET_AND_PUT);
    final String serverAddress = "localhost";
    final int serverPort = tftpServer.getPort();
    tftpClient = new TFTPClient();
    tftpClient.open();
    tftpClient.setSoTimeout(Duration.ofMillis(2000));
    final byte[] content = "TFTP write test!".getBytes(StandardCharsets.UTF_8);
    Files.write(fileToRead, content);
    try (InputStream is = Files.newInputStream(fileToRead)) {
      deleteFile(fileToWrite);
      final String writeFileName = fileToWrite.getFileName().toString();
      tftpClient.sendFile(writeFileName, TFTP.BINARY_MODE, is, serverAddress, serverPort);
    }
    try (OutputStream os = Files.newOutputStream(fileToWrite)) {
      final String readFileName = fileToRead.getFileName().toString();
      final int readBytes = tftpClient.receiveFile(readFileName, TFTP.BINARY_MODE, os, serverAddress, serverPort);
      assertEquals(content.length, readBytes);
    }
  }

  @Test public void testWriteOutsideHome() throws IOException {
    tftpServer = startTftpServer(ServerMode.GET_AND_PUT);
    final String serverAddress = "localhost";
    final int serverPort = tftpServer.getPort();
    tftpClient = new TFTPClient();
    tftpClient.open();
    try (InputStream is = Files.newInputStream(fileToRead)) {

<<<<<<< /usr/src/app/output/apache/commons-net/e3013d784b0b6c93a7624a36870f2dd73e6ecc41/src/test/java/org/apache/commons/net/tftp/TFTPServerPathTest.java/left.java
      final IOException exception = assertThrows(IOException.class, () -> tftpClient.sendFile("../foo", TFTP.BINARY_MODE, is, serverAddress, serverPort));
=======
      try (TFTPClient tftp = new TFTPClient()) {
        tftp.open();
        try {
          assertFalse(new File(serverDirectory, "../foo").exists(), "test construction error");
          assertThrows(IOException.class, () -> {
            try (FileInputStream fis = new FileInputStream(file)) {
              tftp.sendFile("../foo", TFTP.BINARY_MODE, fis, "localhost", SERVER_PORT);
              fail("Server allowed write!");
            }
          });
          assertFalse(new File(serverDirectory, "../foo").exists(), "file created when it should not have been");
        }  finally {
          deleteFixture(file);
        }
      }
>>>>>>> /usr/src/app/output/apache/commons-net/e3013d784b0b6c93a7624a36870f2dd73e6ecc41/src/test/java/org/apache/commons/net/tftp/TFTPServerPathTest.java/right.java

      assertEquals("Error code 0 received: Cannot access files outside of TFTP server root.", exception.getMessage());
      assertFalse(Files.exists(serverDirectory.resolve("foo")), "file created when it should not have been");
    }
  }
}