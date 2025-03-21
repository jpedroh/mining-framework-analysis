package org.gedcom4j.io.reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.parser.GedcomParser;

/**
 * A reader that loads from an input stream and gives back a collection of strings representing the data therein. This
 * implementation handles UTF-8 data
 * 
 * @author frizbog
 */
final class Utf8Reader extends AbstractEncodingSpecificReader {
  /**
     * Was a byte order marker read when inspecting the file to detect encoding?
     */
  private boolean byteOrderMarkerRead = false;

  /**
     * Input stream reader for internal use over the byte stream
     */
  private final InputStreamReader inputStreamReader;

  /**
     * Buffered reader over the input stream reader, for internal use
     */
  private final BufferedReader bufferedReader;

  /**
     * The progress tracking input stream
     */
  private ProgressTrackingInputStream inputStream;

  /**
     * Constructor
     * 
     * @param parser
     *            the {@link GedcomParser} which is using this object to read files
     * 
     * @param byteStream
     *            the stream of data to read
     * @throws IOException
     *             if there's a problem reading the data
     */
  Utf8Reader(GedcomParser parser, InputStream byteStream) throws IOException {
    super(parser, byteStream);
    try {
      inputStream = new ProgressTrackingInputStream(byteStream);
      inputStreamReader = new InputStreamReader(inputStream, "UTF8");
      if (byteOrderMarkerRead) {
        inputStreamReader.read();
        bytesRead = inputStream.getBytesRead();
      }
      bufferedReader = new BufferedReader(inputStreamReader);
    } catch (IOException e) {
      closeReaders();
      throw e;
    }
  }

  /**
     * {@inheritDoc}
     */
  @Override public String nextLine() throws IOException, GedcomParserException {
    String result = null;
    String s = bufferedReader.readLine();
    bytesRead = inputStream.getBytesRead();
    if (s != null && s.length() > 0 && s.charAt(0) == (char) 0xFEFF) {
      s = s.substring(1);
    }
    while (s != null) {
      s = leftTrim(s);
      if (s.length() != 0) {
        result = s;
        break;
      }
      s = bufferedReader.readLine();
      bytesRead = inputStream.getBytesRead();
    }
    bytesRead = inputStream.getBytesRead();
    return result;
  }

  /**
     * Set whether or not a byte order marker was read when inspecting the file to detect its encoding
     * 
     * @param wasByteOrderMarkerRead
     *            true if a byte order marker was read
     */
  public void setByteOrderMarkerRead(boolean wasByteOrderMarkerRead) {
    byteOrderMarkerRead = wasByteOrderMarkerRead;
  }

  /**
     * {@inheritDoc}
     */
  @Override void cleanUp() throws IOException {
    closeReaders();
  }

  /**
     * Close the readers created in this class. Private so it can't be overridden (like {@link #cleanUp()} can be), which makes it
     * safe to call from the constructor.
     * 
     * @throws IOException
     *             if the readers can't be closed
     */
  private void closeReaders() throws IOException {
    if (bufferedReader != null) {
      bufferedReader.close();
    }
    if (inputStreamReader != null) {
      inputStreamReader.close();
    }
  }

  /**
     * Trim all whitespace off the left side (only) of the supplied string.
     * 
     * @param line
     *            the string to trim left leading whitespace from
     * @return the line passed in with the leading whitespace removed. If the original string passed in was null, null is returned
     *         here.
     */
  private String leftTrim(String line) {
    if (line == null) {
      return null;
    }
    if (line.length() == 0) {
      return "";
    }
    if (!Character.isWhitespace(line.charAt(0))) {
      return line;
    }
    for (int i = 0; i < line.length(); i++) {
      if (!Character.isWhitespace(line.charAt(i))) {
        return line.substring(i);
      }
    }
    return "";
  }
}