package org.apache.commons.net.io;
import org.apache.commons.net.util.NetConstants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * CRLFLineReader implements a readLine() method that requires
 * exactly CRLF to terminate an input line.
 * This is required for IMAP, which allows bare CR and LF.
 *
 * @since 3.0
 */
public final class CRLFLineReader extends BufferedReader {
  private static final char LF = '\n';

  private static final char CR = '\r';

  /**
     * Creates a CRLFLineReader that wraps an existing Reader
     * input source.
     * @param reader  The Reader input source.
     */
  public CRLFLineReader(final Reader reader) {
    super(reader);
  }

  /**
     * Read a line of text.
     * A line is considered to be terminated by carriage return followed immediately by a linefeed.
     * This contrasts with BufferedReader which also allows other combinations.
     * @since 3.0
     */
  @Override public String readLine() throws IOException {
    final StringBuilder sb = new StringBuilder();
    int intch;
    boolean prevWasCR = false;
    synchronized (lock) {
      while ((intch = read()) != NetConstants.EOS) {
        if (prevWasCR && intch == LF) {
          return sb.substring(0, sb.length() - 1);
        }
        prevWasCR = intch == CR;
        sb.append((char) intch);
      }
    }
    final String string = sb.toString();
    if (string.isEmpty()) {
      return null;
    }
    return string;
  }
}