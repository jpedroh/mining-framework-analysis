package com.google.zxing.qrcode.encoder;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.StringUtils;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Mode;
import com.google.zxing.qrcode.decoder.Version;
import org.junit.Assert;
import org.junit.Test;
import java.util.EnumMap;
import java.util.Map;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author satorux@google.com (Satoru Takabayashi) - creator
 * @author mysen@google.com (Chris Mysen) - ported from C++
 */
public final class EncoderTestCase extends Assert {
  @Test public void testGetAlphanumericCode() {
    for (int i = 0; i < 10; ++i) {
      assertEquals(i, Encoder.getAlphanumericCode('0' + i));
    }
    for (int i = 10; i < 36; ++i) {
      assertEquals(i, Encoder.getAlphanumericCode('A' + i - 10));
    }
    assertEquals(36, Encoder.getAlphanumericCode(' '));
    assertEquals(37, Encoder.getAlphanumericCode('$'));
    assertEquals(38, Encoder.getAlphanumericCode('%'));
    assertEquals(39, Encoder.getAlphanumericCode('*'));
    assertEquals(40, Encoder.getAlphanumericCode('+'));
    assertEquals(41, Encoder.getAlphanumericCode('-'));
    assertEquals(42, Encoder.getAlphanumericCode('.'));
    assertEquals(43, Encoder.getAlphanumericCode('/'));
    assertEquals(44, Encoder.getAlphanumericCode(':'));
    assertEquals(-1, Encoder.getAlphanumericCode('a'));
    assertEquals(-1, Encoder.getAlphanumericCode('#'));
    assertEquals(-1, Encoder.getAlphanumericCode('\u0000'));
  }

  @Test public void testChooseMode() {
    assertSame(Mode.NUMERIC, Encoder.chooseMode("0"));
    assertSame(Mode.NUMERIC, Encoder.chooseMode("0123456789"));
    assertSame(Mode.ALPHANUMERIC, Encoder.chooseMode("A"));
    assertSame(Mode.ALPHANUMERIC, Encoder.chooseMode("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:"));
    assertSame(Mode.BYTE, Encoder.chooseMode("a"));
    assertSame(Mode.BYTE, Encoder.chooseMode("#"));
    assertSame(Mode.BYTE, Encoder.chooseMode(""));
    assertSame(Mode.BYTE, Encoder.chooseMode(shiftJISString(bytes(0x8, 0xa, 0x8, 0xa, 0x8, 0xa, 0x8, 0xa6))));
    assertSame(Mode.BYTE, Encoder.chooseMode(shiftJISString(bytes(0x9, 0xf, 0x9, 0x7b))));
    assertSame(Mode.BYTE, Encoder.chooseMode(shiftJISString(bytes(0xe, 0x4, 0x9, 0x5, 0x9, 0x61))));
  }

  @Test public void testEncode() throws WriterException {
    QRCode qrCode = Encoder.encode("ABCDEF", ErrorCorrectionLevel.H);
    String expected = "<<\n" + " mode: ALPHANUMERIC\n" + " ecLevel: H\n" + " version: 1\n" + " maskPattern: 0\n" + " matrix:\n" + " 1 1 1 1 1 1 1 0 1 1 1 1 0 0 1 1 1 1 1 1 1\n" + " 1 0 0 0 0 0 1 0 0 1 1 1 0 0 1 0 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 0 1 1 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 1 1 1 0 1 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 1 1 0 0 1 0 1 1 1 0 1\n" + " 1 0 0 0 0 0 1 0 0 1 0 0 0 0 1 0 0 0 0 0 1\n" + " 1 1 1 1 1 1 1 0 1 0 1 0 1 0 1 1 1 1 1 1 1\n" + " 0 0 0 0 0 0 0 0 0 0 1 0 1 0 0 0 0 0 0 0 0\n" + " 0 0 1 0 1 1 1 0 1 1 0 0 1 1 0 0 0 1 0 0 1\n" + " 1 0 1 1 1 0 0 1 0 0 0 1 0 1 0 0 0 0 0 0 0\n" + " 0 0 1 1 0 0 1 0 1 0 0 0 1 0 1 0 1 0 1 1 0\n" + " 1 1 0 1 0 1 0 1 1 1 0 1 0 1 0 0 0 0 0 1 0\n" + " 0 0 1 1 0 1 1 1 1 0 0 0 1 0 1 0 1 1 1 1 0\n" + " 0 0 0 0 0 0 0 0 1 0 0 1 1 1 0 1 0 1 0 0 0\n" + " 1 1 1 1 1 1 1 0 0 0 1 0 1 0 1 1 0 0 0 0 1\n" + " 1 0 0 0 0 0 1 0 1 1 1 1 0 1 0 1 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 1 0 1 1 0 1 0 1 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 1 0 1 1 1 1 0 1 0 1 0\n" + " 1 0 1 1 1 0 1 0 1 0 0 0 1 0 1 0 1 1 1 0 1\n" + " 1 0 0 0 0 0 1 0 0 1 1 0 1 1 0 1 0 0 0 1 1\n" + " 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0 0 1 0 1 0 1\n" + ">>\n";
    assertEquals(expected, qrCode.toString());
  }

  @Test public void testEncodeWithVersion() throws WriterException {
    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.QR_VERSION, 7);
    QRCode qrCode = Encoder.encode("ABCDEF", ErrorCorrectionLevel.H, hints);
    assertTrue(qrCode.toString().contains(" version: 7\n"));
  }

  @Test(expected = WriterException.class) public void testEncodeWithVersionTooSmall() throws WriterException {
    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.QR_VERSION, 3);
    Encoder.encode("THISMESSAGEISTOOLONGFORAQRCODEVERSION3", ErrorCorrectionLevel.H, hints);
  }

  @Test public void testSimpleUTF8ECI() throws WriterException {
    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
    QRCode qrCode = Encoder.encode("hello", ErrorCorrectionLevel.H, hints);
    String expected = "<<\n" + " mode: BYTE\n" + " ecLevel: H\n" + " version: 1\n" + " maskPattern: 3\n" + " matrix:\n" + " 1 1 1 1 1 1 1 0 0 0 0 0 0 0 1 1 1 1 1 1 1\n" + " 1 0 0 0 0 0 1 0 0 0 1 0 1 0 1 0 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 0 1 0 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 1 0 1 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 1 0 1 0 1 0 1 0 1 1 1 0 1\n" + " 1 0 0 0 0 0 1 0 0 0 0 0 1 0 1 0 0 0 0 0 1\n" + " 1 1 1 1 1 1 1 0 1 0 1 0 1 0 1 1 1 1 1 1 1\n" + " 0 0 0 0 0 0 0 0 1 1 1 0 0 0 0 0 0 0 0 0 0\n" + " 0 0 1 1 0 0 1 1 1 1 0 0 0 1 1 0 1 0 0 0 0\n" + " 0 0 1 1 1 0 0 0 0 0 1 1 0 0 0 1 0 1 1 1 0\n" + " 0 1 0 1 0 1 1 1 0 1 0 1 0 0 0 0 0 1 1 1 1\n" + " 1 1 0 0 1 0 0 1 1 0 0 1 1 1 1 0 1 0 1 1 0\n" + " 0 0 0 0 1 0 1 1 1 1 0 0 0 0 0 1 0 0 1 0 0\n" + " 0 0 0 0 0 0 0 0 1 1 1 1 0 0 1 1 1 0 0 0 1\n" + " 1 1 1 1 1 1 1 0 1 1 1 0 1 0 1 1 0 0 1 0 0\n" + " 1 0 0 0 0 0 1 0 0 0 1 0 0 1 1 1 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 0 0 0 0 1 1 0 0 0 0 0\n" + " 1 0 1 1 1 0 1 0 1 1 1 0 1 0 0 0 1 1 0 0 0\n" + " 1 0 1 1 1 0 1 0 1 1 0 0 0 1 0 0 1 0 0 0 0\n" + " 1 0 0 0 0 0 1 0 0 0 0 1 1 0 1 0 1 0 1 1 0\n" + " 1 1 1 1 1 1 1 0 0 1 0 1 1 1 0 1 1 0 0 0 0\n" + ">>\n";
    assertEquals(expected, qrCode.toString());
  }

  @Test public void testEncodeKanjiMode() throws WriterException {
    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.CHARACTER_SET, "Shift_JIS");
    QRCode qrCode = Encoder.encode("\u65e5\u672c", ErrorCorrectionLevel.M, hints);
    String expected = "<<\n" + " mode: KANJI\n" + " ecLevel: M\n" + " version: 1\n" + " maskPattern: 4\n" + " matrix:\n" + " 1 1 1 1 1 1 1 0 1 1 1 1 0 0 1 1 1 1 1 1 1\n" + " 1 0 0 0 0 0 1 0 0 0 0 1 1 0 1 0 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 0 0 1 0 0 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 1 0 1 0 1 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 1 1 0 1 1 0 1 0 1 1 1 0 1\n" + " 1 0 0 0 0 0 1 0 1 0 1 0 1 0 1 0 0 0 0 0 1\n" + " 1 1 1 1 1 1 1 0 1 0 1 0 1 0 1 1 1 1 1 1 1\n" + " 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0\n" + " 1 0 0 0 1 0 1 1 1 0 0 0 1 1 1 1 1 1 0 0 1\n" + " 0 1 1 0 0 1 0 1 1 0 1 0 1 1 1 0 0 0 1 0 1\n" + " 1 1 1 1 0 1 1 1 0 0 1 0 1 1 0 0 0 0 1 1 1\n" + " 1 0 1 0 1 1 0 0 0 0 1 1 1 0 0 1 0 0 1 1 0\n" + " 0 0 1 0 1 1 1 1 1 1 1 1 0 0 1 1 1 1 0 1 1\n" + " 0 0 0 0 0 0 0 0 1 1 1 1 1 0 0 1 0 1 0 0 0\n" + " 1 1 1 1 1 1 1 0 1 1 0 1 0 0 1 1 1 1 1 1 0\n" + " 1 0 0 0 0 0 1 0 0 0 0 0 0 1 1 0 1 0 1 0 1\n" + " 1 0 1 1 1 0 1 0 1 0 1 0 1 1 1 0 0 0 1 1 1\n" + " 1 0 1 1 1 0 1 0 0 1 0 0 1 1 1 0 0 0 1 1 1\n" + " 1 0 1 1 1 0 1 0 0 1 1 0 1 1 0 0 0 1 0 0 0\n" + " 1 0 0 0 0 0 1 0 0 0 1 1 1 0 0 1 0 1 0 0 0\n" + " 1 1 1 1 1 1 1 0 1 1 1 1 0 0 1 1 1 0 1 1 0\n" + ">>\n";
    assertEquals(expected, qrCode.toString());
  }

  @Test public void testEncodeShiftjisNumeric() throws WriterException {
    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.CHARACTER_SET, "Shift_JIS");
    QRCode qrCode = Encoder.encode("0123", ErrorCorrectionLevel.M, hints);
    String expected = "<<\n" + " mode: NUMERIC\n" + " ecLevel: M\n" + " version: 1\n" + " maskPattern: 0\n" + " matrix:\n" + " 1 1 1 1 1 1 1 0 0 0 0 0 1 0 1 1 1 1 1 1 1\n" + " 1 0 0 0 0 0 1 0 1 1 0 1 0 0 1 0 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 1 0 0 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 0 0 1 0 0 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 1 0 1 1 1 0 1 0 1 1 1 0 1\n" + " 1 0 0 0 0 0 1 0 0 1 0 1 0 0 1 0 0 0 0 0 1\n" + " 1 1 1 1 1 1 1 0 1 0 1 0 1 0 1 1 1 1 1 1 1\n" + " 0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 0 0\n" + " 1 0 1 0 1 0 1 0 0 0 0 0 1 0 0 0 1 0 0 1 0\n" + " 0 0 0 0 0 0 0 1 1 0 1 1 0 1 0 1 0 1 0 1 0\n" + " 0 1 0 1 0 1 1 1 1 0 0 1 0 1 1 1 0 1 0 1 0\n" + " 0 1 1 1 0 0 0 0 0 0 1 1 1 1 0 1 1 1 0 1 0\n" + " 0 0 0 1 1 1 1 1 1 1 1 1 0 1 1 1 0 0 1 0 1\n" + " 0 0 0 0 0 0 0 0 1 1 0 0 0 0 1 0 0 0 1 1 0\n" + " 1 1 1 1 1 1 1 0 0 1 0 0 1 0 0 0 1 0 0 0 1\n" + " 1 0 0 0 0 0 1 0 0 1 0 0 0 0 1 0 0 0 1 0 0\n" + " 1 0 1 1 1 0 1 0 1 1 0 0 1 0 1 0 1 0 1 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 1 1 0 1 0 1 0 1 0 1 0\n" + " 1 0 1 1 1 0 1 0 1 0 1 1 0 1 1 1 0 1 1 0 1\n" + " 1 0 0 0 0 0 1 0 0 0 1 1 1 1 0 1 1 1 0 0 0\n" + " 1 1 1 1 1 1 1 0 1 0 1 1 0 1 1 1 0 1 1 0 1\n" + ">>\n";
    assertEquals(expected, qrCode.toString());
  }

  @Test public void testEncodeGS1WithStringTypeHint() throws WriterException {
    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.GS1_FORMAT, "true");
    QRCode qrCode = Encoder.encode("100001%11171218", ErrorCorrectionLevel.H, hints);
    verifyGS1EncodedData(qrCode);
  }

  @Test public void testEncodeGS1WithBooleanTypeHint() throws WriterException {
    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.GS1_FORMAT, true);
    QRCode qrCode = Encoder.encode("100001%11171218", ErrorCorrectionLevel.H, hints);
    verifyGS1EncodedData(qrCode);
  }

  @Test public void testDoesNotEncodeGS1WhenBooleanTypeHintExplicitlyFalse() throws WriterException {
    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.GS1_FORMAT, false);
    QRCode qrCode = Encoder.encode("ABCDEF", ErrorCorrectionLevel.H, hints);
    verifyNotGS1EncodedData(qrCode);
  }

  @Test public void testDoesNotEncodeGS1WhenStringTypeHintExplicitlyFalse() throws WriterException {
    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.GS1_FORMAT, "false");
    QRCode qrCode = Encoder.encode("ABCDEF", ErrorCorrectionLevel.H, hints);
    verifyNotGS1EncodedData(qrCode);
  }

  @Test public void testGS1ModeHeaderWithECI() throws WriterException {
    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
    hints.put(EncodeHintType.GS1_FORMAT, true);
    QRCode qrCode = Encoder.encode("hello", ErrorCorrectionLevel.H, hints);
    String expected = "<<\n" + " mode: BYTE\n" + " ecLevel: H\n" + " version: 1\n" + " maskPattern: 6\n" + " matrix:\n" + " 1 1 1 1 1 1 1 0 0 0 1 1 0 0 1 1 1 1 1 1 1\n" + " 1 0 0 0 0 0 1 0 0 1 1 0 0 0 1 0 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 1 1 0 0 0 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 1 1 0 1 0 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 0 0 1 1 0 0 1 0 1 1 1 0 1\n" + " 1 0 0 0 0 0 1 0 0 1 0 0 1 0 1 0 0 0 0 0 1\n" + " 1 1 1 1 1 1 1 0 1 0 1 0 1 0 1 1 1 1 1 1 1\n" + " 0 0 0 0 0 0 0 0 0 0 1 1 1 0 0 0 0 0 0 0 0\n" + " 0 0 0 1 1 0 1 1 0 1 0 0 0 0 0 0 0 1 1 0 0\n" + " 0 1 0 1 1 0 0 1 0 1 1 1 1 1 1 0 1 1 1 0 1\n" + " 0 1 1 1 1 0 1 0 0 1 0 1 0 1 1 1 0 0 1 0 1\n" + " 1 1 1 1 1 0 0 1 0 0 0 1 1 0 0 1 0 0 1 0 0\n" + " 1 0 0 1 0 0 1 1 0 1 1 0 1 0 1 0 0 1 0 0 1\n" + " 0 0 0 0 0 0 0 0 1 1 1 1 1 1 0 0 1 0 0 0 1\n" + " 1 1 1 1 1 1 1 0 1 0 0 1 0 1 1 0 1 0 1 0 0\n" + " 1 0 0 0 0 0 1 0 0 1 0 0 0 0 1 0 1 1 1 0 0\n" + " 1 0 1 1 1 0 1 0 1 1 0 1 1 0 0 0 1 1 0 0 0\n" + " 1 0 1 1 1 0 1 0 1 0 1 1 1 1 1 0 0 0 1 1 0\n" + " 1 0 1 1 1 0 1 0 0 0 1 0 0 1 0 0 1 0 1 1 1\n" + " 1 0 0 0 0 0 1 0 0 1 0 0 0 0 0 0 0 1 1 0 0\n" + " 1 1 1 1 1 1 1 0 0 1 0 1 0 0 1 0 0 0 0 0 0\n" + ">>\n";
    assertEquals(expected, qrCode.toString());
  }

  @Test public void testAppendModeInfo() {
    BitArray bits = new BitArray();
    Encoder.appendModeInfo(Mode.NUMERIC, bits);
    assertEquals(" ...X", bits.toString());
  }

  @Test public void testAppendLengthInfo() throws WriterException {
    BitArray bits = new BitArray();
    Encoder.appendLengthInfo(1, Version.getVersionForNumber(1), Mode.NUMERIC, bits);
    assertEquals(" ........ .X", bits.toString());
    bits = new BitArray();
    Encoder.appendLengthInfo(2, Version.getVersionForNumber(10), Mode.ALPHANUMERIC, bits);
    assertEquals(" ........ .X.", bits.toString());
    bits = new BitArray();
    Encoder.appendLengthInfo(255, Version.getVersionForNumber(27), Mode.BYTE, bits);
    assertEquals(" ........ XXXXXXXX", bits.toString());
    bits = new BitArray();
    Encoder.appendLengthInfo(512, Version.getVersionForNumber(40), Mode.KANJI, bits);
    assertEquals(" ..X..... ....", bits.toString());
  }

  @Test public void testAppendBytes() throws WriterException {
    BitArray bits = new BitArray();
    Encoder.appendBytes("1", Mode.NUMERIC, bits, Encoder.DEFAULT_BYTE_MODE_ENCODING);
    assertEquals(" ...X", bits.toString());
    bits = new BitArray();
    Encoder.appendBytes("A", Mode.ALPHANUMERIC, bits, Encoder.DEFAULT_BYTE_MODE_ENCODING);
    assertEquals(" ..X.X.", bits.toString());
    try {
      Encoder.appendBytes("a", Mode.ALPHANUMERIC, bits, Encoder.DEFAULT_BYTE_MODE_ENCODING);
    } catch (WriterException we) {
    }
    bits = new BitArray();
    Encoder.appendBytes("abc", Mode.BYTE, bits, Encoder.DEFAULT_BYTE_MODE_ENCODING);
    assertEquals(" .XX....X .XX...X. .XX...XX", bits.toString());
    Encoder.appendBytes("\u0000", Mode.BYTE, bits, Encoder.DEFAULT_BYTE_MODE_ENCODING);
    bits = new BitArray();
    Encoder.appendBytes(shiftJISString(bytes(0x93, 0x5f)), Mode.KANJI, bits, Encoder.DEFAULT_BYTE_MODE_ENCODING);
    assertEquals(" .XX.XX.. XXXXX", bits.toString());
  }

  @Test public void testTerminateBits() throws WriterException {
    BitArray v = new BitArray();
    Encoder.terminateBits(0, v);
    assertEquals("", v.toString());
    v = new BitArray();
    Encoder.terminateBits(1, v);
    assertEquals(" ........", v.toString());
    v = new BitArray();
    v.appendBits(0, 3);
    Encoder.terminateBits(1, v);
    assertEquals(" ........", v.toString());
    v = new BitArray();
    v.appendBits(0, 5);
    Encoder.terminateBits(1, v);
    assertEquals(" ........", v.toString());
    v = new BitArray();
    v.appendBits(0, 8);
    Encoder.terminateBits(1, v);
    assertEquals(" ........", v.toString());
    v = new BitArray();
    Encoder.terminateBits(2, v);
    assertEquals(" ........ XXX.XX..", v.toString());
    v = new BitArray();
    v.appendBits(0, 1);
    Encoder.terminateBits(3, v);
    assertEquals(" ........ XXX.XX.. ...X...X", v.toString());
  }

  @Test public void testGetNumDataBytesAndNumECBytesForBlockID() throws WriterException {
    int[] numDataBytes = new int[1];
    int[] numEcBytes = new int[1];
    Encoder.getNumDataBytesAndNumECBytesForBlockID(26, 9, 1, 0, numDataBytes, numEcBytes);
    assertEquals(9, numDataBytes[0]);
    assertEquals(17, numEcBytes[0]);
    Encoder.getNumDataBytesAndNumECBytesForBlockID(70, 26, 2, 0, numDataBytes, numEcBytes);
    assertEquals(13, numDataBytes[0]);
    assertEquals(22, numEcBytes[0]);
    Encoder.getNumDataBytesAndNumECBytesForBlockID(70, 26, 2, 1, numDataBytes, numEcBytes);
    assertEquals(13, numDataBytes[0]);
    assertEquals(22, numEcBytes[0]);
    Encoder.getNumDataBytesAndNumECBytesForBlockID(196, 66, 5, 0, numDataBytes, numEcBytes);
    assertEquals(13, numDataBytes[0]);
    assertEquals(26, numEcBytes[0]);
    Encoder.getNumDataBytesAndNumECBytesForBlockID(196, 66, 5, 4, numDataBytes, numEcBytes);
    assertEquals(14, numDataBytes[0]);
    assertEquals(26, numEcBytes[0]);
    Encoder.getNumDataBytesAndNumECBytesForBlockID(3706, 1276, 81, 0, numDataBytes, numEcBytes);
    assertEquals(15, numDataBytes[0]);
    assertEquals(30, numEcBytes[0]);
    Encoder.getNumDataBytesAndNumECBytesForBlockID(3706, 1276, 81, 20, numDataBytes, numEcBytes);
    assertEquals(16, numDataBytes[0]);
    assertEquals(30, numEcBytes[0]);
    Encoder.getNumDataBytesAndNumECBytesForBlockID(3706, 1276, 81, 80, numDataBytes, numEcBytes);
    assertEquals(16, numDataBytes[0]);
    assertEquals(30, numEcBytes[0]);
  }

  @Test public void testInterleaveWithECBytes() throws WriterException {
    byte[] dataBytes = bytes(32, 65, 205, 69, 41, 220, 46, 128, 236);
    BitArray in = new BitArray();
    for (byte dataByte : dataBytes) {
      in.appendBits(dataByte, 8);
    }
    BitArray out = Encoder.interleaveWithECBytes(in, 26, 9, 1);
    byte[] expected = bytes(32, 65, 205, 69, 41, 220, 46, 128, 236, 42, 159, 74, 221, 244, 169, 239, 150, 138, 70, 237, 85, 224, 96, 74, 219, 61);
    assertEquals(expected.length, out.getSizeInBytes());
    byte[] outArray = new byte[expected.length];
    out.toBytes(0, outArray, 0, expected.length);
    for (int x = 0; x < expected.length; x++) {
      assertEquals(expected[x], outArray[x]);
    }
    dataBytes = bytes(67, 70, 22, 38, 54, 70, 86, 102, 118, 134, 150, 166, 182, 198, 214, 230, 247, 7, 23, 39, 55, 71, 87, 103, 119, 135, 151, 166, 22, 38, 54, 70, 86, 102, 118, 134, 150, 166, 182, 198, 214, 230, 247, 7, 23, 39, 55, 71, 87, 103, 119, 135, 151, 160, 236, 17, 236, 17, 236, 17, 236, 17);
    in = new BitArray();
    for (byte dataByte : dataBytes) {
      in.appendBits(dataByte, 8);
    }
    out = Encoder.interleaveWithECBytes(in, 134, 62, 4);
    expected = bytes(67, 230, 54, 55, 70, 247, 70, 71, 22, 7, 86, 87, 38, 23, 102, 103, 54, 39, 118, 119, 70, 55, 134, 135, 86, 71, 150, 151, 102, 87, 166, 160, 118, 103, 182, 236, 134, 119, 198, 17, 150, 135, 214, 236, 166, 151, 230, 17, 182, 166, 247, 236, 198, 22, 7, 17, 214, 38, 23, 236, 39, 17, 175, 155, 245, 236, 80, 146, 56, 74, 155, 165, 133, 142, 64, 183, 132, 13, 178, 54, 132, 108, 45, 113, 53, 50, 214, 98, 193, 152, 233, 147, 50, 71, 65, 190, 82, 51, 209, 199, 171, 54, 12, 112, 57, 113, 155, 117, 211, 164, 117, 30, 158, 225, 31, 190, 242, 38, 140, 61, 179, 154, 214, 138, 147, 87, 27, 96, 77, 47, 187, 49, 156, 214);
    assertEquals(expected.length, out.getSizeInBytes());
    outArray = new byte[expected.length];
    out.toBytes(0, outArray, 0, expected.length);
    for (int x = 0; x < expected.length; x++) {
      assertEquals(expected[x], outArray[x]);
    }
  }

  private static byte[] bytes(int... ints) {
    byte[] bytes = new byte[ints.length];
    for (int i = 0; i < ints.length; i++) {
      bytes[i] = (byte) ints[i];
    }
    return bytes;
  }

  @Test public void testAppendNumericBytes() {
    BitArray bits = new BitArray();
    Encoder.appendNumericBytes("1", bits);
    assertEquals(" ...X", bits.toString());
    bits = new BitArray();
    Encoder.appendNumericBytes("12", bits);
    assertEquals(" ...XX..", bits.toString());
    bits = new BitArray();
    Encoder.appendNumericBytes("123", bits);
    assertEquals(" ...XXXX. XX", bits.toString());
    bits = new BitArray();
    Encoder.appendNumericBytes("1234", bits);
    assertEquals(" ...XXXX. XX.X..", bits.toString());
    bits = new BitArray();
    Encoder.appendNumericBytes("", bits);
    assertEquals("", bits.toString());
  }

  @Test public void testAppendAlphanumericBytes() throws WriterException {
    BitArray bits = new BitArray();
    Encoder.appendAlphanumericBytes("A", bits);
    assertEquals(" ..X.X.", bits.toString());
    bits = new BitArray();
    Encoder.appendAlphanumericBytes("AB", bits);
    assertEquals(" ..XXX..X X.X", bits.toString());
    bits = new BitArray();
    Encoder.appendAlphanumericBytes("ABC", bits);
    assertEquals(" ..XXX..X X.X..XX. .", bits.toString());
    bits = new BitArray();
    Encoder.appendAlphanumericBytes("", bits);
    assertEquals("", bits.toString());
    try {
      Encoder.appendAlphanumericBytes("abc", new BitArray());
    } catch (WriterException we) {
    }
  }

  @Test public void testAppend8BitBytes() {
    BitArray bits = new BitArray();
    Encoder.append8BitBytes("abc", bits, Encoder.DEFAULT_BYTE_MODE_ENCODING);
    assertEquals(" .XX....X .XX...X. .XX...XX", bits.toString());
    bits = new BitArray();
    Encoder.append8BitBytes("", bits, Encoder.DEFAULT_BYTE_MODE_ENCODING);
    assertEquals("", bits.toString());
  }

  @Test public void testAppendKanjiBytes() throws WriterException {
    BitArray bits = new BitArray();
    Encoder.appendKanjiBytes(shiftJISString(bytes(0x93, 0x5f)), bits);
    assertEquals(" .XX.XX.. XXXXX", bits.toString());
    Encoder.appendKanjiBytes(shiftJISString(bytes(0xe4, 0xaa)), bits);
    assertEquals(" .XX.XX.. XXXXXXX. X.X.X.X. X.", bits.toString());
  }

  @Test public void testGenerateECBytes() {
    byte[] dataBytes = bytes(32, 65, 205, 69, 41, 220, 46, 128, 236);
    byte[] ecBytes = Encoder.generateECBytes(dataBytes, 17);
    int[] expected = { 42, 159, 74, 221, 244, 169, 239, 150, 138, 70, 237, 85, 224, 96, 74, 219, 61 };
    assertEquals(expected.length, ecBytes.length);
    for (int x = 0; x < expected.length; x++) {
      assertEquals(expected[x], ecBytes[x] & 0xFF);
    }
    dataBytes = bytes(67, 70, 22, 38, 54, 70, 86, 102, 118, 134, 150, 166, 182, 198, 214);
    ecBytes = Encoder.generateECBytes(dataBytes, 18);
    expected = new int[] { 175, 80, 155, 64, 178, 45, 214, 233, 65, 209, 12, 155, 117, 31, 140, 214, 27, 187 };
    assertEquals(expected.length, ecBytes.length);
    for (int x = 0; x < expected.length; x++) {
      assertEquals(expected[x], ecBytes[x] & 0xFF);
    }
    dataBytes = bytes(32, 49, 205, 69, 42, 20, 0, 236, 17);
    ecBytes = Encoder.generateECBytes(dataBytes, 17);
    expected = new int[] { 0, 3, 130, 179, 194, 0, 55, 211, 110, 79, 98, 72, 170, 96, 211, 137, 213 };
    assertEquals(expected.length, ecBytes.length);
    for (int x = 0; x < expected.length; x++) {
      assertEquals(expected[x], ecBytes[x] & 0xFF);
    }
  }

  @Test public void testBugInBitVectorNumBytes() throws WriterException {
    StringBuilder builder = new StringBuilder(3518);
    for (int x = 0; x < 3518; x++) {
      builder.append('0');
    }
    Encoder.encode(builder.toString(), ErrorCorrectionLevel.L);
  }

  @Test public void testMinimalEncoder1() throws Exception {
    verifyMinimalEncoding("A", "ALPHANUMERIC(A),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder2() throws Exception {
    verifyMinimalEncoding("AB", "ALPHANUMERIC(AB),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder3() throws Exception {
    verifyMinimalEncoding("ABC", "ALPHANUMERIC(AB,C),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder4() throws Exception {
    verifyMinimalEncoding("ABCD", "ALPHANUMERIC(AB,CD),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder5() throws Exception {
    verifyMinimalEncoding("ABCDE", "ALPHANUMERIC(AB,CD,E),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder6() throws Exception {
    verifyMinimalEncoding("ABCDEF", "ALPHANUMERIC(AB,CD,EF),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder7() throws Exception {
    verifyMinimalEncoding("ABCDEFG", "ALPHANUMERIC(AB,CD,EF,G),TERMINATO" + "R()", null, false);
  }

  @Test public void testMinimalEncoder8() throws Exception {
    verifyMinimalEncoding("1", "NUMERIC(1),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder9() throws Exception {
    verifyMinimalEncoding("12", "NUMERIC(12),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder10() throws Exception {
    verifyMinimalEncoding("123", "NUMERIC(123),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder11() throws Exception {
    verifyMinimalEncoding("1234", "NUMERIC(123,4),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder12() throws Exception {
    verifyMinimalEncoding("12345", "NUMERIC(123,45),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder13() throws Exception {
    verifyMinimalEncoding("123456", "NUMERIC(123,456),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder14() throws Exception {
    verifyMinimalEncoding("123A", "ALPHANUMERIC(12,3A),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder15() throws Exception {
    verifyMinimalEncoding("A1", "ALPHANUMERIC(A1),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder16() throws Exception {
    verifyMinimalEncoding("A12", "ALPHANUMERIC(A1,2),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder17() throws Exception {
    verifyMinimalEncoding("A123", "ALPHANUMERIC(A1,23),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder18() throws Exception {
    verifyMinimalEncoding("A1234", "ALPHANUMERIC(A1,23,4),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder19() throws Exception {
    verifyMinimalEncoding("A12345", "ALPHANUMERIC(A1,23,45),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder20() throws Exception {
    verifyMinimalEncoding("A123456", "ALPHANUMERIC(A1,23,45,6),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder21() throws Exception {
    verifyMinimalEncoding("A1234567", "ALPHANUMERIC(A1,23,45,67),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder22() throws Exception {
    verifyMinimalEncoding("A12345678", "BYTE(A),NUMERIC(123,456,78),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder23() throws Exception {
    verifyMinimalEncoding("A123456789", "BYTE(A),NUMERIC(123,456,789),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder24() throws Exception {
    verifyMinimalEncoding("A1234567890", "ALPHANUMERIC(A1),NUMERIC(234,567,890),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder25() throws Exception {
    verifyMinimalEncoding("AB1", "ALPHANUMERIC(AB,1),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder26() throws Exception {
    verifyMinimalEncoding("AB12", "ALPHANUMERIC(AB,12),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder27() throws Exception {
    verifyMinimalEncoding("AB123", "ALPHANUMERIC(AB,12,3),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder28() throws Exception {
    verifyMinimalEncoding("AB1234", "ALPHANUMERIC(AB,12,34),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder29() throws Exception {
    verifyMinimalEncoding("ABC1", "ALPHANUMERIC(AB,C1),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder30() throws Exception {
    verifyMinimalEncoding("ABC12", "ALPHANUMERIC(AB,C1,2),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder31() throws Exception {
    verifyMinimalEncoding("ABC1234", "ALPHANUMERIC(AB,C1,23,4),TERMINA" + "TOR()", null, false);
  }

  @Test public void testMinimalEncoder32() throws Exception {
    verifyMinimalEncoding("http://foo.com", "BYTE(h,t,t,p,:,/,/,f,o,o,.,c,o,m)" + ",TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder33() throws Exception {
    verifyMinimalEncoding("HTTP://FOO.COM", "ALPHANUMERIC(HT,TP,:/,/F,OO,.C,OM" + "),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder34() throws Exception {
    verifyMinimalEncoding("1001114670010%01201220%107211220%140045003267781", "NUMERIC(100,111,467,001,0),ALPHANUMERIC(%0,12,01,22,0%,10,72,11,22,0%),NUMERIC(140,045,003,267,781),TERMINA" + "TOR()", null, false);
  }

  @Test public void testMinimalEncoder35() throws Exception {
    verifyMinimalEncoding("\u0150", "ECI(ISO-8859-2),BYTE(.),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder36() throws Exception {
    verifyMinimalEncoding("\u015c", "ECI(ISO-8859-3),BYTE(.),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder37() throws Exception {
    verifyMinimalEncoding("\u0150\u015c", "ECI(UTF-8),BYTE(.,.),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder38() throws Exception {
    verifyMinimalEncoding("\u0150\u0150\u015c\u015c", "ECI(ISO-8859-2),BYTE(.," + ".),ECI(ISO-8859-3),BYTE(.,.),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder39() throws Exception {
    verifyMinimalEncoding("abcdef\u0150ghij", "ECI(ISO-8859-2),BYTE(a,b,c,d,e," + "f,.,g,h,i,j),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder40() throws Exception {
    verifyMinimalEncoding("2938928329832983\u01502938928329832983\u015c2938928329832983", "NUMERIC(293,892,832,983,298,3),ECI(ISO-8859-2),BYTE(.),NUMERIC(293,892,832,983,298,3),ECI(ISO-8" + "859-3),BYTE(.),NUMERIC(293,892,832,983,298,3),TERMINATOR()", null, false);
  }

  @Test public void testMinimalEncoder41() throws Exception {
    verifyMinimalEncoding("1001114670010%01201220%107211220%140045003267781", "FNC1_FIRST_POSITION(),NUMERIC(100,111" + ",467,001,0),ALPHANUMERIC(%0,12,01,22,0%,10,72,11,22,0%),NUMERIC(140,045,003,267,781),TERMINATOR()", null, true);
  }

  @Test public void testMinimalEncoder42() throws Exception {
    boolean haveCharsets = false;
    try {
      Charset.forName("ISO-8859-6");
      Charset.forName("ISO-8859-8");
      haveCharsets = true;
    } catch (Exception e) {
    }
    if (haveCharsets) {
      verifyMinimalEncoding("that particularly stands out to me is \u0625\u0650\u062c\u064e\u0651\u0627\u0635 (" + "\u02be\u0101\u1e63) \"pear\", suggested to have originated from Hebrew \u05d0\u05b7\u05d2\u05b8" + "\u05bc\u05e1 (ag\u00e1s)", "ECI(ISO-8859-6),BYTE(t,h,a,t, ,p,a,r,t,i,c,u,l,a,r,l,y, ,s,t,a,n,d,s, ,o,u,t," + " ,t,o, ,m,e, ,i,s, ,.,.,.,.,.,.,., ,(),ECI(UTF-8),BYTE(.,.,.,), ,\",p,e,a,r,\",,, ,s,u,g,g,e,s,t,e,d, ,t," + "o, ,h,a,v,e, ,o,r,i,g,i,n,a,t,e,d, ,f,r,o,m, ,H,e,b,r,e,w, ,.,.,.,.,.,., ,(,a,g,.,s,)),TERMINATOR()", null, false);
      verifyMinimalEncoding("that particularly stands out to me is \u0625\u0650\u062c\u064e\u0651\u0627\u0635 (" + "\u02be\u0101\u1e63) \"pear\", suggested to have originated from Hebrew \u05d0\u05b7\u05d2\u05b8" + "\u05bc\u05e1 (ag\u00e1s)", "ECI(UTF-8),BYTE(t,h,a,t, ,p,a,r,t,i,c,u,l,a,r,l,y, ,s,t,a,n,d,s, ,o,u,t, ,t,o" + ", ,m,e, ,i,s, ,.,.,.,.,.,.,., ,(,.,.,.,), ,\",p,e,a,r,\",,, ,s,u,g,g,e,s,t,e,d, ,t,o, ,h,a,v,e, ,o,r,i,g," + "i,n,a,t,e,d, ,f,r,o,m, ,H,e,b,r,e,w, ,.,.,.,.,.,., ,(,a,g,.,s,)),TERMINATOR()", StandardCharsets.UTF_8, false);
    }
  }

  static void verifyMinimalEncoding(String input, String expectedResult, Charset priorityCharset, boolean isGS1) throws Exception {
    MinimalEncoder.ResultList result = MinimalEncoder.encode(input, null, priorityCharset, isGS1);
    assertEquals(result.toString(), expectedResult);
  }

  private static void verifyGS1EncodedData(QRCode qrCode) {
    String expected = "<<\n" + " mode: ALPHANUMERIC\n" + " ecLevel: H\n" + " version: 2\n" + " maskPattern: 2\n" + " matrix:\n" + " 1 1 1 1 1 1 1 0 1 0 1 1 1 1 0 1 1 0 1 1 1 1 1 1 1\n" + " 1 0 0 0 0 0 1 0 1 0 0 0 0 1 1 0 1 0 1 0 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 1 0 1 1 0 1 1 0 0 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 1 0 1 0 1 1 1 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 1 1 1 1 1 1 1 0 1 0 1 1 1 0 1\n" + " 1 0 0 0 0 0 1 0 1 0 0 1 1 1 0 0 0 0 1 0 0 0 0 0 1\n" + " 1 1 1 1 1 1 1 0 1 0 1 0 1 0 1 0 1 0 1 1 1 1 1 1 1\n" + " 0 0 0 0 0 0 0 0 1 1 1 0 0 0 1 1 1 0 0 0 0 0 0 0 0\n" + " 0 0 1 1 1 0 1 0 1 1 1 1 0 1 1 0 1 1 1 1 0 0 1 1 1\n" + " 0 0 0 1 1 1 0 1 0 0 1 0 0 1 0 0 1 1 1 0 0 1 0 0 1\n" + " 1 0 1 1 0 0 1 0 1 1 0 0 0 0 1 0 1 1 1 0 0 1 0 0 1\n" + " 0 0 1 1 0 1 0 1 1 1 1 0 0 1 1 1 1 0 0 0 1 1 0 1 1\n" + " 0 0 1 0 0 0 1 0 0 0 1 1 0 1 0 0 0 1 0 1 1 1 0 1 0\n" + " 1 1 1 0 1 1 0 1 0 0 0 0 0 0 0 1 1 0 1 1 0 1 0 0 0\n" + " 1 0 1 0 1 0 1 1 0 1 0 1 0 1 1 0 0 0 0 0 1 1 0 0 1\n" + " 1 0 0 1 0 1 0 1 0 0 0 1 1 1 1 0 1 0 1 0 0 1 0 0 1\n" + " 1 0 1 0 0 1 1 1 0 1 1 0 0 1 0 0 1 1 1 1 1 1 0 0 0\n" + " 0 0 0 0 0 0 0 0 1 0 0 1 0 1 1 0 1 0 0 0 1 0 0 1 0\n" + " 1 1 1 1 1 1 1 0 0 0 0 1 0 0 1 1 1 0 1 0 1 0 1 1 1\n" + " 1 0 0 0 0 0 1 0 0 1 1 1 1 1 0 1 1 0 0 0 1 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 1 0 1 0 0 1 1 1 1 1 1 1 1 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 1 1 0 0 0 0 0 0 0 0 1 0 1 0 0 0 0\n" + " 1 0 1 1 1 0 1 0 1 0 0 0 1 1 0 1 0 0 1 1 1 0 1 0 1\n" + " 1 0 0 0 0 0 1 0 0 1 0 1 0 1 1 1 0 1 0 0 1 1 1 1 1\n" + " 1 1 1 1 1 1 1 0 0 1 1 0 0 1 1 0 1 0 0 0 0 1 0 1 1\n" + ">>\n";
    assertEquals(expected, qrCode.toString());
  }

  private static void verifyNotGS1EncodedData(QRCode qrCode) {
    String expected = "<<\n" + " mode: ALPHANUMERIC\n" + " ecLevel: H\n" + " version: 1\n" + " maskPattern: 0\n" + " matrix:\n" + " 1 1 1 1 1 1 1 0 1 1 1 1 0 0 1 1 1 1 1 1 1\n" + " 1 0 0 0 0 0 1 0 0 1 1 1 0 0 1 0 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 0 1 1 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 1 1 1 0 1 0 1 0 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 1 1 0 0 1 0 1 1 1 0 1\n" + " 1 0 0 0 0 0 1 0 0 1 0 0 0 0 1 0 0 0 0 0 1\n" + " 1 1 1 1 1 1 1 0 1 0 1 0 1 0 1 1 1 1 1 1 1\n" + " 0 0 0 0 0 0 0 0 0 0 1 0 1 0 0 0 0 0 0 0 0\n" + " 0 0 1 0 1 1 1 0 1 1 0 0 1 1 0 0 0 1 0 0 1\n" + " 1 0 1 1 1 0 0 1 0 0 0 1 0 1 0 0 0 0 0 0 0\n" + " 0 0 1 1 0 0 1 0 1 0 0 0 1 0 1 0 1 0 1 1 0\n" + " 1 1 0 1 0 1 0 1 1 1 0 1 0 1 0 0 0 0 0 1 0\n" + " 0 0 1 1 0 1 1 1 1 0 0 0 1 0 1 0 1 1 1 1 0\n" + " 0 0 0 0 0 0 0 0 1 0 0 1 1 1 0 1 0 1 0 0 0\n" + " 1 1 1 1 1 1 1 0 0 0 1 0 1 0 1 1 0 0 0 0 1\n" + " 1 0 0 0 0 0 1 0 1 1 1 1 0 1 0 1 1 1 1 0 1\n" + " 1 0 1 1 1 0 1 0 1 0 1 1 0 1 0 1 0 0 0 0 1\n" + " 1 0 1 1 1 0 1 0 0 1 1 0 1 1 1 1 0 1 0 1 0\n" + " 1 0 1 1 1 0 1 0 1 0 0 0 1 0 1 0 1 1 1 0 1\n" + " 1 0 0 0 0 0 1 0 0 1 1 0 1 1 0 1 0 0 0 1 1\n" + " 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0 0 1 0 1 0 1\n" + ">>\n";
    assertEquals(expected, qrCode.toString());
  }

  private static String shiftJISString(byte[] bytes) {
    return new String(bytes, StringUtils.SHIFT_JIS_CHARSET);
  }
}