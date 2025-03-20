package org.apache.commons.net.examples.util;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.net.io.Util;
import org.apache.commons.net.util.NetConstants;

/**
 * This is a utility class providing a reader/writer capability required
 * by the weatherTelnet, rexec, rshell, and rlogin example programs.
 * The only point of the class is to hold the static method readWrite
 * which spawns a reader thread and a writer thread.  The reader thread
 * reads from a local input source (presumably stdin) and writes the
 * data to a remote output destination.  The writer thread reads from
 * a remote input source and writes to a local output destination.
 * The threads terminate when the remote input source closes.
 */
public final class IOUtil {
  public static void readWrite(final InputStream remoteInput, final OutputStream remoteOutput, final InputStream localInput, final OutputStream localOutput) {
    final Thread reader;
    final Thread writer;
    reader = new Thread(() -> {
      int ch;
      try {
        while (!Thread.interrupted() && (ch = localInput.read()) != -1) {
          remoteOutput.write(ch);
          remoteOutput.flush();
        }
      } catch (final IOException e) {
      }
    }) {
      @Override public void run() {
        int ch;
        try {
          while (!interrupted() && (ch = localInput.read()) != NetConstants.EOS) {
            remoteOutput.write(ch);
            remoteOutput.flush();
          }
        } catch (final IOException e) {
        }
      }
    };
    writer = new Thread(() -> {
      try {
        Util.copyStream(remoteInput, localOutput);
      } catch (final IOException e) {
        e.printStackTrace();
        System.exit(1);
      }
    });
    writer.setPriority(Thread.currentThread().getPriority() + 1);
    writer.start();
    reader.setDaemon(true);
    reader.start();
    try {
      writer.join();
      reader.interrupt();
    } catch (final InterruptedException e) {
    }
  }
}