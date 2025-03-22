package com.google.zxing.oned;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.ReaderException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.BitArray;
import java.util.Map;

public class Code128WriterTestCase extends Assert {
  private static final String FNC1 = "11110101110";

  private static final String FNC2 = "11110101000";

  private static final String FNC3 = "10111100010";

  private static final String FNC4 = "10111101110";

  private static final String START_CODE_B = "11010010000";

  private static final String START_CODE_C = "11010011100";

  private static final String SWITCH_CODE_B = "10111101110";

  public static final String QUIET_SPACE = "00000";

  public static final String STOP = "1100011101011";

  private Writer writer;

  private Code128Reader reader;

  @Before public void setup() {
    writer = new Code128Writer();
    reader = new Code128Reader();
  }

  @Test public void testEncodeWithFunc3() throws WriterException {
    String toEncode = "\u00f3" + "123";
    String expected = QUIET_SPACE + START_CODE_B + FNC3 + "10011100110" + "11001110010" + "11001011100" + "11101000110" + STOP + QUIET_SPACE;
    BitMatrix result = writer.encode(toEncode, BarcodeFormat.CODE_128, 0, 0);
    String actual = matrixToString(result);
    assertEquals(expected, actual);
  }

  @Test public void testEncodeWithFunc2() throws WriterException {
    String toEncode = "\u00f2" + "123";
    String expected = QUIET_SPACE + START_CODE_B + FNC2 + "10011100110" + "11001110010" + "11001011100" + "11100010110" + STOP + QUIET_SPACE;
    BitMatrix result = writer.encode(toEncode, BarcodeFormat.CODE_128, 0, 0);
    String actual = matrixToString(result);
    assertEquals(expected, actual);
  }

  @Test public void testEncodeWithFunc1() throws WriterException {
    String toEncode = "\u00f1" + "123";
    String expected = QUIET_SPACE + START_CODE_C + FNC1 + "10110011100" + SWITCH_CODE_B + "11001011100" + "10101111000" + STOP + QUIET_SPACE;
    BitMatrix result = writer.encode(toEncode, BarcodeFormat.CODE_128, 0, 0);
    String actual = matrixToString(result);
    assertEquals(expected, actual);
  }

  @Test public void testRoundtrip() throws WriterException, ReaderException {
    String toEncode = "\u00f1" + "10958" + "\u00f1" + "17160526";
    String expected = "1095817160526";
    Map<DecodeHintType, ?> hints = null;
    BitMatrix encResult = writer.encode(toEncode, BarcodeFormat.CODE_128, 0, 0);
    Result rtResult = reader.decodeRow(0, matrixToArray(encResult), 
<<<<<<< /usr/src/app/output/zxing/zxing/5413f5ceff8fea063f6042eefa08e6a8db78f30c/core/src/test/java/com/google/zxing/oned/Code128WriterTestCase.java/left.java
    null
=======
    hints
>>>>>>> /usr/src/app/output/zxing/zxing/5413f5ceff8fea063f6042eefa08e6a8db78f30c/core/src/test/java/com/google/zxing/oned/Code128WriterTestCase.java/right.java
    );
    String actual = rtResult.getText();
    assertEquals(expected, actual);
  }

  @Test public void testEncodeWithFunc4() throws WriterException {
    String toEncode = "\u00f4" + "123";
    String expected = QUIET_SPACE + START_CODE_B + FNC4 + "10011100110" + "11001110010" + "11001011100" + "11100011010" + STOP + QUIET_SPACE;
    BitMatrix result = writer.encode(toEncode, BarcodeFormat.CODE_128, 0, 0);
    String actual = matrixToString(result);
    assertEquals(expected, actual);
  }

  private static BitArray matrixToArray(BitMatrix result) {
    return result.getRow(0, null);
  }

  private static String matrixToString(BitMatrix result) {
    StringBuilder builder = new StringBuilder(result.getWidth());
    for (int i = 0; i < result.getWidth(); i++) {
      builder.append(result.get(i, 0) ? '1' : '0');
    }
    return builder.toString();
  }
}