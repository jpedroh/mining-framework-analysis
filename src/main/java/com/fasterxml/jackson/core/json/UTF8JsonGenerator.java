package com.fasterxml.jackson.core.json;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.io.*;

public class UTF8JsonGenerator extends JsonGeneratorImpl {
  private final static byte BYTE_u = (byte) 'u';

  private final static byte BYTE_0 = (byte) '0';

  private final static byte BYTE_LBRACKET = (byte) '[';

  private final static byte BYTE_RBRACKET = (byte) ']';

  private final static byte BYTE_LCURLY = (byte) '{';

  private final static byte BYTE_RCURLY = (byte) '}';

  private final static byte BYTE_BACKSLASH = (byte) '\\';

  private final static byte BYTE_COMMA = (byte) ',';

  private final static byte BYTE_COLON = (byte) ':';

  private final static byte BYTE_QUOTE = (byte) '\"';

  private final static int MAX_BYTES_TO_BUFFER = 512;

  final static byte[] HEX_CHARS = CharTypes.copyHexBytes();

  private final static byte[] NULL_BYTES = { 'n', 'u', 'l', 'l' };

  private final static byte[] TRUE_BYTES = { 't', 'r', 'u', 'e' };

  private final static byte[] FALSE_BYTES = { 'f', 'a', 'l', 's', 'e' };

  /**
     * Underlying output stream used for writing JSON content.
     */
  final protected OutputStream _outputStream;

  /**
     * Intermediate buffer in which contents are buffered before
     * being written using {@link #_outputStream}.
     */
  protected byte[] _outputBuffer;

  /**
     * Pointer to the position right beyond the last character to output
     * (end marker; may be past the buffer)
     */
  protected int _outputTail = 0;

  /**
     * End marker of the output buffer; one past the last valid position
     * within the buffer.
     */
  protected final int _outputEnd;

  /**
     * Maximum number of <code>char</code>s that we know will always fit
     * in the output buffer after escaping
     */
  protected final int _outputMaxContiguous;

  /**
     * Intermediate buffer in which characters of a String are copied
     * before being encoded.
     */
  protected char[] _charBuffer;

  /**
     * Length of <code>_charBuffer</code>
     */
  protected final int _charBufferLength;

  /**
     * 6 character temporary buffer allocated if needed, for constructing
     * escape sequences
     */
  protected byte[] _entityBuffer;

  /**
     * Flag that indicates whether the output buffer is recycable (and
     * needs to be returned to recycler once we are done) or not.
     */
  protected boolean _bufferRecyclable;

  /**
     * Flag that is set if quoting is not to be added around
     * JSON Object property names.
     */
  protected boolean _cfgUnqNames;

  public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out) {
    super(ctxt, features, codec);
    _outputStream = out;
    _bufferRecyclable = true;
    _outputBuffer = ctxt.allocWriteEncodingBuffer();
    _outputEnd = _outputBuffer.length;
    _outputMaxContiguous = _outputEnd >> 3;
    _charBuffer = ctxt.allocConcatBuffer();
    _charBufferLength = _charBuffer.length;
    if (isEnabled(Feature.ESCAPE_NON_ASCII)) {
      setHighestNonEscapedChar(127);
    }
    _cfgUnqNames = !Feature.QUOTE_FIELD_NAMES.enabledIn(features);
  }

  public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, byte[] outputBuffer, int outputOffset, boolean bufferRecyclable) {
    super(ctxt, features, codec);
    _outputStream = out;
    _bufferRecyclable = bufferRecyclable;
    _outputTail = outputOffset;
    _outputBuffer = outputBuffer;
    _outputEnd = _outputBuffer.length;
    _outputMaxContiguous = _outputEnd >> 3;
    _charBuffer = ctxt.allocConcatBuffer();
    _charBufferLength = _charBuffer.length;
    _cfgUnqNames = !Feature.QUOTE_FIELD_NAMES.enabledIn(features);
  }

  @Override public Object getOutputTarget() {
    return _outputStream;
  }

  @Override public void writeFieldName(String name) throws IOException {
    if (_cfgPrettyPrinter != null) {
      _writePPFieldName(name);
      return;
    }
    final int status = _writeContext.writeFieldName(name);
    if (status == JsonWriteContext.STATUS_EXPECT_VALUE) {
      _reportError("Can not write a field name, expecting a value");
    }
    if (status == JsonWriteContext.STATUS_OK_AFTER_COMMA) {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = BYTE_COMMA;
    }
    if (_cfgUnqNames) {
      _writeStringSegments(name, false);
      return;
    }
    final int len = name.length();
    if (len > _charBufferLength) {
      _writeStringSegments(name, true);
      return;
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    name.getChars(0, len, _charBuffer, 0);
    if (len <= _outputMaxContiguous) {
      if ((_outputTail + len) > _outputEnd) {
        _flushBuffer();
      }
      _writeStringSegment(_charBuffer, 0, len);
    } else {
      _writeStringSegments(_charBuffer, 0, len);
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  @Override public void writeFieldName(SerializableString name) throws IOException {
    if (_cfgPrettyPrinter != null) {
      _writePPFieldName(name);
      return;
    }
    final int status = _writeContext.writeFieldName(name.getValue());
    if (status == JsonWriteContext.STATUS_EXPECT_VALUE) {
      _reportError("Can not write a field name, expecting a value");
    }
    if (status == JsonWriteContext.STATUS_OK_AFTER_COMMA) {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = BYTE_COMMA;
    }
    if (_cfgUnqNames) {
      _writeUnq(name);
      return;
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    int len = name.appendQuotedUTF8(_outputBuffer, _outputTail);
    if (len < 0) {
      _writeBytes(name.asQuotedUTF8());
    } else {
      _outputTail += len;
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  private final void _writeUnq(SerializableString name) throws IOException {
    int len = name.appendQuotedUTF8(_outputBuffer, _outputTail);
    if (len < 0) {
      _writeBytes(name.asQuotedUTF8());
    } else {
      _outputTail += len;
    }
  }

  @Override public final void writeStartArray() throws IOException {
    _verifyValueWrite("start an array");
    _writeContext = _writeContext.createChildArrayContext();
    if (_cfgPrettyPrinter != null) {
      _cfgPrettyPrinter.writeStartArray(this);
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = BYTE_LBRACKET;
    }
  }

  @Override public final void writeEndArray() throws IOException {
    if (!_writeContext.inArray()) {
      _reportError("Current context not an ARRAY but " + _writeContext.getTypeDesc());
    }
    if (_cfgPrettyPrinter != null) {
      _cfgPrettyPrinter.writeEndArray(this, _writeContext.getEntryCount());
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = BYTE_RBRACKET;
    }
    _writeContext = _writeContext.getParent();
  }

  @Override public final void writeStartObject() throws IOException {
    _verifyValueWrite("start an object");
    _writeContext = _writeContext.createChildObjectContext();
    if (_cfgPrettyPrinter != null) {
      _cfgPrettyPrinter.writeStartObject(this);
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = BYTE_LCURLY;
    }
  }

  @Override public final void writeEndObject() throws IOException {
    if (!_writeContext.inObject()) {
      _reportError("Current context not an object but " + _writeContext.getTypeDesc());
    }
    if (_cfgPrettyPrinter != null) {
      _cfgPrettyPrinter.writeEndObject(this, _writeContext.getEntryCount());
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = BYTE_RCURLY;
    }
    _writeContext = _writeContext.getParent();
  }

  /**
     * Specialized version of <code>_writeFieldName</code>, off-lined
     * to keep the "fast path" as simple (and hopefully fast) as possible.
     */
  protected final void _writePPFieldName(String name) throws IOException {
    int status = _writeContext.writeFieldName(name);
    if (status == JsonWriteContext.STATUS_EXPECT_VALUE) {
      _reportError("Can not write a field name, expecting a value");
    }
    if ((status == JsonWriteContext.STATUS_OK_AFTER_COMMA)) {
      _cfgPrettyPrinter.writeObjectEntrySeparator(this);
    } else {
      _cfgPrettyPrinter.beforeObjectEntries(this);
    }
    if (_cfgUnqNames) {
      _writeStringSegments(name, false);
      return;
    }
    final int len = name.length();
    if (len > _charBufferLength) {
      _writeStringSegments(name, true);
      return;
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    name.getChars(0, len, _charBuffer, 0);
    if (len <= _outputMaxContiguous) {
      if ((_outputTail + len) > _outputEnd) {
        _flushBuffer();
      }
      _writeStringSegment(_charBuffer, 0, len);
    } else {
      _writeStringSegments(_charBuffer, 0, len);
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  protected final void _writePPFieldName(SerializableString name) throws IOException {
    final int status = _writeContext.writeFieldName(name.getValue());
    if (status == JsonWriteContext.STATUS_EXPECT_VALUE) {
      _reportError("Can not write a field name, expecting a value");
    }
    if (status == JsonWriteContext.STATUS_OK_AFTER_COMMA) {
      _cfgPrettyPrinter.writeObjectEntrySeparator(this);
    } else {
      _cfgPrettyPrinter.beforeObjectEntries(this);
    }
    final boolean addQuotes = !_cfgUnqNames;
    if (addQuotes) {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = BYTE_QUOTE;
    }
    _writeBytes(name.asQuotedUTF8());
    if (addQuotes) {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = BYTE_QUOTE;
    }
  }

  @Override public void writeString(String text) throws IOException {
    _verifyValueWrite(WRITE_STRING);
    if (text == null) {
      _writeNull();
      return;
    }
    final int len = text.length();
    if (len > _charBufferLength) {
      _writeStringSegments(text, true);
      return;
    }
    text.getChars(0, len, _charBuffer, 0);
    if (len > _outputMaxContiguous) {
      _writeLongString(_charBuffer, 0, len);
      return;
    }
    if ((_outputTail + len) >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    _writeStringSegment(_charBuffer, 0, len);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  private void _writeLongString(char[] text, int offset, int len) throws IOException {
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    _writeStringSegments(_charBuffer, 0, len);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  @Override public void writeString(char[] text, int offset, int len) throws IOException {
    _verifyValueWrite(WRITE_STRING);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    if (len <= _outputMaxContiguous) {
      if ((_outputTail + len) > _outputEnd) {
        _flushBuffer();
      }
      _writeStringSegment(text, offset, len);
    } else {
      _writeStringSegments(text, offset, len);
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  @Override public final void writeString(SerializableString text) throws IOException {
    _verifyValueWrite(WRITE_STRING);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    int len = text.appendQuotedUTF8(_outputBuffer, _outputTail);
    if (len < 0) {
      _writeBytes(text.asQuotedUTF8());
    } else {
      _outputTail += len;
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  @Override public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
    _verifyValueWrite(WRITE_STRING);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    _writeBytes(text, offset, length);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  @Override public void writeUTF8String(byte[] text, int offset, int len) throws IOException {
    _verifyValueWrite(WRITE_STRING);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    if (len <= _outputMaxContiguous) {
      _writeUTF8Segment(text, offset, len);
    } else {
      _writeUTF8Segments(text, offset, len);
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  @Override public void writeRaw(String text) throws IOException, JsonGenerationException {
    int start = 0;
    int len = text.length();
    while (len > 0) {
      char[] buf = _charBuffer;
      final int blen = buf.length;
      final int len2 = (len < blen) ? len : blen;
      text.getChars(start, start + len2, buf, 0);
      writeRaw(buf, 0, len2);
      start += len2;
      len -= len2;
    }
  }

  @Override public void writeRaw(String text, int offset, int len) throws IOException, JsonGenerationException {
    while (len > 0) {
      char[] buf = _charBuffer;
      final int blen = buf.length;
      final int len2 = (len < blen) ? len : blen;
      text.getChars(offset, offset + len2, buf, 0);
      writeRaw(buf, 0, len2);
      offset += len2;
      len -= len2;
    }
  }

  @Override public void writeRaw(SerializableString text) throws IOException, JsonGenerationException {
    byte[] raw = text.asUnquotedUTF8();
    if (raw.length > 0) {
      _writeBytes(raw);
    }
  }

  @Override public void writeRawValue(SerializableString text) throws IOException {
    _verifyValueWrite(WRITE_RAW);
    byte[] raw = text.asUnquotedUTF8();
    if (raw.length > 0) {
      _writeBytes(raw);
    }
  }

  @Override public final void writeRaw(char[] cbuf, int offset, int len) throws IOException, JsonGenerationException {
    {
      int len3 = len + len + len;
      if ((_outputTail + len3) > _outputEnd) {
        if (_outputEnd < len3) {
          _writeSegmentedRaw(cbuf, offset, len);
          return;
        }
        _flushBuffer();
      }
    }
    len += offset;
    main_loop:
    while (offset < len) {
      inner_loop:
      while (true) {
        int ch = (int) cbuf[offset];
        if (ch > 0x7F) {
          break inner_loop;
        }
        _outputBuffer[_outputTail++] = (byte) ch;
        if (++offset >= len) {
          break main_loop;
        }
      }
      char ch = cbuf[offset++];
      if (ch < 0x800) {
        _outputBuffer[_outputTail++] = (byte) (0xc0 | (ch >> 6));
        _outputBuffer[_outputTail++] = (byte) (0x80 | (ch & 0x3f));
      } else {
        offset = _outputRawMultiByteChar(ch, cbuf, offset, len);
      }
    }
  }

  @Override public void writeRaw(char ch) throws IOException, JsonGenerationException {
    if ((_outputTail + 3) >= _outputEnd) {
      _flushBuffer();
    }
    final byte[] bbuf = _outputBuffer;
    if (ch <= 0x7F) {
      bbuf[_outputTail++] = (byte) ch;
    } else {
      if (ch < 0x800) {
        bbuf[_outputTail++] = (byte) (0xc0 | (ch >> 6));
        bbuf[_outputTail++] = (byte) (0x80 | (ch & 0x3f));
      } else {
        _outputRawMultiByteChar(ch, null, 0, 0);
      }
    }
  }

  /**
     * Helper method called when it is possible that output of raw section
     * to output may cross buffer boundary
     */
  private final void _writeSegmentedRaw(char[] cbuf, int offset, int len) throws IOException, JsonGenerationException {
    final int end = _outputEnd;
    final byte[] bbuf = _outputBuffer;
    main_loop:
    while (offset < len) {
      inner_loop:
      while (true) {
        int ch = (int) cbuf[offset];
        if (ch >= 0x80) {
          break inner_loop;
        }
        if (_outputTail >= end) {
          _flushBuffer();
        }
        bbuf[_outputTail++] = (byte) ch;
        if (++offset >= len) {
          break main_loop;
        }
      }
      if ((_outputTail + 3) >= _outputEnd) {
        _flushBuffer();
      }
      char ch = cbuf[offset++];
      if (ch < 0x800) {
        bbuf[_outputTail++] = (byte) (0xc0 | (ch >> 6));
        bbuf[_outputTail++] = (byte) (0x80 | (ch & 0x3f));
      } else {
        offset = _outputRawMultiByteChar(ch, cbuf, offset, len);
      }
    }
  }

  @Override public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
    _verifyValueWrite(WRITE_BINARY);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    _writeBinary(b64variant, data, offset, offset + len);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  @Override public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException, JsonGenerationException {
    _verifyValueWrite(WRITE_BINARY);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    byte[] encodingBuffer = _ioContext.allocBase64Buffer();
    int bytes;
    try {
      if (dataLength < 0) {
        bytes = _writeBinary(b64variant, data, encodingBuffer);
      } else {
        int missing = _writeBinary(b64variant, data, encodingBuffer, dataLength);
        if (missing > 0) {
          _reportError("Too few bytes available: missing " + missing + " bytes (out of " + dataLength + ")");
        }
        bytes = dataLength;
      }
    }  finally {
      _ioContext.releaseBase64Buffer(encodingBuffer);
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    return bytes;
  }

  @Override public void writeNumber(short s) throws IOException, JsonGenerationException {
    _verifyValueWrite(WRITE_NUMBER);
    if ((_outputTail + 6) >= _outputEnd) {
      _flushBuffer();
    }
    if (_cfgNumbersAsStrings) {
      _writeQuotedShort(s);
      return;
    }
    _outputTail = NumberOutput.outputInt(s, _outputBuffer, _outputTail);
  }

  private final void _writeQuotedShort(short s) throws IOException {
    if ((_outputTail + 8) >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    _outputTail = NumberOutput.outputInt(s, _outputBuffer, _outputTail);
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  @Override public void writeNumber(int i) throws IOException, JsonGenerationException {
    _verifyValueWrite(WRITE_NUMBER);
    if ((_outputTail + 11) >= _outputEnd) {
      _flushBuffer();
    }
    if (_cfgNumbersAsStrings) {
      _writeQuotedInt(i);
      return;
    }
    _outputTail = NumberOutput.outputInt(i, _outputBuffer, _outputTail);
  }

  private final void _writeQuotedInt(int i) throws IOException {
    if ((_outputTail + 13) >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    _outputTail = NumberOutput.outputInt(i, _outputBuffer, _outputTail);
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  @Override public void writeNumber(long l) throws IOException, JsonGenerationException {
    _verifyValueWrite(WRITE_NUMBER);
    if (_cfgNumbersAsStrings) {
      _writeQuotedLong(l);
      return;
    }
    if ((_outputTail + 21) >= _outputEnd) {
      _flushBuffer();
    }
    _outputTail = NumberOutput.outputLong(l, _outputBuffer, _outputTail);
  }

  private final void _writeQuotedLong(long l) throws IOException {
    if ((_outputTail + 23) >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    _outputTail = NumberOutput.outputLong(l, _outputBuffer, _outputTail);
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  @Override public void writeNumber(BigInteger value) throws IOException, JsonGenerationException {
    _verifyValueWrite(WRITE_NUMBER);
    if (value == null) {
      _writeNull();
    } else {
      if (_cfgNumbersAsStrings) {
        _writeQuotedRaw(value.toString());
      } else {
        writeRaw(value.toString());
      }
    }
  }

  @Override public void writeNumber(double d) throws IOException, JsonGenerationException {
    if (_cfgNumbersAsStrings || (((Double.isNaN(d) || Double.isInfinite(d)) && isEnabled(Feature.QUOTE_NON_NUMERIC_NUMBERS)))) {
      writeString(String.valueOf(d));
      return;
    }
    _verifyValueWrite(WRITE_NUMBER);
    writeRaw(String.valueOf(d));
  }

  @Override public void writeNumber(float f) throws IOException, JsonGenerationException {
    if (_cfgNumbersAsStrings || (((Float.isNaN(f) || Float.isInfinite(f)) && isEnabled(Feature.QUOTE_NON_NUMERIC_NUMBERS)))) {
      writeString(String.valueOf(f));
      return;
    }
    _verifyValueWrite(WRITE_NUMBER);
    writeRaw(String.valueOf(f));
  }

  @Override public void writeNumber(BigDecimal value) throws IOException, JsonGenerationException {
    _verifyValueWrite(WRITE_NUMBER);
    if (value == null) {
      _writeNull();
    } else {
      if (_cfgNumbersAsStrings) {
        String raw = isEnabled(Feature.WRITE_BIGDECIMAL_AS_PLAIN) ? value.toPlainString() : value.toString();
        _writeQuotedRaw(raw);
      } else {
        if (isEnabled(Feature.WRITE_BIGDECIMAL_AS_PLAIN)) {
          writeRaw(value.toPlainString());
        } else {
          writeRaw(value.toString());
        }
      }
    }
  }

  @Override public void writeNumber(String encodedValue) throws IOException, JsonGenerationException {
    _verifyValueWrite(WRITE_NUMBER);
    if (_cfgNumbersAsStrings) {
      _writeQuotedRaw(encodedValue);
    } else {
      writeRaw(encodedValue);
    }
  }

  private final void _writeQuotedRaw(String value) throws IOException {
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
    writeRaw(value);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = BYTE_QUOTE;
  }

  @Override public void writeBoolean(boolean state) throws IOException, JsonGenerationException {
    _verifyValueWrite(WRITE_BOOLEAN);
    if ((_outputTail + 5) >= _outputEnd) {
      _flushBuffer();
    }
    byte[] keyword = state ? TRUE_BYTES : FALSE_BYTES;
    int len = keyword.length;
    System.arraycopy(keyword, 0, _outputBuffer, _outputTail, len);
    _outputTail += len;
  }

  @Override public void writeNull() throws IOException, JsonGenerationException {
    _verifyValueWrite(WRITE_NULL);
    _writeNull();
  }

  @Override protected final void _verifyValueWrite(String typeMsg) throws IOException {
    int status = _writeContext.writeValue();
    if (status == JsonWriteContext.STATUS_EXPECT_NAME) {
      _reportError("Can not " + typeMsg + ", expecting field name");
    }
    if (_cfgPrettyPrinter == null) {
      byte b;
      switch (status) {
        case JsonWriteContext.STATUS_OK_AFTER_COMMA:
        b = BYTE_COMMA;
        break;
        case JsonWriteContext.STATUS_OK_AFTER_COLON:
        b = BYTE_COLON;
        break;
        case JsonWriteContext.STATUS_OK_AFTER_SPACE:
        if (_rootValueSeparator != null) {
          byte[] raw = _rootValueSeparator.asUnquotedUTF8();
          if (raw.length > 0) {
            _writeBytes(raw);
          }
        }
        return;
        case JsonWriteContext.STATUS_OK_AS_IS:
        default:
        return;
      }
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail] = b;
      ++_outputTail;
      return;
    }
    _verifyPrettyValueWrite(typeMsg, status);
  }

  protected final void _verifyPrettyValueWrite(String typeMsg, int status) throws IOException {
    switch (status) {
      case JsonWriteContext.STATUS_OK_AFTER_COMMA:
      _cfgPrettyPrinter.writeArrayValueSeparator(this);
      break;
      case JsonWriteContext.STATUS_OK_AFTER_COLON:
      _cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
      break;
      case JsonWriteContext.STATUS_OK_AFTER_SPACE:
      _cfgPrettyPrinter.writeRootValueSeparator(this);
      break;
      case JsonWriteContext.STATUS_OK_AS_IS:
      if (_writeContext.inArray()) {
        _cfgPrettyPrinter.beforeArrayValues(this);
      } else {
        if (_writeContext.inObject()) {
          _cfgPrettyPrinter.beforeObjectEntries(this);
        }
      }
      break;
      default:
      _throwInternal();
      break;
    }
  }

  @Override public void flush() throws IOException {
    _flushBuffer();
    if (_outputStream != null) {
      if (isEnabled(Feature.FLUSH_PASSED_TO_STREAM)) {
        _outputStream.flush();
      }
    }
  }

  @Override public void close() throws IOException {
    super.close();
    if (_outputBuffer != null && isEnabled(Feature.AUTO_CLOSE_JSON_CONTENT)) {
      while (true) {
        JsonStreamContext ctxt = getOutputContext();
        if (ctxt.inArray()) {
          writeEndArray();
        } else {
          if (ctxt.inObject()) {
            writeEndObject();
          } else {
            break;
          }
        }
      }
    }
    _flushBuffer();
    if (_outputStream != null) {
      if (_ioContext.isResourceManaged() || isEnabled(Feature.AUTO_CLOSE_TARGET)) {
        _outputStream.close();
      } else {
        if (isEnabled(Feature.FLUSH_PASSED_TO_STREAM)) {
          _outputStream.flush();
        }
      }
    }
    _releaseBuffers();
  }

  @Override protected void _releaseBuffers() {
    byte[] buf = _outputBuffer;
    if (buf != null && _bufferRecyclable) {
      _outputBuffer = null;
      _ioContext.releaseWriteEncodingBuffer(buf);
    }
    char[] cbuf = _charBuffer;
    if (cbuf != null) {
      _charBuffer = null;
      _ioContext.releaseConcatBuffer(cbuf);
    }
  }

  private final void _writeBytes(byte[] bytes) throws IOException {
    final int len = bytes.length;
    if ((_outputTail + len) > _outputEnd) {
      _flushBuffer();
      if (len > MAX_BYTES_TO_BUFFER) {
        _outputStream.write(bytes, 0, len);
        return;
      }
    }
    System.arraycopy(bytes, 0, _outputBuffer, _outputTail, len);
    _outputTail += len;
  }

  private final void _writeBytes(byte[] bytes, int offset, int len) throws IOException {
    if ((_outputTail + len) > _outputEnd) {
      _flushBuffer();
      if (len > MAX_BYTES_TO_BUFFER) {
        _outputStream.write(bytes, offset, len);
        return;
      }
    }
    System.arraycopy(bytes, offset, _outputBuffer, _outputTail, len);
    _outputTail += len;
  }

  /**
     * Method called when String to write is long enough not to fit
     * completely in temporary copy buffer. If so, we will actually
     * copy it in small enough chunks so it can be directly fed
     * to single-segment writes (instead of maximum slices that
     * would fit in copy buffer)
     */
  private final void _writeStringSegments(String text, boolean addQuotes) throws IOException {
    if (addQuotes) {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = BYTE_QUOTE;
    }
    int left = text.length();
    int offset = 0;
    final char[] cbuf = _charBuffer;
    while (left > 0) {
      int len = Math.min(_outputMaxContiguous, left);
      text.getChars(offset, offset + len, cbuf, 0);
      if ((_outputTail + len) > _outputEnd) {
        _flushBuffer();
      }
      _writeStringSegment(cbuf, 0, len);
      offset += len;
      left -= len;
    }
    if (addQuotes) {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = BYTE_QUOTE;
    }
  }

  /**
     * Method called when character sequence to write is long enough that
     * its maximum encoded and escaped form is not guaranteed to fit in
     * the output buffer. If so, we will need to choose smaller output
     * chunks to write at a time.
     */
  private final void _writeStringSegments(char[] cbuf, int offset, int totalLen) throws IOException, JsonGenerationException {
    do {
      int len = Math.min(_outputMaxContiguous, totalLen);
      if ((_outputTail + len) > _outputEnd) {
        _flushBuffer();
      }
      _writeStringSegment(cbuf, offset, len);
      offset += len;
      totalLen -= len;
    } while(totalLen > 0);
  }

  /**
     * This method called when the string content is already in
     * a char buffer, and its maximum total encoded and escaped length
     * can not exceed size of the output buffer.
     * Caller must ensure that there is enough space in output buffer,
     * assuming case of all non-escaped ASCII characters, as well as
     * potentially enough space for other cases (but not necessarily flushed)
     */
  private final void _writeStringSegment(char[] cbuf, int offset, int len) throws IOException, JsonGenerationException {
    len += offset;
    int outputPtr = _outputTail;
    final byte[] outputBuffer = _outputBuffer;
    final int[] escCodes = _outputEscapes;
    while (offset < len) {
      int ch = cbuf[offset];
      if (ch > 0x7F || escCodes[ch] != 0) {
        break;
      }
      outputBuffer[outputPtr++] = (byte) ch;
      ++offset;
    }
    _outputTail = outputPtr;
    if (offset < len) {
      if (_characterEscapes != null) {
        _writeCustomStringSegment2(cbuf, offset, len);
      } else {
        if (_maximumNonEscapedChar == 0) {
          _writeStringSegment2(cbuf, offset, len);
        } else {
          _writeStringSegmentASCII2(cbuf, offset, len);
        }
      }
    }
  }

  /**
     * Secondary method called when content contains characters to escape,
     * and/or multi-byte UTF-8 characters.
     */
  private final void _writeStringSegment2(final char[] cbuf, int offset, final int end) throws IOException, JsonGenerationException {
    if ((_outputTail + 6 * (end - offset)) > _outputEnd) {
      _flushBuffer();
    }
    int outputPtr = _outputTail;
    final byte[] outputBuffer = _outputBuffer;
    final int[] escCodes = _outputEscapes;
    while (offset < end) {
      int ch = cbuf[offset++];
      if (ch <= 0x7F) {
        if (escCodes[ch] == 0) {
          outputBuffer[outputPtr++] = (byte) ch;
          continue;
        }
        int escape = escCodes[ch];
        if (escape > 0) {
          outputBuffer[outputPtr++] = BYTE_BACKSLASH;
          outputBuffer[outputPtr++] = (byte) escape;
        } else {
          outputPtr = _writeGenericEscape(ch, outputPtr);
        }
        continue;
      }
      if (ch <= 0x7FF) {
        outputBuffer[outputPtr++] = (byte) (0xc0 | (ch >> 6));
        outputBuffer[outputPtr++] = (byte) (0x80 | (ch & 0x3f));
      } else {
        outputPtr = _outputMultiByteChar(ch, outputPtr);
      }
    }
    _outputTail = outputPtr;
  }

  /**
     * Same as <code>_writeStringSegment2(char[], ...)</code., but with
     * additional escaping for high-range code points
     */
  private final void _writeStringSegmentASCII2(final char[] cbuf, int offset, final int end) throws IOException, JsonGenerationException {
    if ((_outputTail + 6 * (end - offset)) > _outputEnd) {
      _flushBuffer();
    }
    int outputPtr = _outputTail;
    final byte[] outputBuffer = _outputBuffer;
    final int[] escCodes = _outputEscapes;
    final int maxUnescaped = _maximumNonEscapedChar;
    while (offset < end) {
      int ch = cbuf[offset++];
      if (ch <= 0x7F) {
        if (escCodes[ch] == 0) {
          outputBuffer[outputPtr++] = (byte) ch;
          continue;
        }
        int escape = escCodes[ch];
        if (escape > 0) {
          outputBuffer[outputPtr++] = BYTE_BACKSLASH;
          outputBuffer[outputPtr++] = (byte) escape;
        } else {
          outputPtr = _writeGenericEscape(ch, outputPtr);
        }
        continue;
      }
      if (ch > maxUnescaped) {
        outputPtr = _writeGenericEscape(ch, outputPtr);
        continue;
      }
      if (ch <= 0x7FF) {
        outputBuffer[outputPtr++] = (byte) (0xc0 | (ch >> 6));
        outputBuffer[outputPtr++] = (byte) (0x80 | (ch & 0x3f));
      } else {
        outputPtr = _outputMultiByteChar(ch, outputPtr);
      }
    }
    _outputTail = outputPtr;
  }

  /**
     * Same as <code>_writeStringSegmentASCII2(char[], ...)</code., but with
     * additional checking for completely custom escapes
     */
  private final void _writeCustomStringSegment2(final char[] cbuf, int offset, final int end) throws IOException, JsonGenerationException {
    if ((_outputTail + 6 * (end - offset)) > _outputEnd) {
      _flushBuffer();
    }
    int outputPtr = _outputTail;
    final byte[] outputBuffer = _outputBuffer;
    final int[] escCodes = _outputEscapes;
    final int maxUnescaped = (_maximumNonEscapedChar <= 0) ? 0xFFFF : _maximumNonEscapedChar;
    final CharacterEscapes customEscapes = _characterEscapes;
    while (offset < end) {
      int ch = cbuf[offset++];
      if (ch <= 0x7F) {
        if (escCodes[ch] == 0) {
          outputBuffer[outputPtr++] = (byte) ch;
          continue;
        }
        int escape = escCodes[ch];
        if (escape > 0) {
          outputBuffer[outputPtr++] = BYTE_BACKSLASH;
          outputBuffer[outputPtr++] = (byte) escape;
        } else {
          if (escape == CharacterEscapes.ESCAPE_CUSTOM) {
            SerializableString esc = customEscapes.getEscapeSequence(ch);
            if (esc == null) {
              _reportError("Invalid custom escape definitions; custom escape not found for character code 0x" + Integer.toHexString(ch) + ", although was supposed to have one");
            }
            outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
          } else {
            outputPtr = _writeGenericEscape(ch, outputPtr);
          }
        }
        continue;
      }
      if (ch > maxUnescaped) {
        outputPtr = _writeGenericEscape(ch, outputPtr);
        continue;
      }
      SerializableString esc = customEscapes.getEscapeSequence(ch);
      if (esc != null) {
        outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
        continue;
      }
      if (ch <= 0x7FF) {
        outputBuffer[outputPtr++] = (byte) (0xc0 | (ch >> 6));
        outputBuffer[outputPtr++] = (byte) (0x80 | (ch & 0x3f));
      } else {
        outputPtr = _outputMultiByteChar(ch, outputPtr);
      }
    }
    _outputTail = outputPtr;
  }

  private final int _writeCustomEscape(byte[] outputBuffer, int outputPtr, SerializableString esc, int remainingChars) throws IOException, JsonGenerationException {
    byte[] raw = esc.asUnquotedUTF8();
    int len = raw.length;
    if (len > 6) {
      return _handleLongCustomEscape(outputBuffer, outputPtr, _outputEnd, raw, remainingChars);
    }
    System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
    return (outputPtr + len);
  }

  private final int _handleLongCustomEscape(byte[] outputBuffer, int outputPtr, int outputEnd, byte[] raw, int remainingChars) throws IOException, JsonGenerationException {
    int len = raw.length;
    if ((outputPtr + len) > outputEnd) {
      _outputTail = outputPtr;
      _flushBuffer();
      outputPtr = _outputTail;
      if (len > outputBuffer.length) {
        _outputStream.write(raw, 0, len);
        return outputPtr;
      }
      System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
      outputPtr += len;
    }
    if ((outputPtr + 6 * remainingChars) > outputEnd) {
      _flushBuffer();
      return _outputTail;
    }
    return outputPtr;
  }

  /**
     * Method called when UTF-8 encoded (but NOT yet escaped!) content is not guaranteed
     * to fit in the output buffer after escaping; as such, we just need to
     * chunk writes.
     */
  private final void _writeUTF8Segments(byte[] utf8, int offset, int totalLen) throws IOException, JsonGenerationException {
    do {
      int len = Math.min(_outputMaxContiguous, totalLen);
      _writeUTF8Segment(utf8, offset, len);
      offset += len;
      totalLen -= len;
    } while(totalLen > 0);
  }

  private final void _writeUTF8Segment(byte[] utf8, final int offset, final int len) throws IOException, JsonGenerationException {
    final int[] escCodes = _outputEscapes;
    for (int ptr = offset, end = offset + len; ptr < end; ) {
      int ch = utf8[ptr++];
      if ((ch >= 0) && escCodes[ch] != 0) {
        _writeUTF8Segment2(utf8, offset, len);
        return;
      }
    }
    if ((_outputTail + len) > _outputEnd) {
      _flushBuffer();
    }
    System.arraycopy(utf8, offset, _outputBuffer, _outputTail, len);
    _outputTail += len;
  }

  private final void _writeUTF8Segment2(final byte[] utf8, int offset, int len) throws IOException, JsonGenerationException {
    int outputPtr = _outputTail;
    if ((outputPtr + (len * 6)) > _outputEnd) {
      _flushBuffer();
      outputPtr = _outputTail;
    }
    final byte[] outputBuffer = _outputBuffer;
    final int[] escCodes = _outputEscapes;
    len += offset;
    while (offset < len) {
      byte b = utf8[offset++];
      int ch = b;
      if (ch < 0 || escCodes[ch] == 0) {
        outputBuffer[outputPtr++] = b;
        continue;
      }
      int escape = escCodes[ch];
      if (escape > 0) {
        outputBuffer[outputPtr++] = BYTE_BACKSLASH;
        outputBuffer[outputPtr++] = (byte) escape;
      } else {
        outputPtr = _writeGenericEscape(ch, outputPtr);
      }
    }
    _outputTail = outputPtr;
  }

  protected final void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, final int inputEnd) throws IOException, JsonGenerationException {
    int safeInputEnd = inputEnd - 3;
    int safeOutputEnd = _outputEnd - 6;
    int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
    while (inputPtr <= safeInputEnd) {
      if (_outputTail > safeOutputEnd) {
        _flushBuffer();
      }
      int b24 = ((int) input[inputPtr++]) << 8;
      b24 |= ((int) input[inputPtr++]) & 0xFF;
      b24 = (b24 << 8) | (((int) input[inputPtr++]) & 0xFF);
      _outputTail = b64variant.encodeBase64Chunk(b24, _outputBuffer, _outputTail);
      if (--chunksBeforeLF <= 0) {
        _outputBuffer[_outputTail++] = '\\';
        _outputBuffer[_outputTail++] = 'n';
        chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
      }
    }
    int inputLeft = inputEnd - inputPtr;
    if (inputLeft > 0) {
      if (_outputTail > safeOutputEnd) {
        _flushBuffer();
      }
      int b24 = ((int) input[inputPtr++]) << 16;
      if (inputLeft == 2) {
        b24 |= (((int) input[inputPtr++]) & 0xFF) << 8;
      }
      _outputTail = b64variant.encodeBase64Partial(b24, inputLeft, _outputBuffer, _outputTail);
    }
  }

  protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer, int bytesLeft) throws IOException, JsonGenerationException {
    int inputPtr = 0;
    int inputEnd = 0;
    int lastFullOffset = -3;
    int safeOutputEnd = _outputEnd - 6;
    int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
    while (bytesLeft > 2) {
      if (inputPtr > lastFullOffset) {
        inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
        inputPtr = 0;
        if (inputEnd < 3) {
          break;
        }
        lastFullOffset = inputEnd - 3;
      }
      if (_outputTail > safeOutputEnd) {
        _flushBuffer();
      }
      int b24 = ((int) readBuffer[inputPtr++]) << 8;
      b24 |= ((int) readBuffer[inputPtr++]) & 0xFF;
      b24 = (b24 << 8) | (((int) readBuffer[inputPtr++]) & 0xFF);
      bytesLeft -= 3;
      _outputTail = b64variant.encodeBase64Chunk(b24, _outputBuffer, _outputTail);
      if (--chunksBeforeLF <= 0) {
        _outputBuffer[_outputTail++] = '\\';
        _outputBuffer[_outputTail++] = 'n';
        chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
      }
    }
    if (bytesLeft > 0) {
      inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, bytesLeft);
      inputPtr = 0;
      if (inputEnd > 0) {
        if (_outputTail > safeOutputEnd) {
          _flushBuffer();
        }
        int b24 = ((int) readBuffer[inputPtr++]) << 16;
        int amount;
        if (inputPtr < inputEnd) {
          b24 |= (((int) readBuffer[inputPtr]) & 0xFF) << 8;
          amount = 2;
        } else {
          amount = 1;
        }
        _outputTail = b64variant.encodeBase64Partial(b24, amount, _outputBuffer, _outputTail);
        bytesLeft -= amount;
      }
    }
    return bytesLeft;
  }

  protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer) throws IOException, JsonGenerationException {
    int inputPtr = 0;
    int inputEnd = 0;
    int lastFullOffset = -3;
    int bytesDone = 0;
    int safeOutputEnd = _outputEnd - 6;
    int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
    while (true) {
      if (inputPtr > lastFullOffset) {
        inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, readBuffer.length);
        inputPtr = 0;
        if (inputEnd < 3) {
          break;
        }
        lastFullOffset = inputEnd - 3;
      }
      if (_outputTail > safeOutputEnd) {
        _flushBuffer();
      }
      int b24 = ((int) readBuffer[inputPtr++]) << 8;
      b24 |= ((int) readBuffer[inputPtr++]) & 0xFF;
      b24 = (b24 << 8) | (((int) readBuffer[inputPtr++]) & 0xFF);
      bytesDone += 3;
      _outputTail = b64variant.encodeBase64Chunk(b24, _outputBuffer, _outputTail);
      if (--chunksBeforeLF <= 0) {
        _outputBuffer[_outputTail++] = '\\';
        _outputBuffer[_outputTail++] = 'n';
        chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
      }
    }
    if (inputPtr < inputEnd) {
      if (_outputTail > safeOutputEnd) {
        _flushBuffer();
      }
      int b24 = ((int) readBuffer[inputPtr++]) << 16;
      int amount = 1;
      if (inputPtr < inputEnd) {
        b24 |= (((int) readBuffer[inputPtr]) & 0xFF) << 8;
        amount = 2;
      }
      bytesDone += amount;
      _outputTail = b64variant.encodeBase64Partial(b24, amount, _outputBuffer, _outputTail);
    }
    return bytesDone;
  }

  private final int _readMore(InputStream in, byte[] readBuffer, int inputPtr, int inputEnd, int maxRead) throws IOException {
    int i = 0;
    while (inputPtr < inputEnd) {
      readBuffer[i++] = readBuffer[inputPtr++];
    }
    inputPtr = 0;
    inputEnd = i;
    maxRead = Math.min(maxRead, readBuffer.length);
    do {
      int length = maxRead - inputEnd;
      if (length == 0) {
        break;
      }
      int count = in.read(readBuffer, inputEnd, length);
      if (count < 0) {
        return inputEnd;
      }
      inputEnd += count;
    } while(inputEnd < 3);
    return inputEnd;
  }

  /**
     * Method called to output a character that is beyond range of
     * 1- and 2-byte UTF-8 encodings, when outputting "raw" 
     * text (meaning it is not to be escaped or quoted)
     */
  private final int _outputRawMultiByteChar(int ch, char[] cbuf, int inputOffset, int inputLen) throws IOException {
    if (ch >= SURR1_FIRST) {
      if (ch <= SURR2_LAST) {
        if (inputOffset >= inputLen || cbuf == null) {
          _reportError("Split surrogate on writeRaw() input (last character)");
        }
        _outputSurrogates(ch, cbuf[inputOffset]);
        return (inputOffset + 1);
      }
    }
    final byte[] bbuf = _outputBuffer;
    bbuf[_outputTail++] = (byte) (0xe0 | (ch >> 12));
    bbuf[_outputTail++] = (byte) (0x80 | ((ch >> 6) & 0x3f));
    bbuf[_outputTail++] = (byte) (0x80 | (ch & 0x3f));
    return inputOffset;
  }

  protected final void _outputSurrogates(int surr1, int surr2) throws IOException {
    int c = _decodeSurrogate(surr1, surr2);
    if ((_outputTail + 4) > _outputEnd) {
      _flushBuffer();
    }
    final byte[] bbuf = _outputBuffer;
    bbuf[_outputTail++] = (byte) (0xf0 | (c >> 18));
    bbuf[_outputTail++] = (byte) (0x80 | ((c >> 12) & 0x3f));
    bbuf[_outputTail++] = (byte) (0x80 | ((c >> 6) & 0x3f));
    bbuf[_outputTail++] = (byte) (0x80 | (c & 0x3f));
  }

  /**
     * 
     * @param ch
     * @param outputPtr Position within output buffer to append multi-byte in
     * 
     * @return New output position after appending
     * 
     * @throws IOException
     */
  private final int _outputMultiByteChar(int ch, int outputPtr) throws IOException {
    byte[] bbuf = _outputBuffer;
    if (ch >= SURR1_FIRST && ch <= SURR2_LAST) {
      bbuf[outputPtr++] = BYTE_BACKSLASH;
      bbuf[outputPtr++] = BYTE_u;
      bbuf[outputPtr++] = HEX_CHARS[(ch >> 12) & 0xF];
      bbuf[outputPtr++] = HEX_CHARS[(ch >> 8) & 0xF];
      bbuf[outputPtr++] = HEX_CHARS[(ch >> 4) & 0xF];
      bbuf[outputPtr++] = HEX_CHARS[ch & 0xF];
    } else {
      bbuf[outputPtr++] = (byte) (0xe0 | (ch >> 12));
      bbuf[outputPtr++] = (byte) (0x80 | ((ch >> 6) & 0x3f));
      bbuf[outputPtr++] = (byte) (0x80 | (ch & 0x3f));
    }
    return outputPtr;
  }

  private final void _writeNull() throws IOException {
    if ((_outputTail + 4) >= _outputEnd) {
      _flushBuffer();
    }
    System.arraycopy(NULL_BYTES, 0, _outputBuffer, _outputTail, 4);
    _outputTail += 4;
  }

  /**
     * Method called to write a generic Unicode escape for given character.
     * 
     * @param charToEscape Character to escape using escape sequence (\\uXXXX)
     */
  private int _writeGenericEscape(int charToEscape, int outputPtr) throws IOException {
    final byte[] bbuf = _outputBuffer;
    bbuf[outputPtr++] = BYTE_BACKSLASH;
    bbuf[outputPtr++] = BYTE_u;
    if (charToEscape > 0xFF) {
      int hi = (charToEscape >> 8) & 0xFF;
      bbuf[outputPtr++] = HEX_CHARS[hi >> 4];
      bbuf[outputPtr++] = HEX_CHARS[hi & 0xF];
      charToEscape &= 0xFF;
    } else {
      bbuf[outputPtr++] = BYTE_0;
      bbuf[outputPtr++] = BYTE_0;
    }
    bbuf[outputPtr++] = HEX_CHARS[charToEscape >> 4];
    bbuf[outputPtr++] = HEX_CHARS[charToEscape & 0xF];
    return outputPtr;
  }

  protected final void _flushBuffer() throws IOException {
    int len = _outputTail;
    if (len > 0) {
      _outputTail = 0;
      _outputStream.write(_outputBuffer, 0, len);
    }
  }
}