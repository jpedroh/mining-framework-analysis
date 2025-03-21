package com.fasterxml.jackson.core.json;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.NumberOutput;

/**
 * {@link JsonGenerator} that outputs JSON content using a {@link java.io.Writer}
 * which handles character encoding.
 */
public class WriterBasedJsonGenerator extends JsonGeneratorBase {
  protected final static int SHORT_WRITE = 32;

  protected final static char[] HEX_CHARS = CharTypes.copyHexChars();

  protected final Writer _writer;

  /**
     * Character used for quoting JSON Object property names
     * and String values.
     */
  protected final char _quoteChar;

  /**
     * Intermediate buffer in which contents are buffered before
     * being written using {@link #_writer}.
     */
  protected char[] _outputBuffer;

  /**
     * Pointer to the first buffered character to output
     */
  protected int _outputHead;

  /**
     * Pointer to the position right beyond the last character to output
     * (end marker; may point to position right beyond the end of the buffer)
     */
  protected int _outputTail;

  /**
     * End marker of the output buffer; one past the last valid position
     * within the buffer.
     */
  protected int _outputEnd;

  /**
     * Short (14 char) temporary buffer allocated if needed, for constructing
     * escape sequences
     */
  protected char[] _entityBuffer;

  /**
     * When custom escapes are used, this member variable is used
     * internally to hold a reference to currently used escape
     */
  protected SerializableString _currentEscape;

  /**
     * Intermediate buffer in which characters of a String are copied
     * before being encoded.
     */
  protected char[] _copyBuffer;

  public WriterBasedJsonGenerator(ObjectWriteContext writeCtxt, IOContext ioCtxt, int streamWriteFeatures, int formatWriteFeatures, Writer w, SerializableString rootValueSep, PrettyPrinter pp, CharacterEscapes charEsc, int maxNonEscaped, char quoteChar) {
    super(writeCtxt, ioCtxt, streamWriteFeatures, formatWriteFeatures, rootValueSep, pp, charEsc, maxNonEscaped);
    _writer = w;
    _outputBuffer = ioCtxt.allocConcatBuffer();
    _outputEnd = _outputBuffer.length;
    _quoteChar = quoteChar;
    setCharacterEscapes(charEsc);
  }

  @Override public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
    _characterEscapes = esc;
    if (esc == null) {
      _outputEscapes = (_quoteChar == '\"') ? DEFAULT_OUTPUT_ESCAPES : CharTypes.get7BitOutputEscapes(_quoteChar);
    } else {
      _outputEscapes = esc.getEscapeCodesForAscii();
    }
    return this;
  }

  @Override public Object streamWriteOutputTarget() {
    return _writer;
  }

  @Override public int streamWriteOutputBuffered() {
    int len = _outputTail - _outputHead;
    return Math.max(0, len);
  }

  @Override public void writeName(String name) throws JacksonException {
    int status = _streamWriteContext.writeName(name);
    if (status == JsonWriteContext.STATUS_EXPECT_VALUE) {
      _reportError("Cannot write a property name, expecting a value");
    }
    _writeName(name, (status == JsonWriteContext.STATUS_OK_AFTER_COMMA));
  }

  @Override public void writeName(SerializableString name) throws JacksonException {
    int status = _streamWriteContext.writeName(name.getValue());
    if (status == JsonWriteContext.STATUS_EXPECT_VALUE) {
      _reportError("Cannot write a property name, expecting a value");
    }
    _writeName(name, (status == JsonWriteContext.STATUS_OK_AFTER_COMMA));
  }

  protected final void _writeName(String name, boolean commaBefore) throws JacksonException {
    if (_cfgPrettyPrinter != null) {
      _writePPName(name, commaBefore);
      return;
    }
    if ((_outputTail + 1) >= _outputEnd) {
      _flushBuffer();
    }
    if (commaBefore) {
      _outputBuffer[_outputTail++] = ',';
    }
    if (_cfgUnqNames) {
      _writeString(name);
      return;
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    _writeString(name);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  protected final void _writeName(SerializableString name, boolean commaBefore) throws JacksonException {
    if (_cfgPrettyPrinter != null) {
      _writePPName(name, commaBefore);
      return;
    }
    if ((_outputTail + 1) >= _outputEnd) {
      _flushBuffer();
    }
    if (commaBefore) {
      _outputBuffer[_outputTail++] = ',';
    }
    if (_cfgUnqNames) {
      final char[] ch = name.asQuotedChars();
      writeRaw(ch, 0, ch.length);
      return;
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    int len = name.appendQuoted(_outputBuffer, _outputTail);
    if (len < 0) {
      _writeNameTail(name);
      return;
    }
    _outputTail += len;
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  private final void _writeNameTail(SerializableString name) throws JacksonException {
    final char[] quoted = name.asQuotedChars();
    writeRaw(quoted, 0, quoted.length);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  @Override public void writeStartArray() throws JacksonException {
    _verifyValueWrite("start an array");
    _streamWriteContext = _streamWriteContext.createChildArrayContext(null);
    if (_cfgPrettyPrinter != null) {
      _cfgPrettyPrinter.writeStartArray(this);
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = '[';
    }
  }

  @Override public void writeStartArray(Object forValue) throws JacksonException {
    _verifyValueWrite("start an array");
    _streamWriteContext = _streamWriteContext.createChildArrayContext(forValue);
    if (_cfgPrettyPrinter != null) {
      _cfgPrettyPrinter.writeStartArray(this);
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = '[';
    }
  }

  @Override public void writeStartArray(Object forValue, int len) throws JacksonException {
    _verifyValueWrite("start an array");
    _streamWriteContext = _streamWriteContext.createChildArrayContext(forValue);
    if (_cfgPrettyPrinter != null) {
      _cfgPrettyPrinter.writeStartArray(this);
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = '[';
    }
  }

  @Override public void writeEndArray() throws JacksonException {
    if (!_streamWriteContext.inArray()) {
      _reportError("Current context not Array but " + _streamWriteContext.typeDesc());
    }
    if (_cfgPrettyPrinter != null) {
      _cfgPrettyPrinter.writeEndArray(this, _streamWriteContext.getEntryCount());
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = ']';
    }
    _streamWriteContext = _streamWriteContext.clearAndGetParent();
  }

  @Override public void writeStartObject() throws JacksonException {
    _verifyValueWrite("start an object");
    _streamWriteContext = _streamWriteContext.createChildObjectContext(null);
    if (_cfgPrettyPrinter != null) {
      _cfgPrettyPrinter.writeStartObject(this);
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = '{';
    }
  }

  @Override public void writeStartObject(Object forValue) throws JacksonException {
    _verifyValueWrite("start an object");
    JsonWriteContext ctxt = _streamWriteContext.createChildObjectContext(forValue);
    _streamWriteContext = ctxt;
    if (_cfgPrettyPrinter != null) {
      _cfgPrettyPrinter.writeStartObject(this);
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = '{';
    }
  }

  @Override public void writeStartObject(Object forValue, int size) throws JacksonException {
    _verifyValueWrite("start an object");
    JsonWriteContext ctxt = _streamWriteContext.createChildObjectContext(forValue);
    _streamWriteContext = ctxt;
    if (_cfgPrettyPrinter != null) {
      _cfgPrettyPrinter.writeStartObject(this);
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = '{';
    }
  }

  @Override public void writeEndObject() throws JacksonException {
    if (!_streamWriteContext.inObject()) {
      _reportError("Current context not Object but " + _streamWriteContext.typeDesc());
    }
    if (_cfgPrettyPrinter != null) {
      _cfgPrettyPrinter.writeEndObject(this, _streamWriteContext.getEntryCount());
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = '}';
    }
    _streamWriteContext = _streamWriteContext.clearAndGetParent();
  }

  protected final void _writePPName(String name, boolean commaBefore) throws JacksonException {
    if (commaBefore) {
      _cfgPrettyPrinter.writeObjectEntrySeparator(this);
    } else {
      _cfgPrettyPrinter.beforeObjectEntries(this);
    }
    if (_cfgUnqNames) {
      _writeString(name);
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = _quoteChar;
      _writeString(name);
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = _quoteChar;
    }
  }

  protected final void _writePPName(SerializableString name, boolean commaBefore) throws JacksonException {
    if (commaBefore) {
      _cfgPrettyPrinter.writeObjectEntrySeparator(this);
    } else {
      _cfgPrettyPrinter.beforeObjectEntries(this);
    }
    final char[] quoted = name.asQuotedChars();
    if (_cfgUnqNames) {
      writeRaw(quoted, 0, quoted.length);
    } else {
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = _quoteChar;
      writeRaw(quoted, 0, quoted.length);
      if (_outputTail >= _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = _quoteChar;
    }
  }

  @Override public void writeString(String text) throws JacksonException {
    _verifyValueWrite(WRITE_STRING);
    if (text == null) {
      _writeNull();
      return;
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    _writeString(text);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  @Override public void writeString(Reader reader, int len) throws JacksonException {
    _verifyValueWrite(WRITE_STRING);
    if (reader == null) {
      _reportError("null reader");
      return;
    }
    int toRead = (len >= 0) ? len : Integer.MAX_VALUE;
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    final char[] buf = _allocateCopyBuffer();
    while (toRead > 0) {
      int toReadNow = Math.min(toRead, buf.length);
      int numRead;
      try {
        numRead = reader.read(buf, 0, toReadNow);
      } catch (IOException e) {
        throw _wrapIOFailure(e);
      }
      if (numRead <= 0) {
        break;
      }
      _writeString(buf, 0, numRead);
      toRead -= numRead;
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    if (toRead > 0 && len >= 0) {
      _reportError("Didn\'t read enough from reader");
    }
  }

  @Override public void writeString(char[] text, int offset, int len) throws JacksonException {
    _verifyValueWrite(WRITE_STRING);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    _writeString(text, offset, len);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  @Override public void writeString(SerializableString sstr) throws JacksonException {
    _verifyValueWrite(WRITE_STRING);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    int len = sstr.appendQuoted(_outputBuffer, _outputTail);
    if (len < 0) {
      _writeString2(sstr);
      return;
    }
    _outputTail += len;
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  private void _writeString2(SerializableString sstr) throws JacksonException {
    char[] text = sstr.asQuotedChars();
    final int len = text.length;
    if (len < SHORT_WRITE) {
      int room = _outputEnd - _outputTail;
      if (len > room) {
        _flushBuffer();
      }
      System.arraycopy(text, 0, _outputBuffer, _outputTail, len);
      _outputTail += len;
    } else {
      _flushBuffer();
      try {
        _writer.write(text, 0, len);
      } catch (IOException e) {
        throw _wrapIOFailure(e);
      }
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  @Override public void writeRawUTF8String(byte[] text, int offset, int length) throws JacksonException {
    _reportUnsupportedOperation();
  }

  @Override public void writeUTF8String(byte[] text, int offset, int length) throws JacksonException {
    _reportUnsupportedOperation();
  }

  @Override public void writeRaw(String text) throws JacksonException {
    int len = text.length();
    int room = _outputEnd - _outputTail;
    if (room == 0) {
      _flushBuffer();
      room = _outputEnd - _outputTail;
    }
    if (room >= len) {
      text.getChars(0, len, _outputBuffer, _outputTail);
      _outputTail += len;
    } else {
      writeRawLong(text);
    }
  }

  @Override public void writeRaw(String text, int start, int len) throws JacksonException {
    int room = _outputEnd - _outputTail;
    if (room < len) {
      _flushBuffer();
      room = _outputEnd - _outputTail;
    }
    if (room >= len) {
      text.getChars(start, start + len, _outputBuffer, _outputTail);
      _outputTail += len;
    } else {
      writeRawLong(text.substring(start, start + len));
    }
  }

  @Override public void writeRaw(SerializableString text) throws JacksonException {
    int len = text.appendUnquoted(_outputBuffer, _outputTail);
    if (len < 0) {
      writeRaw(text.getValue());
      return;
    }
    _outputTail += len;
  }

  @Override public void writeRaw(char[] text, int offset, int len) throws JacksonException {
    if (len < SHORT_WRITE) {
      int room = _outputEnd - _outputTail;
      if (len > room) {
        _flushBuffer();
      }
      System.arraycopy(text, offset, _outputBuffer, _outputTail, len);
      _outputTail += len;
      return;
    }
    _flushBuffer();
    try {
      _writer.write(text, offset, len);
    } catch (IOException e) {
      throw _wrapIOFailure(e);
    }
  }

  @Override public void writeRaw(char c) throws JacksonException {
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = c;
  }

  private void writeRawLong(String text) throws JacksonException {
    int room = _outputEnd - _outputTail;
    text.getChars(0, room, _outputBuffer, _outputTail);
    _outputTail += room;
    _flushBuffer();
    int offset = room;
    int len = text.length() - room;
    while (len > _outputEnd) {
      int amount = _outputEnd;
      text.getChars(offset, offset + amount, _outputBuffer, 0);
      _outputHead = 0;
      _outputTail = amount;
      _flushBuffer();
      offset += amount;
      len -= amount;
    }
    text.getChars(offset, offset + len, _outputBuffer, 0);
    _outputHead = 0;
    _outputTail = len;
  }

  @Override public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws JacksonException {
    _verifyValueWrite(WRITE_BINARY);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    _writeBinary(b64variant, data, offset, offset + len);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  @Override public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws JacksonException {
    _verifyValueWrite(WRITE_BINARY);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
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
    _outputBuffer[_outputTail++] = _quoteChar;
    return bytes;
  }

  @Override public void writeNumber(short s) throws JacksonException {
    _verifyValueWrite(WRITE_NUMBER);
    if (_cfgNumbersAsStrings) {
      _writeQuotedShort(s);
      return;
    }
    if ((_outputTail + 6) >= _outputEnd) {
      _flushBuffer();
    }
    _outputTail = NumberOutput.outputInt(s, _outputBuffer, _outputTail);
  }

  private void _writeQuotedShort(short s) throws JacksonException {
    if ((_outputTail + 8) >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    _outputTail = NumberOutput.outputInt(s, _outputBuffer, _outputTail);
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  @Override public void writeNumber(int i) throws JacksonException {
    _verifyValueWrite(WRITE_NUMBER);
    if (_cfgNumbersAsStrings) {
      _writeQuotedInt(i);
      return;
    }
    if ((_outputTail + 11) >= _outputEnd) {
      _flushBuffer();
    }
    _outputTail = NumberOutput.outputInt(i, _outputBuffer, _outputTail);
  }

  private void _writeQuotedInt(int i) throws JacksonException {
    if ((_outputTail + 13) >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    _outputTail = NumberOutput.outputInt(i, _outputBuffer, _outputTail);
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  @Override public void writeNumber(long l) throws JacksonException {
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

  private void _writeQuotedLong(long l) throws JacksonException {
    if ((_outputTail + 23) >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    _outputTail = NumberOutput.outputLong(l, _outputBuffer, _outputTail);
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  @Override public void writeNumber(BigInteger value) throws JacksonException {
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

  @Override public void writeNumber(double d) throws JacksonException {
    if (_cfgNumbersAsStrings || (NumberOutput.notFinite(d) && JsonWriteFeature.WRITE_NAN_AS_STRINGS.enabledIn(_formatWriteFeatures))) {
      writeString(NumberOutput.toString(d, isEnabled(Feature.USE_FAST_DOUBLE_WRITER)));
      return;
    }
    _verifyValueWrite(WRITE_NUMBER);
    writeRaw(NumberOutput.toString(d, isEnabled(Feature.USE_FAST_DOUBLE_WRITER)));
  }

  @Override public void writeNumber(float f) throws JacksonException {
    if (_cfgNumbersAsStrings || (NumberOutput.notFinite(f) && JsonWriteFeature.WRITE_NAN_AS_STRINGS.enabledIn(_formatWriteFeatures))) {
      writeString(NumberOutput.toString(f, isEnabled(Feature.USE_FAST_DOUBLE_WRITER)));
      return;
    }
    _verifyValueWrite(WRITE_NUMBER);
    writeRaw(NumberOutput.toString(f, isEnabled(Feature.USE_FAST_DOUBLE_WRITER)));
  }

  @Override public void writeNumber(BigDecimal value) throws JacksonException {
    _verifyValueWrite(WRITE_NUMBER);
    if (value == null) {
      _writeNull();
    } else {
      if (_cfgNumbersAsStrings) {
        _writeQuotedRaw(_asString(value));
      } else {
        writeRaw(_asString(value));
      }
    }
  }

  @Override public void writeNumber(String encodedValue) throws JacksonException {
    _verifyValueWrite(WRITE_NUMBER);
    if (encodedValue == null) {
      _writeNull();
    } else {
      if (_cfgNumbersAsStrings) {
        _writeQuotedRaw(encodedValue);
      } else {
        writeRaw(encodedValue);
      }
    }
  }

  @Override public void writeNumber(char[] encodedValueBuffer, int offset, int length) throws JacksonException {
    _verifyValueWrite(WRITE_NUMBER);
    if (_cfgNumbersAsStrings) {
      _writeQuotedRaw(encodedValueBuffer, offset, length);
    } else {
      writeRaw(encodedValueBuffer, offset, length);
    }
  }

  private void _writeQuotedRaw(String value) throws JacksonException {
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    writeRaw(value);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  private void _writeQuotedRaw(char[] text, int offset, int length) throws JacksonException {
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
    writeRaw(text, offset, length);
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = _quoteChar;
  }

  @Override public void writeBoolean(boolean state) throws JacksonException {
    _verifyValueWrite(WRITE_BOOLEAN);
    if ((_outputTail + 5) >= _outputEnd) {
      _flushBuffer();
    }
    int ptr = _outputTail;
    char[] buf = _outputBuffer;
    if (state) {
      buf[ptr] = 't';
      buf[++ptr] = 'r';
      buf[++ptr] = 'u';
      buf[++ptr] = 'e';
    } else {
      buf[ptr] = 'f';
      buf[++ptr] = 'a';
      buf[++ptr] = 'l';
      buf[++ptr] = 's';
      buf[++ptr] = 'e';
    }
    _outputTail = ptr + 1;
  }

  @Override public void writeNull() throws JacksonException {
    _verifyValueWrite(WRITE_NULL);
    _writeNull();
  }

  @Override protected final void _verifyValueWrite(String typeMsg) throws JacksonException {
    final int status = _streamWriteContext.writeValue();
    if (_cfgPrettyPrinter != null) {
      _verifyPrettyValueWrite(typeMsg, status);
      return;
    }
    char c;
    switch (status) {
      case JsonWriteContext.STATUS_OK_AS_IS:
      default:
      return;
      case JsonWriteContext.STATUS_OK_AFTER_COMMA:
      c = ',';
      break;
      case JsonWriteContext.STATUS_OK_AFTER_COLON:
      c = ':';
      break;
      case JsonWriteContext.STATUS_OK_AFTER_SPACE:
      if (_rootValueSeparator != null) {
        writeRaw(_rootValueSeparator.getValue());
      }
      return;
      case JsonWriteContext.STATUS_EXPECT_NAME:
      _reportCantWriteValueExpectName(typeMsg);
      return;
    }
    if (_outputTail >= _outputEnd) {
      _flushBuffer();
    }
    _outputBuffer[_outputTail++] = c;
  }

  @Override public void flush() throws JacksonException {
    _flushBuffer();
    if (_writer != null) {
      if (isEnabled(StreamWriteFeature.FLUSH_PASSED_TO_STREAM)) {
        try {
          _writer.flush();
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
      }
    }
  }

  @Override public void close() throws JacksonException {
    super.close();
    RuntimeException flushFail = null;
    try {
      if ((_outputBuffer != null) && isEnabled(StreamWriteFeature.AUTO_CLOSE_CONTENT)) {
        while (true) {
          TokenStreamContext ctxt = streamWriteContext();
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
    } catch (RuntimeException e) {
      flushFail = e;
    }
    _outputHead = 0;
    _outputTail = 0;
    if (_writer != null) {
      try {
        if (_ioContext.isResourceManaged() || isEnabled(StreamWriteFeature.AUTO_CLOSE_TARGET)) {
          _writer.close();
        } else {
          if (isEnabled(StreamWriteFeature.FLUSH_PASSED_TO_STREAM)) {
            _writer.flush();
          }
        }
      } catch (IOException e) {
        JacksonException je = _wrapIOFailure(e);
        if (flushFail != null) {
          je.addSuppressed(flushFail);
        }
        throw je;
      }
    }
    _releaseBuffers();
    if (flushFail != null) {
      throw flushFail;
    }
  }

  @Override protected void _releaseBuffers() {
    char[] buf = _outputBuffer;
    if (buf != null) {
      _outputBuffer = null;
      _ioContext.releaseConcatBuffer(buf);
    }
    buf = _copyBuffer;
    if (buf != null) {
      _copyBuffer = null;
      _ioContext.releaseNameCopyBuffer(buf);
    }
  }

  private void _writeString(String text) throws JacksonException {
    final int len = text.length();
    if (len > _outputEnd) {
      _writeLongString(text);
      return;
    }
    if ((_outputTail + len) > _outputEnd) {
      _flushBuffer();
    }
    text.getChars(0, len, _outputBuffer, _outputTail);
    if (_characterEscapes != null) {
      _writeStringCustom(len);
    } else {
      if (_maximumNonEscapedChar != 0) {
        _writeStringASCII(len, _maximumNonEscapedChar);
      } else {
        _writeString2(len);
      }
    }
  }

  private void _writeString2(final int len) throws JacksonException {
    final int end = _outputTail + len;
    final int[] escCodes = _outputEscapes;
    final int escLen = escCodes.length;
    output_loop:
    while (_outputTail < end) {
      escape_loop:
      while (true) {
        char c = _outputBuffer[_outputTail];
        if (c < escLen && escCodes[c] != 0) {
          break escape_loop;
        }
        if (++_outputTail >= end) {
          break output_loop;
        }
      }
      int flushLen = (_outputTail - _outputHead);
      if (flushLen > 0) {
        try {
          _writer.write(_outputBuffer, _outputHead, flushLen);
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
      }
      char c = _outputBuffer[_outputTail++];
      _prependOrWriteCharacterEscape(c, escCodes[c]);
    }
  }

  /**
     * Method called to write "long strings", strings whose length exceeds
     * output buffer length.
     */
  private void _writeLongString(String text) throws JacksonException {
    _flushBuffer();
    final int textLen = text.length();
    int offset = 0;
    do {
      int max = _outputEnd;
      int segmentLen = ((offset + max) > textLen) ? (textLen - offset) : max;
      text.getChars(offset, offset + segmentLen, _outputBuffer, 0);
      if (_characterEscapes != null) {
        _writeSegmentCustom(segmentLen);
      } else {
        if (_maximumNonEscapedChar != 0) {
          _writeSegmentASCII(segmentLen, _maximumNonEscapedChar);
        } else {
          _writeSegment(segmentLen);
        }
      }
      offset += segmentLen;
    } while(offset < textLen);
  }

  /**
     * Method called to output textual context which has been copied
     * to the output buffer prior to call. If any escaping is needed,
     * it will also be handled by the method.
     *<p>
     * Note: when called, textual content to write is within output
     * buffer, right after buffered content (if any). That's why only
     * length of that text is passed, as buffer and offset are implied.
     */
  private void _writeSegment(int end) throws JacksonException {
    final int[] escCodes = _outputEscapes;
    final int escLen = escCodes.length;
    int ptr = 0;
    int start = ptr;
    output_loop:
    while (ptr < end) {
      char c;
      while (true) {
        c = _outputBuffer[ptr];
        if (c < escLen && escCodes[c] != 0) {
          break;
        }
        if (++ptr >= end) {
          break;
        }
      }
      int flushLen = (ptr - start);
      if (flushLen > 0) {
        try {
          _writer.write(_outputBuffer, start, flushLen);
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
        if (ptr >= end) {
          break output_loop;
        }
      }
      ++ptr;
      start = _prependOrWriteCharacterEscape(_outputBuffer, ptr, end, c, escCodes[c]);
    }
  }

  /**
     * This method called when the string content is already in
     * a char buffer, and need not be copied for processing.
     */
  private void _writeString(char[] text, int offset, int len) throws JacksonException {
    if (_characterEscapes != null) {
      _writeStringCustom(text, offset, len);
      return;
    }
    if (_maximumNonEscapedChar != 0) {
      _writeStringASCII(text, offset, len, _maximumNonEscapedChar);
      return;
    }
    len += offset;
    final int[] escCodes = _outputEscapes;
    final int escLen = escCodes.length;
    while (offset < len) {
      int start = offset;
      while (true) {
        char c = text[offset];
        if (c < escLen && escCodes[c] != 0) {
          break;
        }
        if (++offset >= len) {
          break;
        }
      }
      int newAmount = offset - start;
      if (newAmount < SHORT_WRITE) {
        if ((_outputTail + newAmount) > _outputEnd) {
          _flushBuffer();
        }
        if (newAmount > 0) {
          System.arraycopy(text, start, _outputBuffer, _outputTail, newAmount);
          _outputTail += newAmount;
        }
      } else {
        _flushBuffer();
        try {
          _writer.write(text, start, newAmount);
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
      }
      if (offset >= len) {
        break;
      }
      char c = text[offset++];
      _appendCharacterEscape(c, escCodes[c]);
    }
  }

  private void _writeStringASCII(final int len, final int maxNonEscaped) throws JacksonException {
    int end = _outputTail + len;
    final int[] escCodes = _outputEscapes;
    final int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
    int escCode = 0;
    output_loop:
    while (_outputTail < end) {
      char c;
      escape_loop:
      while (true) {
        c = _outputBuffer[_outputTail];
        if (c < escLimit) {
          escCode = escCodes[c];
          if (escCode != 0) {
            break escape_loop;
          }
        } else {
          if (c > maxNonEscaped) {
            escCode = CharacterEscapes.ESCAPE_STANDARD;
            break escape_loop;
          }
        }
        if (++_outputTail >= end) {
          break output_loop;
        }
      }
      int flushLen = (_outputTail - _outputHead);
      if (flushLen > 0) {
        try {
          _writer.write(_outputBuffer, _outputHead, flushLen);
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
      }
      ++_outputTail;
      _prependOrWriteCharacterEscape(c, escCode);
    }
  }

  private void _writeSegmentASCII(int end, final int maxNonEscaped) throws JacksonException {
    final int[] escCodes = _outputEscapes;
    final int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
    int ptr = 0;
    int escCode = 0;
    int start = ptr;
    output_loop:
    while (ptr < end) {
      char c;
      while (true) {
        c = _outputBuffer[ptr];
        if (c < escLimit) {
          escCode = escCodes[c];
          if (escCode != 0) {
            break;
          }
        } else {
          if (c > maxNonEscaped) {
            escCode = CharacterEscapes.ESCAPE_STANDARD;
            break;
          }
        }
        if (++ptr >= end) {
          break;
        }
      }
      int flushLen = (ptr - start);
      if (flushLen > 0) {
        try {
          _writer.write(_outputBuffer, start, flushLen);
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
        if (ptr >= end) {
          break output_loop;
        }
      }
      ++ptr;
      start = _prependOrWriteCharacterEscape(_outputBuffer, ptr, end, c, escCode);
    }
  }

  private void _writeStringASCII(char[] text, int offset, int len, final int maxNonEscaped) throws JacksonException {
    len += offset;
    final int[] escCodes = _outputEscapes;
    final int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
    int escCode = 0;
    while (offset < len) {
      int start = offset;
      char c;
      while (true) {
        c = text[offset];
        if (c < escLimit) {
          escCode = escCodes[c];
          if (escCode != 0) {
            break;
          }
        } else {
          if (c > maxNonEscaped) {
            escCode = CharacterEscapes.ESCAPE_STANDARD;
            break;
          }
        }
        if (++offset >= len) {
          break;
        }
      }
      int newAmount = offset - start;
      if (newAmount < SHORT_WRITE) {
        if ((_outputTail + newAmount) > _outputEnd) {
          _flushBuffer();
        }
        if (newAmount > 0) {
          System.arraycopy(text, start, _outputBuffer, _outputTail, newAmount);
          _outputTail += newAmount;
        }
      } else {
        _flushBuffer();
        try {
          _writer.write(text, start, newAmount);
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
      }
      if (offset >= len) {
        break;
      }
      ++offset;
      _appendCharacterEscape(c, escCode);
    }
  }

  private void _writeStringCustom(final int len) throws JacksonException {
    int end = _outputTail + len;
    final int[] escCodes = _outputEscapes;
    final int maxNonEscaped = (_maximumNonEscapedChar < 1) ? 0xFFFF : _maximumNonEscapedChar;
    final int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
    int escCode = 0;
    final CharacterEscapes customEscapes = _characterEscapes;
    output_loop:
    while (_outputTail < end) {
      char c;
      escape_loop:
      while (true) {
        c = _outputBuffer[_outputTail];
        if (c < escLimit) {
          escCode = escCodes[c];
          if (escCode != 0) {
            break escape_loop;
          }
        } else {
          if (c > maxNonEscaped) {
            escCode = CharacterEscapes.ESCAPE_STANDARD;
            break escape_loop;
          } else {
            if ((_currentEscape = customEscapes.getEscapeSequence(c)) != null) {
              escCode = CharacterEscapes.ESCAPE_CUSTOM;
              break escape_loop;
            }
          }
        }
        if (++_outputTail >= end) {
          break output_loop;
        }
      }
      int flushLen = (_outputTail - _outputHead);
      if (flushLen > 0) {
        try {
          _writer.write(_outputBuffer, _outputHead, flushLen);
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
      }
      ++_outputTail;
      _prependOrWriteCharacterEscape(c, escCode);
    }
  }

  private void _writeSegmentCustom(int end) throws JacksonException {
    final int[] escCodes = _outputEscapes;
    final int maxNonEscaped = (_maximumNonEscapedChar < 1) ? 0xFFFF : _maximumNonEscapedChar;
    final int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
    final CharacterEscapes customEscapes = _characterEscapes;
    int ptr = 0;
    int escCode = 0;
    int start = ptr;
    output_loop:
    while (ptr < end) {
      char c;
      while (true) {
        c = _outputBuffer[ptr];
        if (c < escLimit) {
          escCode = escCodes[c];
          if (escCode != 0) {
            break;
          }
        } else {
          if (c > maxNonEscaped) {
            escCode = CharacterEscapes.ESCAPE_STANDARD;
            break;
          } else {
            if ((_currentEscape = customEscapes.getEscapeSequence(c)) != null) {
              escCode = CharacterEscapes.ESCAPE_CUSTOM;
              break;
            }
          }
        }
        if (++ptr >= end) {
          break;
        }
      }
      int flushLen = (ptr - start);
      if (flushLen > 0) {
        try {
          _writer.write(_outputBuffer, start, flushLen);
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
        if (ptr >= end) {
          break output_loop;
        }
      }
      ++ptr;
      start = _prependOrWriteCharacterEscape(_outputBuffer, ptr, end, c, escCode);
    }
  }

  private void _writeStringCustom(char[] text, int offset, int len) throws JacksonException {
    len += offset;
    final int[] escCodes = _outputEscapes;
    final int maxNonEscaped = (_maximumNonEscapedChar < 1) ? 0xFFFF : _maximumNonEscapedChar;
    final int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
    final CharacterEscapes customEscapes = _characterEscapes;
    int escCode = 0;
    while (offset < len) {
      int start = offset;
      char c;
      while (true) {
        c = text[offset];
        if (c < escLimit) {
          escCode = escCodes[c];
          if (escCode != 0) {
            break;
          }
        } else {
          if (c > maxNonEscaped) {
            escCode = CharacterEscapes.ESCAPE_STANDARD;
            break;
          } else {
            if ((_currentEscape = customEscapes.getEscapeSequence(c)) != null) {
              escCode = CharacterEscapes.ESCAPE_CUSTOM;
              break;
            }
          }
        }
        if (++offset >= len) {
          break;
        }
      }
      int newAmount = offset - start;
      if (newAmount < SHORT_WRITE) {
        if ((_outputTail + newAmount) > _outputEnd) {
          _flushBuffer();
        }
        if (newAmount > 0) {
          System.arraycopy(text, start, _outputBuffer, _outputTail, newAmount);
          _outputTail += newAmount;
        }
      } else {
        _flushBuffer();
        try {
          _writer.write(text, start, newAmount);
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
      }
      if (offset >= len) {
        break;
      }
      ++offset;
      _appendCharacterEscape(c, escCode);
    }
  }

  protected final void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, final int inputEnd) throws JacksonException {
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

  protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer, int bytesLeft) throws JacksonException {
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

  protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer) throws JacksonException {
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

  private int _readMore(InputStream in, byte[] readBuffer, int inputPtr, int inputEnd, int maxRead) throws JacksonException {
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
      int count;
      try {
        count = in.read(readBuffer, inputEnd, length);
      } catch (IOException e) {
        throw _wrapIOFailure(e);
      }
      if (count < 0) {
        return inputEnd;
      }
      inputEnd += count;
    } while(inputEnd < 3);
    return inputEnd;
  }

  private final void _writeNull() throws JacksonException {
    if ((_outputTail + 4) >= _outputEnd) {
      _flushBuffer();
    }
    int ptr = _outputTail;
    char[] buf = _outputBuffer;
    buf[ptr] = 'n';
    buf[++ptr] = 'u';
    buf[++ptr] = 'l';
    buf[++ptr] = 'l';
    _outputTail = ptr + 1;
  }

  /**
     * Method called to try to either prepend character escape at front of
     * given buffer; or if not possible, to write it out directly.
     * Uses head and tail pointers (and updates as necessary)
     */
  private void _prependOrWriteCharacterEscape(char ch, int escCode) throws JacksonException {
    if (escCode >= 0) {
      if (_outputTail >= 2) {
        int ptr = _outputTail - 2;
        _outputHead = ptr;
        _outputBuffer[ptr++] = '\\';
        _outputBuffer[ptr] = (char) escCode;
        return;
      }
      char[] buf = _entityBuffer;
      if (buf == null) {
        buf = _allocateEntityBuffer();
      }
      _outputHead = _outputTail;
      buf[1] = (char) escCode;
      try {
        _writer.write(buf, 0, 2);
      } catch (IOException e) {
        throw _wrapIOFailure(e);
      }
      return;
    }
    if (escCode != CharacterEscapes.ESCAPE_CUSTOM) {
      if (_outputTail >= 6) {
        char[] buf = _outputBuffer;
        int ptr = _outputTail - 6;
        _outputHead = ptr;
        buf[ptr] = '\\';
        buf[++ptr] = 'u';
        if (ch > 0xFF) {
          int hi = (ch >> 8) & 0xFF;
          buf[++ptr] = HEX_CHARS[hi >> 4];
          buf[++ptr] = HEX_CHARS[hi & 0xF];
          ch &= 0xFF;
        } else {
          buf[++ptr] = '0';
          buf[++ptr] = '0';
        }
        buf[++ptr] = HEX_CHARS[ch >> 4];
        buf[++ptr] = HEX_CHARS[ch & 0xF];
        return;
      }
      char[] buf = _entityBuffer;
      if (buf == null) {
        buf = _allocateEntityBuffer();
      }
      _outputHead = _outputTail;
      try {
        if (ch > 0xFF) {
          int hi = (ch >> 8) & 0xFF;
          int lo = ch & 0xFF;
          buf[10] = HEX_CHARS[hi >> 4];
          buf[11] = HEX_CHARS[hi & 0xF];
          buf[12] = HEX_CHARS[lo >> 4];
          buf[13] = HEX_CHARS[lo & 0xF];
          _writer.write(buf, 8, 6);
        } else {
          buf[6] = HEX_CHARS[ch >> 4];
          buf[7] = HEX_CHARS[ch & 0xF];
          _writer.write(buf, 2, 6);
        }
      } catch (IOException e) {
        throw _wrapIOFailure(e);
      }
      return;
    }
    String escape;
    if (_currentEscape == null) {
      escape = _characterEscapes.getEscapeSequence(ch).getValue();
    } else {
      escape = _currentEscape.getValue();
      _currentEscape = null;
    }
    int len = escape.length();
    if (_outputTail >= len) {
      int ptr = _outputTail - len;
      _outputHead = ptr;
      escape.getChars(0, len, _outputBuffer, ptr);
      return;
    }
    _outputHead = _outputTail;
    try {
      _writer.write(escape);
    } catch (IOException e) {
      throw _wrapIOFailure(e);
    }
  }

  /**
     * Method called to try to either prepend character escape at front of
     * given buffer; or if not possible, to write it out directly.
     * 
     * @return Pointer to start of prepended entity (if prepended); or 'ptr'
     *   if not.
     */
  private int _prependOrWriteCharacterEscape(char[] buffer, int ptr, int end, char ch, int escCode) throws JacksonException {
    if (escCode >= 0) {
      if (ptr > 1 && ptr < end) {
        ptr -= 2;
        buffer[ptr] = '\\';
        buffer[ptr + 1] = (char) escCode;
      } else {
        char[] ent = _entityBuffer;
        if (ent == null) {
          ent = _allocateEntityBuffer();
        }
        ent[1] = (char) escCode;
        try {
          _writer.write(ent, 0, 2);
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
      }
      return ptr;
    }
    if (escCode != CharacterEscapes.ESCAPE_CUSTOM) {
      if (ptr > 5 && ptr < end) {
        ptr -= 6;
        buffer[ptr++] = '\\';
        buffer[ptr++] = 'u';
        if (ch > 0xFF) {
          int hi = (ch >> 8) & 0xFF;
          buffer[ptr++] = HEX_CHARS[hi >> 4];
          buffer[ptr++] = HEX_CHARS[hi & 0xF];
          ch &= 0xFF;
        } else {
          buffer[ptr++] = '0';
          buffer[ptr++] = '0';
        }
        buffer[ptr++] = HEX_CHARS[ch >> 4];
        buffer[ptr] = HEX_CHARS[ch & 0xF];
        ptr -= 5;
      } else {
        char[] ent = _entityBuffer;
        if (ent == null) {
          ent = _allocateEntityBuffer();
        }
        _outputHead = _outputTail;
        try {
          if (ch > 0xFF) {
            int hi = (ch >> 8) & 0xFF;
            int lo = ch & 0xFF;
            ent[10] = HEX_CHARS[hi >> 4];
            ent[11] = HEX_CHARS[hi & 0xF];
            ent[12] = HEX_CHARS[lo >> 4];
            ent[13] = HEX_CHARS[lo & 0xF];
            _writer.write(ent, 8, 6);
          } else {
            ent[6] = HEX_CHARS[ch >> 4];
            ent[7] = HEX_CHARS[ch & 0xF];
            _writer.write(ent, 2, 6);
          }
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
      }
      return ptr;
    }
    String escape;
    if (_currentEscape == null) {
      escape = _characterEscapes.getEscapeSequence(ch).getValue();
    } else {
      escape = _currentEscape.getValue();
      _currentEscape = null;
    }
    int len = escape.length();
    if (ptr >= len && ptr < end) {
      ptr -= len;
      escape.getChars(0, len, buffer, ptr);
    } else {
      try {
        _writer.write(escape);
      } catch (IOException e) {
        throw _wrapIOFailure(e);
      }
    }
    return ptr;
  }

  /**
     * Method called to append escape sequence for given character, at the
     * end of standard output buffer; or if not possible, write out directly.
     */
  private void _appendCharacterEscape(char ch, int escCode) throws JacksonException {
    if (escCode >= 0) {
      if ((_outputTail + 2) > _outputEnd) {
        _flushBuffer();
      }
      _outputBuffer[_outputTail++] = '\\';
      _outputBuffer[_outputTail++] = (char) escCode;
      return;
    }
    if (escCode != CharacterEscapes.ESCAPE_CUSTOM) {
      if ((_outputTail + 5) >= _outputEnd) {
        _flushBuffer();
      }
      int ptr = _outputTail;
      char[] buf = _outputBuffer;
      buf[ptr++] = '\\';
      buf[ptr++] = 'u';
      if (ch > 0xFF) {
        int hi = (ch >> 8) & 0xFF;
        buf[ptr++] = HEX_CHARS[hi >> 4];
        buf[ptr++] = HEX_CHARS[hi & 0xF];
        ch &= 0xFF;
      } else {
        buf[ptr++] = '0';
        buf[ptr++] = '0';
      }
      buf[ptr++] = HEX_CHARS[ch >> 4];
      buf[ptr++] = HEX_CHARS[ch & 0xF];
      _outputTail = ptr;
      return;
    }
    String escape;
    if (_currentEscape == null) {
      escape = _characterEscapes.getEscapeSequence(ch).getValue();
    } else {
      escape = _currentEscape.getValue();
      _currentEscape = null;
    }
    int len = escape.length();
    if ((_outputTail + len) > _outputEnd) {
      _flushBuffer();
      if (len > _outputEnd) {
        try {
          _writer.write(escape);
        } catch (IOException e) {
          throw _wrapIOFailure(e);
        }
        return;
      }
    }
    escape.getChars(0, len, _outputBuffer, _outputTail);
    _outputTail += len;
  }

  private char[] _allocateEntityBuffer() {
    char[] buf = new char[14];
    buf[0] = '\\';
    buf[2] = '\\';
    buf[3] = 'u';
    buf[4] = '0';
    buf[5] = '0';
    buf[8] = '\\';
    buf[9] = 'u';
    _entityBuffer = buf;
    return buf;
  }

  private char[] _allocateCopyBuffer() {
    if (_copyBuffer == null) {
      _copyBuffer = _ioContext.allocNameCopyBuffer(2000);
    }
    return _copyBuffer;
  }

  protected void _flushBuffer() throws JacksonException {
    int len = _outputTail - _outputHead;
    if (len > 0) {
      int offset = _outputHead;
      _outputTail = _outputHead = 0;
      try {
        _writer.write(_outputBuffer, offset, len);
      } catch (IOException e) {
        throw _wrapIOFailure(e);
      }
    }
  }
}