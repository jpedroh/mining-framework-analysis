package org.apache.commons.collections4.bloomfilter.hasher;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher.Builder;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Tests the
 * {@link org.apache.commons.collections4.bloomfilter.hasher.Hasher.Builder Hasher.Builder}.
 */
public class HasherBuilderTest {
  private static class TestBuilder implements Hasher.Builder {
    ArrayList<byte[]> items = new ArrayList<>();

    @Override public Hasher build() {
      throw new NotImplementedException("Not required");
    }

    @Override public Builder with(byte[] item) {
      items.add(item);
      return this;
    }
  }

  /**
     * Tests that adding CharSequence items works correctly.
     */
  @Test public void withCharSequenceTest() {
    final String ascii = "plain";
    final String extended = getExtendedString();
    for (final String s : new String[] { ascii, extended }) {
      for (final Charset cs : new Charset[] { StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8, StandardCharsets.UTF_16 }) {
        TestBuilder builder = new TestBuilder();
        builder.with(s, cs);
        assertArrayEquals(s.getBytes(cs), builder.items.get(0));
      }
    }
  }


<<<<<<< /usr/src/app/output/apache/commons-collections/96a6d523e8f8d041a6acb7814c8c0fa40c72000a/src/test/java/org/apache/commons/collections4/bloomfilter/hasher/HasherBuilderTest.java/left.java
  /**
     * Tests that adding unencoded CharSequence items works correctly.
     */
  @Test public void withUnecodedCharSequenceTest() {
    final String ascii = "plain";
    final String extended = getExtendedString();
    for (final String s : new String[] { ascii, extended }) {
      final TestBuilder builder = new TestBuilder();
      builder.withUnencoded(s);
      final byte[] encoded = builder.items.get(0);
      final char[] original = s.toCharArray();
      assertEquals(original.length * 2, encoded.length);
      final CharBuffer buffer = ByteBuffer.wrap(encoded).order(ByteOrder.LITTLE_ENDIAN).asCharBuffer();
      for (int i = 0; i < original.length; i++) {
        assertEquals(original[i], buffer.get(i));
      }
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
     * Tests that adding unencoded CharSequence items works correctly.
     */
  @Test public void withUnencodedCharSequenceTest() {
    final String ascii = "plain";
    final String extended = getExtendedString();
    for (final String s : new String[] { ascii, extended }) {
      final TestBuilder builder = new TestBuilder();
      builder.withUnencoded(s);
      final byte[] encoded = builder.items.get(0);
      final char[] original = s.toCharArray();
      Assert.assertEquals(original.length * 2, encoded.length);
      final CharBuffer buffer = ByteBuffer.wrap(encoded).order(ByteOrder.LITTLE_ENDIAN).asCharBuffer();
      for (int i = 0; i < original.length; i++) {
        Assert.assertEquals(original[i], buffer.get(i));
      }
    }
  }

  /**
     * Gets a string with non-standard characters.
     *
     * @return the extended string
     */
  static String getExtendedString() {
    final char[] data = { 'e', 'x', 't', 'e', 'n', 'd', 'e', 'd', ' ', 0xCA98, 0xD803, 0xDE6D };
    return String.valueOf(data);
  }
}