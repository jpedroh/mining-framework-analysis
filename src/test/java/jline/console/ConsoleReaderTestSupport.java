package jline.console;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import jline.TerminalSupport;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Provides support for console reader tests.
 */
public abstract class ConsoleReaderTestSupport {
  protected ConsoleReader console;

  protected ByteArrayOutputStream consoleOutputStream;

  @Before public void setUp() throws Exception {
    consoleOutputStream = new ByteArrayOutputStream();
    console = new ConsoleReader(null, consoleOutputStream, new TerminalSupport(true) { });
    console.setKeyMap(KeyMap.EMACS);
  }

  protected void assertConsoleOutputContains(Character c) {
    String output = consoleOutputStream.toString();
    assertTrue(output.contains(c.toString()));
  }

  protected void assertBeeped() {
    assertConsoleOutputContains(ConsoleReader.KEYBOARD_BELL);
  }

  protected void assertBuffer(final String expected, final Buffer buffer) throws IOException {
    assertBuffer(expected, buffer, true);
  }

  protected void assertBuffer(final String expected, final Buffer buffer, final boolean clear) throws IOException {
    if (clear) {
      console.finishBuffer();
      console.getHistory().clear();
    }
    console.setInput(new ByteArrayInputStream(buffer.getBytes()));
    String line;
    while ((line = console.readLine((String) null)) != null) {
    }
    assertEquals(expected, console.getCursorBuffer().toString());
  }

  protected void assertPosition(int pos, final Buffer buffer, final boolean clear) throws IOException {
    if (clear) {
      console.finishBuffer();
      console.getHistory().clear();
    }
    console.setInput(new ByteArrayInputStream(buffer.getBytes()));
    String line;
    while ((line = console.readLine((String) null)) != null) {
    }
    assertEquals(pos, console.getCursorPosition());
  }

  /**
     * This is used to check the contents of the last completed
     * line of input in the input buffer.
     * 
     * @param expected The expected contents of the line.
     * @param buffer The buffer
     * @param clear If true, the current buffer of the console
     *    is cleared.
     * @throws IOException
     */
  protected void assertLine(final String expected, final Buffer buffer, final boolean clear) throws IOException {
    if (clear) {
      console.finishBuffer();
      console.getHistory().clear();
    }
    console.setInput(new ByteArrayInputStream(buffer.getBytes()));
    String line;
    String prevLine = null;
    while ((line = console.readLine((String) null)) != null) {
      prevLine = line;
    }
    assertEquals(expected, prevLine);
  }

  private String getKeyForAction(final Operation key) {
    switch (key) {
      case BACKWARD_WORD:
      return "\u001bb";
      case BEGINNING_OF_LINE:
      return "\u001b[H";
      case END_OF_LINE:
      return "\u0005";
      case KILL_WORD:
      return "\u001bd";
      case UNIX_WORD_RUBOUT:
      return "\u0017";
      case ACCEPT_LINE:
      return "\n";
      case PREVIOUS_HISTORY:
      return "\u001b[A";
      case NEXT_HISTORY:
      return "\u001b[B";
      case BACKWARD_CHAR:
      return "\u0002";
      case COMPLETE:
      return "\t";
      case BACKWARD_DELETE_CHAR:
      return "\b";
      case VI_EOF_MAYBE:
      return "\u0004";
      case BACKWARD_KILL_WORD:
      return new String(new char[] { 27, 127 });
      case YANK:
      return "\u0019";
      case YANK_POP:
      return new String(new char[] { 27, 121 });
      case KILL_WORD:
      return new String(new char[] { 27, 100 });
    }
    throw new IllegalArgumentException(key.toString());
  }

  protected class Buffer {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    public Buffer() {
    }

    public Buffer(final String str) {
      append(str);
    }

    public byte[] getBytes() {
      return out.toByteArray();
    }

    public Buffer op(final Operation op) {
      return append(getKeyForAction(op));
    }

    public Buffer ctrlA() {
      return append("\u0001");
    }

    /**
         * Generate a CTRL-X sequence where 'X' is the control character
         * you wish to generate.
         * @param let The letter of the control character. Valid values are
         *   'A' through 'Z'.
         * @return The modified buffer.
         */
    public Buffer ctrl(char let) {
      if (let < 'A' || let > 'Z') {
        throw new RuntimeException("Cannot generate CTRL code for " + "char \'" + let + "\' (" + ((int) let) + ")");
      }
      int ch = (((int) let) - 'A') + 1;
      return append((char) ch);
    }

    public Buffer enter() {
      return ctrl('J');
    }

    public Buffer CR() {
      return ctrl('M');
    }

    public Buffer ctrlU() {
      return append("\u0015");
    }

    public Buffer tab() {
      return op(Operation.COMPLETE);
    }

    public Buffer escape() {
      return append("\u001b");
    }

    public Buffer back() {
      return op(Operation.BACKWARD_DELETE_CHAR);
    }

    public Buffer back(int n) {
      for (int i = 0; i < n; i++) {
        op(Operation.BACKWARD_DELETE_CHAR);
      }
      return this;
    }

    public Buffer left() {
      return append("\u001b[D");
    }

    public Buffer left(int n) {
      for (int i = 0; i < n; i++) {
        append("\u001b[D");
      }
      return this;
    }

    public Buffer right() {
      return append("\u001b[C");
    }

    public Buffer right(int n) {
      for (int i = 0; i < n; i++) {
        append("\u001b[C");
      }
      return this;
    }

    public Buffer up() {
      return append(getKeyForAction(Operation.PREVIOUS_HISTORY));
    }

    public Buffer down() {
      return append("\u001b[B");
    }

    public Buffer append(final String str) {
      for (byte b : str.getBytes()) {
        append(b);
      }
      return this;
    }

    public Buffer append(final int i) {
      out.write((byte) i);
      return this;
    }
  }
}