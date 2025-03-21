package org.fusesource.jansi;
import static org.fusesource.jansi.internal.CLibrary.STDERR_FILENO;
import static org.fusesource.jansi.internal.CLibrary.STDOUT_FILENO;
import static org.fusesource.jansi.internal.CLibrary.isatty;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Provides consistent access to an ANSI aware console PrintStream.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @since 1.0
 */
public class AnsiConsole {
  public static final PrintStream system_out = System.out;

  public static final PrintStream out = new PrintStream(wrapOutputStream(system_out));

  public static final PrintStream system_err = System.err;

  public static final PrintStream err = new PrintStream(wrapErrorOutputStream(system_err));

  private static int installed;

  private AnsiConsole() {
  }

  public static OutputStream wrapOutputStream(final OutputStream stream) {
    try {
      return wrapOutputStream(stream, STDOUT_FILENO);
    } catch (Throwable ignore) {
      return wrapOutputStream(stream, 0);
    }
  }

  public static OutputStream wrapErrorOutputStream(final OutputStream stream) {
    try {
      return wrapOutputStream(stream, STDERR_FILENO);
    } catch (Throwable ignore) {
      return wrapOutputStream(stream, 0);
    }
  }

  public static OutputStream wrapOutputStream(final OutputStream stream, int fileno) {
    if (Boolean.getBoolean("jansi.passthrough")) {
      return stream;
    }
    if (Boolean.getBoolean("jansi.strip")) {
      return new AnsiOutputStream(stream);
    }
    String os = System.getProperty("os.name");
    if (os.startsWith("Windows") && !isXterm()) {
      try {
        return new WindowsAnsiOutputStream(stream);
      } catch (Throwable ignore) {
      }
      return new AnsiOutputStream(stream);
    }
    try {
      boolean forceColored = Boolean.getBoolean("jansi.force");
      if (!isXterm() && !forceColored && isatty(fileno) == 0) {
        return new AnsiOutputStream(stream);
      }
    } catch (Throwable ignore) {
    }
    return new FilterOutputStream(stream) {
      @Override public void close() throws IOException {
        write(AnsiOutputStream.REST_CODE);
        flush();
        super.close();
      }
    };
  }

  private static boolean isXterm() {
    String term = System.getenv("TERM");
    return term != null && term.startsWith("xterm");
  }

  /**
     * If the standard out natively supports ANSI escape codes, then this just
     * returns System.out, otherwise it will provide an ANSI aware PrintStream
     * which strips out the ANSI escape sequences or which implement the escape
     * sequences.
     *
     * @return a PrintStream which is ANSI aware.
     */
  public static PrintStream out() {
    return out;
  }

  /**
     * If the standard out natively supports ANSI escape codes, then this just
     * returns System.err, otherwise it will provide an ANSI aware PrintStream
     * which strips out the ANSI escape sequences or which implement the escape
     * sequences.
     *
     * @return a PrintStream which is ANSI aware.
     */
  public static PrintStream err() {
    return err;
  }

  /**
     * Install Console.out to System.out.
     */
  synchronized static public void systemInstall() {
    installed++;
    if (installed == 1) {
      System.setOut(out);
      System.setErr(err);
    }
  }

  /**
     * undo a previous {@link #systemInstall()}.  If {@link #systemInstall()} was called
     * multiple times, it {@link #systemUninstall()} must call the same number of times before
     * it is actually uninstalled.
     */
  synchronized public static void systemUninstall() {
    installed--;
    if (installed == 0) {
      System.setOut(system_out);
      System.setErr(system_err);
    }
  }
}