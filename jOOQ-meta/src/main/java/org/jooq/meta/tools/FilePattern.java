package org.jooq.meta.tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;
import org.jooq.Internal;
import org.jooq.tools.JooqLogger;

/**
 * A utility class that can traverse a directory structure given some ant-style
 * file patterns.
 * <p>
 * This is INTERNAL API. Please do not use directly as API may change
 * incompatibly.
 *
 * @author Lukas Eder
 */
@Internal public final class FilePattern {
  private static final JooqLogger log = JooqLogger.getLogger(FilePattern.class);

  public static final Comparator<File> fileComparator(String sort) {
    if ("alphanumeric".equals(sort)) {
      return new Comparator<File>() {
        @Override public int compare(File o1, File o2) {
          return o1.compareTo(o2);
        }
      };
    } else {
      if ("none".equals(sort)) {
        return null;
      } else {
        if ("flyway".equals(sort)) {
          return FlywayFileComparator.INSTANCE;
        } else {
          return FileComparator.INSTANCE;
        }
      }
    }
  }

  /**
     * Try loading <code>pattern</code>.
     * <p>
     * This method tries loading contents from <code>pattern</code> using the
     * following algorithm:
     * <p>
     * <ul>
     * <li>If <code>pattern</code> is a valid classpath resource, load it from
     * there</li>
     * <li>If <code>pattern</code> is a valid file on the file system, load it
     * from there</li>
     * <li>Match all files on the file system according to <code>pattern</code>
     * (interpreted as an ant-style file pattern), and load them</li>
     * </ul>
     */
  public static final void load(String encoding, String pattern, Comparator<File> fileComparator, Loader loader) throws Exception {
    InputStream in = null;
    boolean loaded = false;
    try {
      in = FilePattern.class.getResourceAsStream(pattern);
      if (in != null) {
        log.info("Reading from classpath: " + pattern);
        loader.load(encoding, in);
        loaded = true;
      } else {
        File file = new File(pattern);
        if (file.exists()) {
          load(encoding, file, fileComparator, null, loader);
          loaded = true;
        } else {
          if (pattern.contains("*") || pattern.contains("?")) {
            file = new File(pattern.replaceAll("[*?].*", "")).getCanonicalFile();
          } else {
            file = new File(".").getCanonicalFile();
          }
          Pattern regex = Pattern.compile("^.*?" + pattern.replace("\\", "/").replace(".", "\\.").replace("?", ".").replace("**", ".+?").replace("*", "[^/]*") + "$");
          load(encoding, file, fileComparator, regex, loader);
          loaded = true;
        }
      }
      if (!loaded) {
        log.error("Could not find source(s) : " + pattern);
      }
    }  finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (Exception ignore) {
      }
    }
  }

  private static final void load(String encoding, File file, Comparator<File> fileComparator, Pattern pattern, Loader loader) throws Exception {
    if (file.isFile()) {
      if (pattern == null || pattern.matcher(file.getCanonicalPath().replace("\\", "/")).matches()) {
        log.info("Reading from: " + file + " [*]");
        loader.load(encoding, new FileInputStream(file));
      }
    } else {
      if (file.isDirectory()) {
        log.info("Reading from: " + file);
        File[] files = file.listFiles();
        if (files != null) {
          if (fileComparator != null) {
            Arrays.sort(files, fileComparator);
          }
          for (File f : files) {
            load(encoding, f, fileComparator, pattern, loader);
          }
        }
      } else {
        if (!file.exists() && file.getParentFile() != null) {
          load(encoding, file.getParentFile(), fileComparator, pattern, loader);
        }
      }
    }
  }

  public interface Loader {
    void load(String encoding, InputStream in) throws Exception;
  }
}