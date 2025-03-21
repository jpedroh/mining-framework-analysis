package org.apache.commons.io.input;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.testtools.YellOnCloseInputStream;
import org.apache.commons.io.testtools.YellOnCloseOutputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit Test Case for {@link TeeInputStream}.
 */
public class TeeInputStreamTest {
  private static class ExceptionOnCloseByteArrayInputStream extends ByteArrayInputStream {
    public ExceptionOnCloseByteArrayInputStream() {
      super(new byte[0]);
    }

    @Override public void close() throws IOException {
      throw new IOException();
    }
  }

  private static class RecordCloseByteArrayInputStream extends ByteArrayInputStream {
    boolean closed;

    public RecordCloseByteArrayInputStream() {
      super(new byte[0]);
    }

    @Override public void close() throws IOException {
      super.close();
      closed = true;
    }
  }

  private static class ExceptionOnCloseByteArrayOutputStream extends ByteArrayOutputStream {
    @Override public void close() throws IOException {
      throw new IOException();
    }
  }

  private static class RecordCloseByteArrayOutputStream extends ByteArrayOutputStream {
    boolean closed;

    @Override public void close() throws IOException {
      super.close();
      closed = true;
    }
  }

  private final String ASCII = "US-ASCII";

  private InputStream tee;

  private ByteArrayOutputStream output;

  @Before public void setUp() throws Exception {
    final InputStream input = new ByteArrayInputStream("abc".getBytes(ASCII));
    output = new ByteArrayOutputStream();
    tee = new TeeInputStream(input, output);
  }

  @Test public void testReadNothing() throws Exception {
    assertEquals("", new String(output.toString(ASCII)));
  }

  @Test public void testReadOneByte() throws Exception {
    assertEquals('a', tee.read());
    assertEquals("a", new String(output.toString(ASCII)));
  }

  @Test public void testReadEverything() throws Exception {
    assertEquals('a', tee.read());
    assertEquals('b', tee.read());
    assertEquals('c', tee.read());
    assertEquals(-1, tee.read());
    assertEquals("abc", new String(output.toString(ASCII)));
  }

  @Test public void testReadToArray() throws Exception {
    final byte[] buffer = new byte[8];
    assertEquals(3, tee.read(buffer));
    assertEquals('a', buffer[0]);
    assertEquals('b', buffer[1]);
    assertEquals('c', buffer[2]);
    assertEquals(-1, tee.read(buffer));
    assertEquals("abc", new String(output.toString(ASCII)));
  }

  @Test public void testReadToArrayWithOffset() throws Exception {
    final byte[] buffer = new byte[8];
    assertEquals(3, tee.read(buffer, 4, 4));
    assertEquals('a', buffer[4]);
    assertEquals('b', buffer[5]);
    assertEquals('c', buffer[6]);
    assertEquals(-1, tee.read(buffer, 4, 4));
    assertEquals("abc", new String(output.toString(ASCII)));
  }

  @Test public void testSkip() throws Exception {
    assertEquals('a', tee.read());
    assertEquals(1, tee.skip(1));
    assertEquals('c', tee.read());
    assertEquals(-1, tee.read());
    assertEquals("ac", new String(output.toString(ASCII)));
  }

  @Test public void testMarkReset() throws Exception {
    assertEquals('a', tee.read());
    tee.mark(1);
    assertEquals('b', tee.read());
    tee.reset();
    assertEquals('b', tee.read());
    assertEquals('c', tee.read());
    assertEquals(-1, tee.read());
    assertEquals("abbc", new String(output.toString(ASCII)));
  }

  /**
     * Tests that the main {@code InputStream} is closed when closing the branch {@code OutputStream} throws an
     * exception on {@link TeeInputStream#close()}, if specified to do so.
     */
  @Test public void testCloseBranchIOException() throws Exception {
    final 
<<<<<<< /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/left.java
    RecordCloseByteArrayInputStream
=======
    ByteArrayInputStream
>>>>>>> /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/right.java
     goodIs = 
<<<<<<< /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/left.java
    new RecordCloseByteArrayInputStream()
=======
    mock(ByteArrayInputStream.class)
>>>>>>> /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/right.java
    ;
    final 
<<<<<<< /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/left.java
    ByteArrayOutputStream
=======
    OutputStream
>>>>>>> /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/right.java
     badOs = new 
<<<<<<< /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/left.java
    ExceptionOnCloseByteArrayOutputStream
=======
    YellOnCloseOutputStream
>>>>>>> /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/right.java
    ();
    final TeeInputStream nonClosingTis = new TeeInputStream(goodIs, badOs, false);
    nonClosingTis.close();

<<<<<<< /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/left.java
    Assert.assertTrue(goodIs.closed)
=======
    verify(goodIs).close()
>>>>>>> /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/right.java
    ;
    final TeeInputStream closingTis = new TeeInputStream(goodIs, badOs, true);
    try {
      closingTis.close();
      Assert.fail("Expected " + IOException.class.getName());
    } catch (final IOException e) {

<<<<<<< /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/left.java
      Assert.assertTrue(goodIs.closed)
=======
      verify(goodIs, times(2)).close()
>>>>>>> /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/right.java
      ;
    }
  }

  /**
     * Tests that the branch {@code OutputStream} is closed when closing the main {@code InputStream} throws an
     * exception on {@link TeeInputStream#close()}, if specified to do so.
     */
  @Test public void testCloseMainIOException() throws IOException {
    final 
<<<<<<< /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/left.java
    ByteArrayInputStream
=======
    InputStream
>>>>>>> /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/right.java
     badIs = new 
<<<<<<< /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/left.java
    ExceptionOnCloseByteArrayInputStream
=======
    YellOnCloseInputStream
>>>>>>> /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/right.java
    ();
    final 
<<<<<<< /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/left.java
    RecordCloseByteArrayOutputStream
=======
    ByteArrayOutputStream
>>>>>>> /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/right.java
     goodOs = 
<<<<<<< /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/left.java
    new RecordCloseByteArrayOutputStream()
=======
    mock(ByteArrayOutputStream.class)
>>>>>>> /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/right.java
    ;
    final TeeInputStream nonClosingTis = new TeeInputStream(badIs, goodOs, false);
    try {
      nonClosingTis.close();
      Assert.fail("Expected " + IOException.class.getName());
    } catch (final IOException e) {

<<<<<<< /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/left.java
      Assert.assertFalse(goodOs.closed)
=======
      verify(goodOs, never()).close()
>>>>>>> /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/right.java
      ;
    }
    final TeeInputStream closingTis = new TeeInputStream(badIs, goodOs, true);
    try {
      closingTis.close();
      Assert.fail("Expected " + IOException.class.getName());
    } catch (final IOException e) {

<<<<<<< /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/left.java
      Assert.assertTrue(goodOs.closed)
=======
      verify(goodOs).close()
>>>>>>> /usr/src/app/output/apache/commons-io/a07c36067143ced7302aace252d6219d952cbd14/src/test/java/org/apache/commons/io/input/TeeInputStreamTest.java/right.java
      ;
    }
  }
}