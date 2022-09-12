package it.pagopa.io.sign.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtility {

  private FileUtility() {
    throw new IllegalStateException("You cannot initialize this class (FileUtility)");
  }

  public static String calculateSize(File file) {
    final int binaryConv = 1024;
    try {
      long bytes;
      bytes = Files.size(file.toPath());
      return String.format("%,d kilobytes", bytes / binaryConv);
    } catch (IOException e) {
      return "INVALID_SIZE";
    }
  }

  public static Path generateTempPath(String fileName) throws IOException {
    Path tempFolder = Files.createTempDirectory("io_sign_dss_");
    return Path.of(tempFolder.toString(), fileName);
  }
}
