package org.jboss.remoting3;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * The version of Remoting.
 *
 * @apiviz.exclude
 */
@SuppressWarnings(value = { "deprecation" }) public final class Version {
  private Version() {
  }

  private static final String JAR_NAME;

  /**
     * The version string.
     *
     * @deprecated Use {@link #getVersionString()} instead.
     */
  @Deprecated public static final String VERSION;

  static {
    Properties versionProps = new Properties();
    String jarName = "(unknown)";
    String versionString = "(unknown)";

<<<<<<< /usr/src/app/output/jboss-remoting/jboss-remoting/dd7f776d66ed721f4e2f6237137d69faa3d1b703/src/main/java/org/jboss/remoting3/Version.java/left.java
    try (InputStream stream = Version.class.getResourceAsStream("Version.properties")) {
      try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
        versionProps.load(reader);
        jarName = versionProps.getProperty("jarName", jarName);
        versionString = versionProps.getProperty("version", versionString);
      }
    } catch (IOException ignored) {
    }
=======
    try {
      final InputStream stream = Version.class.getResourceAsStream("Version.properties");
      try {
        final InputStreamReader reader = new InputStreamReader(stream);
        try {
          versionProps.load(reader);
          jarName = versionProps.getProperty("jarName", jarName);
          versionString = versionProps.getProperty("version", versionString);
        }  finally {
          safeClose(reader);
        }
      }  finally {
        safeClose(stream);
      }
    } catch (IOException ignored) {
    }
>>>>>>> /usr/src/app/output/jboss-remoting/jboss-remoting/dd7f776d66ed721f4e2f6237137d69faa3d1b703/src/main/java/org/jboss/remoting3/Version.java/right.java

    JAR_NAME = jarName;
    VERSION = versionString;
  }

  /**
     * Get the name of the JBoss Remoting JAR.
     *
     * @return the name
     */
  public static String getJarName() {
    return JAR_NAME;
  }

  /**
     * Get the version string of JBoss Remoting.
     *
     * @return the version string
     */
  public static String getVersionString() {
    return VERSION;
  }

  /**
     * Print out the current version on {@code System.out}.
     *
     * @param args ignored
     */
  public static void main(String[] args) {
    System.out.printf("JBoss Remoting version %s\n", getVersionString());
  }
}