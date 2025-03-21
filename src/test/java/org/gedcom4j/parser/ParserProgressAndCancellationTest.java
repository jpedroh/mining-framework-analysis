package org.gedcom4j.parser;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.exception.ParserCancelledException;
import org.gedcom4j.parser.event.ParseProgressEvent;
import org.gedcom4j.parser.event.ParseProgressListener;
import org.junit.Test;

/**
 * Test getting progress from the parser and being able to cancel
 * 
 * @author frizbog
 */
public class ParserProgressAndCancellationTest implements ParseProgressListener {
  /**
     * Number of notifications received
     */
  int notificationCount = 0;

  /**
     * The parser being tested
     */
  private GedcomParser gp = new GedcomParser();

  /**
     * How many notifications to cancel after
     */
  private int cancelAfter = 0;

  @Override public void progressNotification(ParseProgressEvent e) {
    notificationCount++;
    if (notificationCount >= cancelAfter) {
      gp.cancel();
    }
  }

  /**
     * Test getting notifications and cancelling the parsing of an ascii file
     * 
     * @throws IOException
     * @throws GedcomParserException
     */
  @Test(expected = ParserCancelledException.class) public void testCancellation() throws IOException, GedcomParserException {
    gp = new GedcomParser();
    cancelAfter = 5;
    gp.registerParseObserver(this);
    gp.setParseNotificationRate(1);
    gp.load("sample/willis-ascii.ged");
  }

  /**
     * Test getting notifications and cancelling the parsing of an ascii file
     * 
     * @throws IOException
     * @throws GedcomParserException
     */
  @Test public void testNoCancellation() throws IOException, GedcomParserException {
    gp = new GedcomParser();
    cancelAfter = Integer.MAX_VALUE;
    gp.registerParseObserver(this);
    gp.setParseNotificationRate(10);
    gp.load("sample/willis-ascii.ged");
    assertEquals(
<<<<<<< /usr/src/app/output/frizbog/gedcom4j/11c8b3f6955f298ade7583d2fa0d000b282e597f/src/test/java/org/gedcom4j/parser/ParserProgressAndCancellationTest.java/left.java
    128
=======
    40
>>>>>>> /usr/src/app/output/frizbog/gedcom4j/11c8b3f6955f298ade7583d2fa0d000b282e597f/src/test/java/org/gedcom4j/parser/ParserProgressAndCancellationTest.java/right.java
    , notificationCount);
  }
}