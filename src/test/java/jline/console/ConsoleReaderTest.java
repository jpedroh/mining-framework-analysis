package jline.console;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import jline.TerminalFactory;
import jline.WindowsTerminal;
import jline.console.history.History;
import jline.console.history.MemoryHistory;
import jline.internal.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static jline.console.ConsoleReaderTest.WindowsKey.*;
import static org.junit.Assert.*;

/**
 * Tests for the {@link ConsoleReader}.
 */
public class ConsoleReaderTest {
  private ByteArrayOutputStream output;

  @Before public void setUp() throws Exception {
    TerminalFactory.configure(TerminalFactory.AUTO);
    TerminalFactory.reset();
    System.setProperty(Configuration.JLINE_CONFIGURATION, "/no-such-file");
    System.setProperty(WindowsTerminal.DIRECT_CONSOLE, "false");
    System.setProperty(ConsoleReader.JLINE_INPUTRC, "/no/such/file");
    Configuration.reset();
  }

  @After public void tearDown() throws Exception {
    TerminalFactory.get().restore();
    TerminalFactory.reset();
  }

  private void assertWindowsKeyBehavior(String expected, char[] input) throws Exception {
    StringBuilder buffer = new StringBuilder();
    buffer.append(input);
    ConsoleReader reader = createConsole(buffer.toString());
    assertNotNull(reader);
    String line = reader.readLine();
    assertEquals(expected, line);
  }

  private ConsoleReader createConsole(String chars) throws Exception {
    System.err.println(Configuration.getEncoding());
    System.err.println(chars);
    return createConsole(chars.getBytes(Configuration.getEncoding()));
  }

  private ConsoleReader createConsole(byte[] bytes) throws Exception {
    return createConsole(null, bytes);
  }

  private ConsoleReader createConsole(String appName, byte[] bytes) throws Exception {
    InputStream in = new ByteArrayInputStream(bytes);
    output = new ByteArrayOutputStream();
    ConsoleReader reader = new ConsoleReader(appName, in, output, null);
    reader.setHistory(createSeededHistory());
    return reader;
  }

  private History createSeededHistory() {
    History history = new MemoryHistory();
    history.add("dir");
    history.add("cd c:\\");
    history.add("mkdir monkey");
    return history;
  }

  @Test public void testReadline() throws Exception {
    ConsoleReader consoleReader = createConsole("Sample String\r\n");
    assertNotNull(consoleReader);
    String line = consoleReader.readLine();
    assertEquals("Sample String", line);
  }

  @Test public void testReadlineWithUnicode() throws Exception {
    System.setProperty("input.encoding", "UTF-8");
    ConsoleReader consoleReader = createConsole("\u6771\u00e9\u00e8\r\n");
    assertNotNull(consoleReader);
    String line = consoleReader.readLine();
    assertEquals("\u6771\u00e9\u00e8", line);
  }

  @Test public void testReadlineWithMask() throws Exception {
    ConsoleReader consoleReader = createConsole("Sample String\r\n");
    assertNotNull(consoleReader);
    String line = consoleReader.readLine('*');
    assertEquals("Sample String", line);
    assertEquals("*************", output.toString().trim());
  }

  @Test public void testDeleteOnWindowsTerminal() throws Exception {
    if (!(TerminalFactory.get() instanceof WindowsTerminal)) {
      return;
    }
    char[] characters = new char[] { 'S', 's', (char) SPECIAL_KEY_INDICATOR.code, (char) LEFT_ARROW_KEY.code, (char) SPECIAL_KEY_INDICATOR.code, (char) DELETE_KEY.code, '\r', 'n' };
    assertWindowsKeyBehavior("S", characters);
  }

  @Test public void testNumpadDeleteOnWindowsTerminal() throws Exception {
    if (!(TerminalFactory.get() instanceof WindowsTerminal)) {
      return;
    }
    char[] characters = new char[] { 'S', 's', (char) NUMPAD_KEY_INDICATOR.code, (char) LEFT_ARROW_KEY.code, (char) NUMPAD_KEY_INDICATOR.code, (char) DELETE_KEY.code, '\r', 'n' };
    assertWindowsKeyBehavior("S", characters);
  }

  @Test public void testHomeKeyOnWindowsTerminal() throws Exception {
    if (!(TerminalFactory.get() instanceof WindowsTerminal)) {
      return;
    }
    char[] characters = new char[] { 'S', 's', (char) SPECIAL_KEY_INDICATOR.code, (char) HOME_KEY.code, 'x', '\r', '\n' };
    assertWindowsKeyBehavior("xSs", characters);
  }

  @Test public void testEndKeyOnWindowsTerminal() throws Exception {
    if (!(TerminalFactory.get() instanceof WindowsTerminal)) {
      return;
    }
    char[] characters = new char[] { 'S', 's', (char) SPECIAL_KEY_INDICATOR.code, (char) HOME_KEY.code, 'x', (char) SPECIAL_KEY_INDICATOR.code, (char) END_KEY.code, 'j', '\r', '\n' };
    assertWindowsKeyBehavior("xSsj", characters);
  }

  @Test public void testPageUpOnWindowsTerminal() throws Exception {
    if (!(TerminalFactory.get() instanceof WindowsTerminal)) {
      return;
    }
    char[] characters = new char[] { (char) SPECIAL_KEY_INDICATOR.code, (char) PAGE_UP_KEY.code, '\r', '\n' };
    assertWindowsKeyBehavior("dir", characters);
  }

  @Test public void testPageDownOnWindowsTerminal() throws Exception {
    if (!(TerminalFactory.get() instanceof WindowsTerminal)) {
      return;
    }
    char[] characters = new char[] { (char) SPECIAL_KEY_INDICATOR.code, (char) PAGE_DOWN_KEY.code, '\r', '\n' };
    assertWindowsKeyBehavior("mkdir monkey", characters);
  }

  @Test public void testEscapeOnWindowsTerminal() throws Exception {
    if (!(TerminalFactory.get() instanceof WindowsTerminal)) {
      return;
    }
    char[] characters = new char[] { 's', 's', 's', (char) SPECIAL_KEY_INDICATOR.code, (char) ESCAPE_KEY.code, '\r', '\n' };
    assertWindowsKeyBehavior("", characters);
  }

  @Test public void testInsertOnWindowsTerminal() throws Exception {
    if (!(TerminalFactory.get() instanceof WindowsTerminal)) {
      return;
    }
    char[] characters = new char[] { 'o', 'p', 's', (char) SPECIAL_KEY_INDICATOR.code, (char) HOME_KEY.code, (char) SPECIAL_KEY_INDICATOR.code, (char) INSERT_KEY.code, 'o', 'o', 'p', 's', '\r', '\n' };
    assertWindowsKeyBehavior("oops", characters);
  }

  @Test public void testExpansion() throws Exception {
    ConsoleReader reader = new ConsoleReader();
    MemoryHistory history = new MemoryHistory();
    history.setMaxSize(3);
    history.add("foo");
    history.add("dir");
    history.add("cd c:\\");
    history.add("mkdir monkey");
    reader.setHistory(history);
    assertEquals("echo a!", reader.expandEvents("echo a!"));
    assertEquals("mkdir monkey ; echo a!", reader.expandEvents("!! ; echo a!"));
    assertEquals("echo ! a", reader.expandEvents("echo ! a"));
    assertEquals("echo !\ta", reader.expandEvents("echo !\ta"));
    assertEquals("mkdir barey", reader.expandEvents("^monk^bar^"));
    assertEquals("mkdir barey", reader.expandEvents("^monk^bar"));
    assertEquals("a^monk^bar", reader.expandEvents("a^monk^bar"));
    assertEquals("mkdir monkey", reader.expandEvents("!!"));
    assertEquals("echo echo a", reader.expandEvents("echo !#a"));
    assertEquals("mkdir monkey", reader.expandEvents("!mk"));
    try {
      reader.expandEvents("!mz");
    } catch (IllegalArgumentException e) {
      assertEquals("!mz: event not found", e.getMessage());
    }
    assertEquals("mkdir monkey", reader.expandEvents("!?mo"));
    assertEquals("mkdir monkey", reader.expandEvents("!?mo?"));
    assertEquals("mkdir monkey", reader.expandEvents("!-1"));
    assertEquals("cd c:\\", reader.expandEvents("!-2"));
    assertEquals("cd c:\\", reader.expandEvents("!2"));
    assertEquals("mkdir monkey", reader.expandEvents("!3"));
    try {
      reader.expandEvents("!20");
    } catch (IllegalArgumentException e) {
      assertEquals("!20: event not found", e.getMessage());
    }
    try {
      reader.expandEvents("!-20");
    } catch (IllegalArgumentException e) {
      assertEquals("!-20: event not found", e.getMessage());
    }
  }

  @Test public void testStoringHistory() throws Exception {
    ConsoleReader reader = createConsole("foo ! bar\r\n");
    MemoryHistory history = new MemoryHistory();
    reader.setHistory(history);
    reader.setExpandEvents(true);
    String line = reader.readLine();
    assertEquals("foo ! bar", line);
    history.previous();
    assertEquals("foo \\! bar", history.current());
  }

  @Test public void testStoringHistoryWithExpandEventsOff() throws Exception {
    ConsoleReader reader = createConsole("foo ! bar\r\n");
    MemoryHistory history = new MemoryHistory();
    reader.setHistory(history);
    reader.setExpandEvents(false);
    String line = reader.readLine();
    assertEquals("foo ! bar", line);
    history.previous();
    assertEquals("foo ! bar", history.current());
  }

  @Test public void testMacro() throws Exception {
    ConsoleReader consoleReader = createConsole("\u0018(foo\u0018)\u0018e\r\n");
    assertNotNull(consoleReader);
    String line = consoleReader.readLine();
    assertEquals("foofoo", line);
  }

  @Test public void testInput() throws Exception {
    System.setProperty(ConsoleReader.JLINE_INPUTRC, getClass().getResource("/jline/internal/config1").toExternalForm());
    try {
      ConsoleReader consoleReader = createConsole("\u0018(foo\u0018)\u0018e\r\n");
      assertNotNull(consoleReader);
      assertEquals(Operation.UNIVERSAL_ARGUMENT, consoleReader.getKeys().getBound("" + ((char) ('U' - 'A' + 1))));
      assertEquals("Function Key \u2671", consoleReader.getKeys().getBound("\u001b[11~"));
      assertEquals(null, consoleReader.getKeys().getBound(((char) ('X' - 'A' + 1)) + "q"));
      consoleReader = createConsole("bash", new byte[0]);
      assertNotNull(consoleReader);
      assertEquals("\u001bb\"\u001bf\"", consoleReader.getKeys().getBound(((char) ('X' - 'A' + 1)) + "q"));
    }  finally {
      System.clearProperty(ConsoleReader.JLINE_INPUTRC);
    }
  }

  @Test public void testInput2() throws Exception {
    System.setProperty(ConsoleReader.JLINE_INPUTRC, getClass().getResource("/jline/internal/config2").toExternalForm());
    try {
      ConsoleReader consoleReader = createConsole("Bash", new byte[0]);
      assertNotNull(consoleReader);
      assertNotNull(consoleReader.getKeys().getBound("\u001b" + ((char) ('V' - 'A' + 1))));
    }  finally {
      System.clearProperty(ConsoleReader.JLINE_INPUTRC);
    }
  }

  @Test public void testInputBadConfig() throws Exception {
    System.setProperty(ConsoleReader.JLINE_INPUTRC, getClass().getResource("/jline/internal/config-bad").toExternalForm());
    try {
      ConsoleReader consoleReader = createConsole("Bash", new byte[0]);
      assertNotNull(consoleReader);
      assertEquals("\u001bb\"\u001bf\"", consoleReader.getKeys().getBound(((char) ('X' - 'A' + 1)) + "q"));
    }  finally {
      System.clearProperty(ConsoleReader.JLINE_INPUTRC);
    }
  }

  @Test public void testBell() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ConsoleReader consoleReader = new ConsoleReader(System.in, baos);
    assertFalse("default bell should be disabled", consoleReader.getBellEnabled());
    consoleReader.beep();
    assertEquals("out should not have received bell", 0, baos.toByteArray().length);
    consoleReader.setBellEnabled(true);
    assertTrue("bell should have been enabled", consoleReader.getBellEnabled());
    consoleReader.beep();
    assertEquals("out should have received bell", 1, baos.toByteArray().length);
    assertEquals("out should have received bell", ConsoleReader.KEYBOARD_BELL, baos.toByteArray()[0]);
  }

  public static enum WindowsKey {
    SPECIAL_KEY_INDICATOR(224),
    NUMPAD_KEY_INDICATOR(0),
    LEFT_ARROW_KEY(75),
    RIGHT_ARROW_KEY(77),
    UP_ARROW_KEY(72),
    DOWN_ARROW_KEY(80),
    DELETE_KEY(83),
    HOME_KEY(71),
    END_KEY(79),
    PAGE_UP_KEY(73),
    PAGE_DOWN_KEY(81),
    INSERT_KEY(82),
    ESCAPE_KEY(0)
    ;

    public final int code;

    WindowsKey(final int code) {
      this.code = code;
    }

    private static final Map<Integer, WindowsKey> codes;

    static {
      Map<Integer, WindowsKey> map = new HashMap<Integer, WindowsKey>();
      for (WindowsKey key : WindowsKey.values()) {
        map.put(key.code, key);
      }
      codes = map;
    }

    public static WindowsKey valueOf(final int code) {
      return codes.get(code);
    }
  }
}