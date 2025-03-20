package com.fasterxml.jackson.dataformat.cbor;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.TextBuffer;

public final class CBORParser extends ParserMinimalBase {
  private final static byte[] NO_BYTES = new byte[0];

  public enum Feature implements FormatFeature {

    ;

    final boolean _defaultState;

    final int _mask;

    /**
         * Method that calculates bit set (flags) of all features that
         * are enabled by default.
         */
    public static int collectDefaults() {
      int flags = 0;
      for (Feature f : values()) {
        if (f.enabledByDefault()) {
          flags |= f.getMask();
        }
      }
      return flags;
    }

    private Feature(boolean defaultState) {
      _defaultState = defaultState;
      _mask = (1 << ordinal());
    }

    @Override public boolean enabledByDefault() {
      return _defaultState;
    }

    @Override public int getMask() {
      return _mask;
    }

    @Override public boolean enabledIn(int flags) {
      return (flags & _mask) != 0;
    }
  }

  private final static Charset UTF8 = Charset.forName("UTF-8");

  private final static int[] NO_INTS = new int[0];

  private final static int[] UTF8_UNIT_CODES = CBORConstants.sUtf8UnitLengths;

  private final static double MATH_POW_2_10 = Math.pow(2, 10);

  private final static double MATH_POW_2_NEG14 = Math.pow(2, -14);

  /**
     * Codec used for data binding when (if) requested.
     */
  protected ObjectCodec _objectCodec;

  /**
     * I/O context for this reader. It handles buffer allocation
     * for the reader.
     */
  final protected IOContext _ioContext;

  /**
     * Flag that indicates whether parser is closed or not. Gets
     * set when parser is either closed by explicit call
     * ({@link #close}) or when end-of-input is reached.
     */
  protected boolean _closed;

  /**
     * Pointer to next available character in buffer
     */
  protected int _inputPtr = 0;

  /**
     * Index of character after last available one in the buffer.
     */
  protected int _inputEnd = 0;

  /**
     * Number of characters/bytes that were contained in previous blocks
     * (blocks that were already processed prior to the current buffer).
     */
  protected long _currInputProcessed = 0L;

  /**
     * Current row location of current point in input buffer, starting
     * from 1, if available.
     */
  protected int _currInputRow = 1;

  /**
     * Current index of the first character of the current row in input
     * buffer. Needed to calculate column position, if necessary; benefit
     * of not having column itself is that this only has to be updated
     * once per line.
     */
  protected int _currInputRowStart = 0;

  /**
     * Total number of bytes/characters read before start of current token.
     * For big (gigabyte-sized) sizes are possible, needs to be long,
     * unlike pointers and sizes related to in-memory buffers.
     */
  protected long _tokenInputTotal = 0;

  /**
     * Input row on which current token starts, 1-based
     */
  protected int _tokenInputRow = 1;

  /**
     * Column on input row that current token starts; 0-based (although
     * in the end it'll be converted to 1-based)
     */
  protected int _tokenInputCol = 0;

  /**
     * Information about parser context, context in which
     * the next token is to be parsed (root, array, object).
     */
  protected CBORReadContext _parsingContext;

  /**
     * Buffer that contains contents of String values, including
     * field names if necessary (name split across boundary,
     * contains escape sequence, or access needed to char array)
     */
  protected final TextBuffer _textBuffer;

  /**
     * Temporary buffer that is needed if field name is accessed
     * using {@link #getTextCharacters} method (instead of String
     * returning alternatives)
     */
  protected char[] _nameCopyBuffer = null;

  /**
     * Flag set to indicate whether the field name is available
     * from the name copy buffer or not (in addition to its String
     * representation  being available via read context)
     */
  protected boolean _nameCopied = false;

  /**
     * ByteArrayBuilder is needed if 'getBinaryValue' is called. If so,
     * we better reuse it for remainder of content.
     */
  protected ByteArrayBuilder _byteArrayBuilder = null;

  /**
     * We will hold on to decoded binary data, for duration of
     * current event, so that multiple calls to
     * {@link #getBinaryValue} will not need to decode data more
     * than once.
     */
  protected byte[] _binaryValue;

  /**
     * We will keep track of tag value for possible future use.
     */
  protected int _tagValue = -1;

  /**
     * Input stream that can be used for reading more content, if one
     * in use. May be null, if input comes just as a full buffer,
     * or if the stream has been closed.
     */
  protected InputStream _inputStream;

  /**
     * Current buffer from which data is read; generally data is read into
     * buffer from input source, but in some cases pre-loaded buffer
     * is handed to the parser.
     */
  protected byte[] _inputBuffer;

  /**
     * Flag that indicates whether the input buffer is recycable (and
     * needs to be returned to recycler once we are done) or not.
     *<p>
     * If it is not, it also means that parser can NOT modify underlying
     * buffer.
     */
  protected boolean _bufferRecyclable;

  /**
     * Flag that indicates that the current token has not yet
     * been fully processed, and needs to be finished for
     * some access (or skipped to obtain the next token)
     */
  protected boolean _tokenIncomplete = false;

  /**
     * Type byte of the current token
     */
  protected int _typeByte;

  /**
     * Helper variables used when dealing with chunked content.
     */
  private int _chunkLeft, _chunkEnd;

  /**
     * Symbol table that contains field names encountered so far
     */
  final protected ByteQuadsCanonicalizer _symbols;

  /**
     * Temporary buffer used for name parsing.
     */
  protected int[] _quadBuffer = NO_INTS;

  /**
     * Quads used for hash calculation
     */
  protected int _quad1, _quad2, _quad3;

  final protected static int NR_UNKNOWN = 0;

  final protected static int NR_INT = 0x0001;

  final protected static int NR_LONG = 0x0002;

  final protected static int NR_BIGINT = 0x0004;

  final protected static int NR_FLOAT = 0x008;

  final protected static int NR_DOUBLE = 0x010;

  final protected static int NR_BIGDECIMAL = 0x0020;

  final static BigInteger BI_MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);

  final static BigInteger BI_MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

  final static BigInteger BI_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);

  final static BigInteger BI_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

  final static BigDecimal BD_MIN_LONG = new BigDecimal(BI_MIN_LONG);

  final static BigDecimal BD_MAX_LONG = new BigDecimal(BI_MAX_LONG);

  final static BigDecimal BD_MIN_INT = new BigDecimal(BI_MIN_INT);

  final static BigDecimal BD_MAX_INT = new BigDecimal(BI_MAX_INT);

  final static long MIN_INT_L = (long) Integer.MIN_VALUE;

  final static long MAX_INT_L = (long) Integer.MAX_VALUE;

  final static double MIN_LONG_D = (double) Long.MIN_VALUE;

  final static double MAX_LONG_D = (double) Long.MAX_VALUE;

  final static double MIN_INT_D = (double) Integer.MIN_VALUE;

  final static double MAX_INT_D = (double) Integer.MAX_VALUE;

  final protected static int INT_0 = '0';

  final protected static int INT_9 = '9';

  final protected static int INT_MINUS = '-';

  final protected static int INT_PLUS = '+';

  final protected static char CHAR_NULL = '\u0000';

  /**
     * Bitfield that indicates which numeric representations
     * have been calculated for the current type
     */
  protected int _numTypesValid = NR_UNKNOWN;

  protected int _numberInt;

  protected long _numberLong;

  protected float _numberFloat;

  protected double _numberDouble;

  protected BigInteger _numberBigInt;

  protected BigDecimal _numberBigDecimal;

  public CBORParser(IOContext ctxt, int parserFeatures, int cborFeatures, ObjectCodec codec, ByteQuadsCanonicalizer sym, InputStream in, byte[] inputBuffer, int start, int end, boolean bufferRecyclable) {
    super(parserFeatures);
    _ioContext = ctxt;
    _objectCodec = codec;
    _symbols = sym;
    _inputStream = in;
    _inputBuffer = inputBuffer;
    _inputPtr = start;
    _inputEnd = end;
    _bufferRecyclable = bufferRecyclable;
    _textBuffer = ctxt.constructTextBuffer();
    DupDetector dups = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(parserFeatures) ? DupDetector.rootDetector(this) : null;
    _parsingContext = CBORReadContext.createRootContext(dups);
    _tokenInputRow = -1;
    _tokenInputCol = -1;
  }

  @Override public ObjectCodec getCodec() {
    return _objectCodec;
  }

  @Override public void setCodec(ObjectCodec c) {
    _objectCodec = c;
  }

  @Override public Version version() {
    return PackageVersion.VERSION;
  }

  @Override public int getFormatFeatures() {
    return 0;
  }

  /**
     * Method that can be used to access tag id associated with
     * the most recently decoded value (whether completely, for
     * scalar values, or partially, for Objects/Arrays), if any.
     * If no tag was associated with it, -1 is returned.
     * 
     * @since 2.5
     */
  public int getCurrentTag() {
    return _tagValue;
  }

  @Override public int releaseBuffered(OutputStream out) throws IOException {
    int count = _inputEnd - _inputPtr;
    if (count < 1) {
      return 0;
    }
    int origPtr = _inputPtr;
    out.write(_inputBuffer, origPtr, count);
    return count;
  }

  @Override public Object getInputSource() {
    return _inputStream;
  }

  /**
     * Overridden since we do not really have character-based locations,
     * but we do have byte offset to specify.
     */
  @Override public JsonLocation getTokenLocation() {
    return new JsonLocation(_ioContext.getSourceReference(), _tokenInputTotal, -1, -1, (int) _tokenInputTotal);
  }

  /**
     * Overridden since we do not really have character-based locations,
     * but we do have byte offset to specify.
     */
  @Override public JsonLocation getCurrentLocation() {
    final long offset = _currInputProcessed + _inputPtr;
    return new JsonLocation(_ioContext.getSourceReference(), offset, -1, -1, (int) offset);
  }

  /**
     * Method that can be called to get the name associated with
     * the current event.
     */
  @Override public String getCurrentName() throws IOException {
    if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
      CBORReadContext parent = _parsingContext.getParent();
      return parent.getCurrentName();
    }
    return _parsingContext.getCurrentName();
  }

  @Override public void overrideCurrentName(String name) {
    CBORReadContext ctxt = _parsingContext;
    if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
      ctxt = ctxt.getParent();
    }
    try {
      ctxt.setCurrentName(name);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override public void close() throws IOException {
    if (!_closed) {
      _closed = true;
      _symbols.release();
      try {
        _closeInput();
      }  finally {
        _releaseBuffers();
      }
    }
  }

  @Override public boolean isClosed() {
    return _closed;
  }

  @Override public CBORReadContext getParsingContext() {
    return _parsingContext;
  }

  @Override public boolean hasTextCharacters() {
    if (_currToken == JsonToken.VALUE_STRING) {
      return _textBuffer.hasTextAsCharacters();
    }
    if (_currToken == JsonToken.FIELD_NAME) {
      return _nameCopied;
    }
    return false;
  }

  /**
     * Method called to release internal buffers owned by the base
     * reader. This may be called along with {@link #_closeInput} (for
     * example, when explicitly closing this reader instance), or
     * separately (if need be).
     */
  protected void _releaseBuffers() throws IOException {
    if (_bufferRecyclable) {
      byte[] buf = _inputBuffer;
      if (buf != null) {
        _inputBuffer = null;
        _ioContext.releaseReadIOBuffer(buf);
      }
    }
    _textBuffer.releaseBuffers();
    char[] buf = _nameCopyBuffer;
    if (buf != null) {
      _nameCopyBuffer = null;
      _ioContext.releaseNameCopyBuffer(buf);
    }
  }

  @Override public JsonToken nextToken() throws IOException {
    _numTypesValid = NR_UNKNOWN;
    if (_tokenIncomplete) {
      _skipIncomplete();
    }
    _tokenInputTotal = _currInputProcessed + _inputPtr;
    _binaryValue = null;
    if (_parsingContext.inObject()) {
      if (_currToken != JsonToken.FIELD_NAME) {
        _tagValue = -1;
        if (!_parsingContext.expectMoreValues()) {
          _parsingContext = _parsingContext.getParent();
          return (_currToken = JsonToken.END_OBJECT);
        }
        return (_currToken = _decodeFieldName());
      }
    } else {
      if (!_parsingContext.expectMoreValues()) {
        _tagValue = -1;
        _parsingContext = _parsingContext.getParent();
        return (_currToken = JsonToken.END_ARRAY);
      }
    }
    if (_inputPtr >= _inputEnd) {
      if (!loadMore()) {
        return _handleCBOREOF();
      }
    }
    int ch = _inputBuffer[_inputPtr++];
    int type = (ch >> 5) & 0x7;
    if (type == 6) {
      _tagValue = Integer.valueOf(_decodeTag(ch & 0x1F));
      if (_inputPtr >= _inputEnd) {
        if (!loadMore()) {
          return _handleCBOREOF();
        }
      }
      ch = _inputBuffer[_inputPtr++];
      type = (ch >> 5) & 0x7;
    } else {
      _tagValue = -1;
    }
    final int lowBits = ch & 0x1F;
    switch (type) {
      case 0:
      _numTypesValid = NR_INT;
      if (lowBits <= 23) {
        _numberInt = lowBits;
      } else {
        switch (lowBits - 24) {
          case 0:
          _numberInt = _decode8Bits();
          break;
          case 1:
          _numberInt = _decode16Bits();
          break;
          case 2:
          _numberInt = _decode32Bits();
          break;
          case 3:
          _numberLong = _decode64Bits();
          _numTypesValid = NR_LONG;
          break;
          default:
          _invalidToken(ch);
        }
      }
      return (_currToken = JsonToken.VALUE_NUMBER_INT);
      case 1:
      _numTypesValid = NR_INT;
      if (lowBits <= 23) {
        _numberInt = -lowBits - 1;
      } else {
        switch (lowBits - 24) {
          case 0:
          _numberInt = -_decode8Bits() - 1;
          break;
          case 1:
          _numberInt = -_decode16Bits() - 1;
          break;
          case 2:
          _numberInt = -_decode32Bits() - 1;
          break;
          case 3:
          _numberLong = -_decode64Bits() - 1L;
          _numTypesValid = NR_LONG;
          break;
          default:
          _invalidToken(ch);
        }
      }
      return (_currToken = JsonToken.VALUE_NUMBER_INT);
      case 2:
      _typeByte = ch;
      _tokenIncomplete = true;
      return (_currToken = JsonToken.VALUE_EMBEDDED_OBJECT);
      case 3:
      _typeByte = ch;
      _tokenIncomplete = true;
      return (_currToken = JsonToken.VALUE_STRING);
      case 4:
      _currToken = JsonToken.START_ARRAY;
      {
        int len = _decodeExplicitLength(lowBits);
        _parsingContext = _parsingContext.createChildArrayContext(len);
      }
      return _currToken;
      case 5:
      _currToken = JsonToken.START_OBJECT;
      {
        int len = _decodeExplicitLength(lowBits);
        _parsingContext = _parsingContext.createChildObjectContext(len);
      }
      return _currToken;
      case 6:
      _reportError("Multiple tags not allowed per value (first tag: " + _tagValue + ")");
      default:
      switch (lowBits) {
        case 20:
        return (_currToken = JsonToken.VALUE_FALSE);
        case 21:
        return (_currToken = JsonToken.VALUE_TRUE);
        case 22:
        return (_currToken = JsonToken.VALUE_NULL);
        case 25:
        {
          _numberFloat = (float) _decodeHalfSizeFloat();
          _numTypesValid = NR_FLOAT;
        }
        return (_currToken = JsonToken.VALUE_NUMBER_FLOAT);
        case 26:
        {
          _numberFloat = Float.intBitsToFloat(_decode32Bits());
          _numTypesValid = NR_FLOAT;
        }
        return (_currToken = JsonToken.VALUE_NUMBER_FLOAT);
        case 27:
        _numberDouble = Double.longBitsToDouble(_decode64Bits());
        _numTypesValid = NR_DOUBLE;
        return (_currToken = JsonToken.VALUE_NUMBER_FLOAT);
        case 31:
        if (_parsingContext.inArray()) {
          if (!_parsingContext.hasExpectedLength()) {
            _parsingContext = _parsingContext.getParent();
            return (_currToken = JsonToken.END_ARRAY);
          }
        }
        _reportUnexpectedBreak();
      }
      _invalidToken(ch);
    }
    return null;
  }

  protected JsonToken _handleCBOREOF() throws IOException {
    _tagValue = -1;
    close();
    return (_currToken = null);
  }

  protected String _numberToName(int ch, boolean neg) throws IOException {
    final int lowBits = ch & 0x1F;
    int i;
    if (lowBits <= 23) {
      i = lowBits;
    } else {
      switch (lowBits) {
        case 24:
        i = _decode8Bits();
        break;
        case 25:
        i = _decode16Bits();
        break;
        case 26:
        i = _decode32Bits();
        break;
        case 27:
        {
          long l = _decode64Bits();
          if (neg) {
            l = -l - 1L;
          }
          return String.valueOf(l);
        }
        default:
        throw _constructError("Invalid length indicator for ints (" + lowBits + "), token 0x" + Integer.toHexString(ch));
      }
    }
    if (neg) {
      i = -i - 1;
    }
    return String.valueOf(i);
  }

  /**
     * Method for forcing full read of current token, even if it might otherwise
     * only be read if data is accessed via {@link #getText} and similar methods.
     */
  public void finishToken() throws IOException {
    if (_tokenIncomplete) {
      _finishToken();
    }
  }

  protected void _invalidToken(int ch) throws JsonParseException {
    ch &= 0xFF;
    if (ch == 0xFF) {
      throw _constructError("Mismatched BREAK byte (0xFF): encountered where value expected");
    }
    throw _constructError("Invalid CBOR value token (first byte): 0x" + Integer.toHexString(ch));
  }

  @Override public boolean nextFieldName(SerializableString str) throws IOException {
    if (_parsingContext.inObject() && _currToken != JsonToken.FIELD_NAME) {
      _numTypesValid = NR_UNKNOWN;
      if (_tokenIncomplete) {
        _skipIncomplete();
      }
      _tokenInputTotal = _currInputProcessed + _inputPtr;
      _binaryValue = null;
      _tagValue = -1;
      if (!_parsingContext.expectMoreValues()) {
        _parsingContext = _parsingContext.getParent();
        _currToken = JsonToken.END_OBJECT;
        return false;
      }
      byte[] nameBytes = str.asQuotedUTF8();
      final int byteLen = nameBytes.length;
      int ptr = _inputPtr;
      if ((ptr + byteLen + 1) < _inputEnd) {
        final int ch = _inputBuffer[ptr++];
        if (((ch >> 5) & 0x7) == CBORConstants.MAJOR_TYPE_TEXT) {
          int lenMarker = ch & 0x1F;
          if (lenMarker <= 24) {
            if (lenMarker == 23) {
              lenMarker = _inputBuffer[ptr++] & 0xFF;
            }
            if (lenMarker == byteLen) {
              int i = 0;
              while (true) {
                if (i == lenMarker) {
                  _inputPtr = ptr + i;
                  _parsingContext.setCurrentName(str.getValue());
                  _currToken = JsonToken.FIELD_NAME;
                  return true;
                }
                if (nameBytes[i] != _inputBuffer[ptr + i]) {
                  break;
                }
                ++i;
              }
            }
          }
        }
      }
    }
    return (nextToken() == JsonToken.FIELD_NAME) && str.getValue().equals(getCurrentName());
  }

  @Override public String nextFieldName() throws IOException {
    if (_parsingContext.inObject() && _currToken != JsonToken.FIELD_NAME) {
      _numTypesValid = NR_UNKNOWN;
      if (_tokenIncomplete) {
        _skipIncomplete();
      }
      _tokenInputTotal = _currInputProcessed + _inputPtr;
      _binaryValue = null;
      _tagValue = -1;
      if (!_parsingContext.expectMoreValues()) {
        _parsingContext = _parsingContext.getParent();
        _currToken = JsonToken.END_OBJECT;
        return null;
      }
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      final int ch = _inputBuffer[_inputPtr++];
      final int type = ((ch >> 5) & 0x7);
      if (type != CBORConstants.MAJOR_TYPE_TEXT) {
        if (ch == -1) {
          if (!_parsingContext.hasExpectedLength()) {
            _parsingContext = _parsingContext.getParent();
            _currToken = JsonToken.END_OBJECT;
            return null;
          }
          _reportUnexpectedBreak();
        }
        _decodeNonStringName(ch);
        _currToken = JsonToken.FIELD_NAME;
        return getText();
      }
      final int lenMarker = ch & 0x1F;
      String name;
      if (lenMarker <= 23) {
        if (lenMarker == 0) {
          name = "";
        } else {
          name = _findDecodedFromSymbols(lenMarker);
          if (name != null) {
            _inputPtr += lenMarker;
          } else {
            name = _decodeShortName(lenMarker);
            name = _addDecodedToSymbols(lenMarker, name);
          }
        }
      } else {
        final int actualLen = _decodeExplicitLength(lenMarker);
        if (actualLen < 0) {
          name = _decodeChunkedName();
        } else {
          name = _decodeLongerName(actualLen);
        }
      }
      _parsingContext.setCurrentName(name);
      _currToken = JsonToken.FIELD_NAME;
      return name;
    }
    return (nextToken() == JsonToken.FIELD_NAME) ? getCurrentName() : null;
  }

  @Override public String nextTextValue() throws IOException {
    _numTypesValid = NR_UNKNOWN;
    if (_tokenIncomplete) {
      _skipIncomplete();
    }
    _tokenInputTotal = _currInputProcessed + _inputPtr;
    _binaryValue = null;
    _tagValue = -1;
    if (_parsingContext.inObject()) {
      if (_currToken != JsonToken.FIELD_NAME) {
        _tagValue = -1;
        if (!_parsingContext.expectMoreValues()) {
          _parsingContext = _parsingContext.getParent();
          _currToken = JsonToken.END_OBJECT;
          return null;
        }
        _currToken = _decodeFieldName();
        return null;
      }
    } else {
      if (!_parsingContext.expectMoreValues()) {
        _tagValue = -1;
        _parsingContext = _parsingContext.getParent();
        _currToken = JsonToken.END_ARRAY;
        return null;
      }
    }
    if (_inputPtr >= _inputEnd) {
      if (!loadMore()) {
        _handleCBOREOF();
        return null;
      }
    }
    int ch = _inputBuffer[_inputPtr++];
    int type = (ch >> 5) & 0x7;
    if (type == 6) {
      _tagValue = Integer.valueOf(_decodeTag(ch & 0x1F));
      if (_inputPtr >= _inputEnd) {
        if (!loadMore()) {
          _handleCBOREOF();
          return null;
        }
      }
      ch = _inputBuffer[_inputPtr++];
      type = (ch >> 5) & 0x7;
    } else {
      _tagValue = -1;
    }
    final int lowBits = ch & 0x1F;
    switch (type) {
      case 0:
      _numTypesValid = NR_INT;
      if (lowBits <= 23) {
        _numberInt = lowBits;
      } else {
        switch (lowBits - 24) {
          case 0:
          _numberInt = _decode8Bits();
          break;
          case 1:
          _numberInt = _decode16Bits();
          break;
          case 2:
          _numberInt = _decode32Bits();
          break;
          case 3:
          _numberLong = _decode64Bits();
          _numTypesValid = NR_LONG;
          break;
          default:
          _invalidToken(ch);
        }
      }
      _currToken = JsonToken.VALUE_NUMBER_INT;
      return null;
      case 1:
      _numTypesValid = NR_INT;
      if (lowBits <= 23) {
        _numberInt = -lowBits - 1;
      } else {
        switch (lowBits - 24) {
          case 0:
          _numberInt = -_decode8Bits() - 1;
          break;
          case 1:
          _numberInt = -_decode16Bits() - 1;
          break;
          case 2:
          _numberInt = -_decode32Bits() - 1;
          break;
          case 3:
          _numberLong = -_decode64Bits() - 1L;
          _numTypesValid = NR_LONG;
          break;
          default:
          _invalidToken(ch);
        }
      }
      _currToken = JsonToken.VALUE_NUMBER_INT;
      return null;
      case 2:
      _typeByte = ch;
      _tokenIncomplete = true;
      _currToken = JsonToken.VALUE_EMBEDDED_OBJECT;
      return null;
      case 3:
      _typeByte = ch;
      _tokenIncomplete = true;
      _currToken = JsonToken.VALUE_STRING;
      return _finishTextToken(ch);
      case 4:
      _currToken = JsonToken.START_ARRAY;
      {
        int len = _decodeExplicitLength(lowBits);
        _parsingContext = _parsingContext.createChildArrayContext(len);
      }
      return null;
      case 5:
      _currToken = JsonToken.START_OBJECT;
      {
        int len = _decodeExplicitLength(lowBits);
        _parsingContext = _parsingContext.createChildObjectContext(len);
      }
      return null;
      case 6:
      _reportError("Multiple tags not allowed per value (first tag: " + _tagValue + ")");
      default:
      switch (lowBits) {
        case 20:
        _currToken = JsonToken.VALUE_FALSE;
        return null;
        case 21:
        _currToken = JsonToken.VALUE_TRUE;
        return null;
        case 22:
        _currToken = JsonToken.VALUE_NULL;
        return null;
        case 25:
        {
          _numberFloat = _decodeHalfSizeFloat();
          _numTypesValid = NR_FLOAT;
        }
        _currToken = JsonToken.VALUE_NUMBER_FLOAT;
        return null;
        case 26:
        {
          _numberFloat = Float.intBitsToFloat(_decode32Bits());
          _numTypesValid = NR_FLOAT;
        }
        _currToken = JsonToken.VALUE_NUMBER_FLOAT;
        return null;
        case 27:
        _numberDouble = Double.longBitsToDouble(_decode64Bits());
        _numTypesValid = NR_DOUBLE;
        _currToken = JsonToken.VALUE_NUMBER_FLOAT;
        return null;
        case 31:
        if (_parsingContext.inArray()) {
          if (!_parsingContext.hasExpectedLength()) {
            _parsingContext = _parsingContext.getParent();
            _currToken = JsonToken.END_ARRAY;
            return null;
          }
        }
        _reportUnexpectedBreak();
      }
      _invalidToken(ch);
    }
    return (nextToken() == JsonToken.VALUE_STRING) ? getText() : null;
  }

  @Override public int nextIntValue(int defaultValue) throws IOException {
    if (nextToken() == JsonToken.VALUE_NUMBER_INT) {
      return getIntValue();
    }
    return defaultValue;
  }

  @Override public long nextLongValue(long defaultValue) throws IOException {
    if (nextToken() == JsonToken.VALUE_NUMBER_INT) {
      return getLongValue();
    }
    return defaultValue;
  }

  @Override public Boolean nextBooleanValue() throws IOException {
    switch (nextToken()) {
      case VALUE_TRUE:
      return Boolean.TRUE;
      case VALUE_FALSE:
      return Boolean.FALSE;
      default:
      return null;
    }
  }

  /**
     * Method for accessing textual representation of the current event;
     * if no current event (before first call to {@link #nextToken}, or
     * after encountering end-of-input), returns null.
     * Method can be called for any event.
     */
  @Override public String getText() throws IOException {
    JsonToken t = _currToken;
    if (_tokenIncomplete) {
      if (t == JsonToken.VALUE_STRING) {
        return _finishTextToken(_typeByte);
      }
    }
    if (t == JsonToken.VALUE_STRING) {
      return _textBuffer.contentsAsString();
    }
    if (t == null) {
      return null;
    }
    if (t == JsonToken.FIELD_NAME) {
      return _parsingContext.getCurrentName();
    }
    if (t.isNumeric()) {
      return getNumberValue().toString();
    }
    return _currToken.asString();
  }

  @Override public char[] getTextCharacters() throws IOException {
    if (_currToken != null) {
      if (_tokenIncomplete) {
        _finishToken();
      }
      switch (_currToken) {
        case VALUE_STRING:
        return _textBuffer.getTextBuffer();
        case FIELD_NAME:
        return _parsingContext.getCurrentName().toCharArray();
        case VALUE_NUMBER_INT:
        case VALUE_NUMBER_FLOAT:
        return getNumberValue().toString().toCharArray();
        default:
        return _currToken.asCharArray();
      }
    }
    return null;
  }

  @Override public int getTextLength() throws IOException {
    if (_currToken != null) {
      if (_tokenIncomplete) {
        _finishToken();
      }
      switch (_currToken) {
        case VALUE_STRING:
        return _textBuffer.size();
        case FIELD_NAME:
        return _parsingContext.getCurrentName().length();
        case VALUE_NUMBER_INT:
        case VALUE_NUMBER_FLOAT:
        return getNumberValue().toString().length();
        default:
        return _currToken.asCharArray().length;
      }
    }
    return 0;
  }

  @Override public int getTextOffset() throws IOException {
    return 0;
  }

  @Override public String getValueAsString() throws IOException {
    if (_tokenIncomplete) {
      if (_currToken == JsonToken.VALUE_STRING) {
        return _finishTextToken(_typeByte);
      }
    }
    if (_currToken == JsonToken.VALUE_STRING) {
      return _textBuffer.contentsAsString();
    }
    if (_currToken == null || _currToken == JsonToken.VALUE_NULL || !_currToken.isScalarValue()) {
      return null;
    }
    return getText();
  }

  @Override public String getValueAsString(String defaultValue) throws IOException {
    if (_currToken != JsonToken.VALUE_STRING) {
      if (_currToken == null || _currToken == JsonToken.VALUE_NULL || !_currToken.isScalarValue()) {
        return defaultValue;
      }
    }
    return getText();
  }

  @Override public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
    if (_tokenIncomplete) {
      _finishToken();
    }
    if (_currToken != JsonToken.VALUE_EMBEDDED_OBJECT) {
      _reportError("Current token (" + getCurrentToken() + ") not VALUE_EMBEDDED_OBJECT, can not access as binary");
    }
    return _binaryValue;
  }

  @Override public Object getEmbeddedObject() throws IOException {
    if (_tokenIncomplete) {
      _finishToken();
    }
    if (_currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
      return _binaryValue;
    }
    return null;
  }

  @Override public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
    if (_currToken != JsonToken.VALUE_EMBEDDED_OBJECT) {
      _reportError("Current token (" + getCurrentToken() + ") not VALUE_EMBEDDED_OBJECT, can not access as binary");
    }
    if (!_tokenIncomplete) {
      if (_binaryValue == null) {
        return 0;
      }
      final int len = _binaryValue.length;
      out.write(_binaryValue, 0, len);
      return len;
    }
    _tokenIncomplete = false;
    int len = _decodeExplicitLength(_typeByte & 0x1F);
    if (len >= 0) {
      return _readAndWriteBytes(out, len);
    }
    int total = 0;
    while (true) {
      len = _decodeChunkLength(CBORConstants.MAJOR_TYPE_BYTES);
      if (len < 0) {
        return total;
      }
      total += _readAndWriteBytes(out, len);
    }
  }

  private int _readAndWriteBytes(OutputStream out, int total) throws IOException {
    int left = total;
    while (left > 0) {
      int avail = _inputEnd - _inputPtr;
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
        avail = _inputEnd - _inputPtr;
      }
      int count = Math.min(avail, left);
      out.write(_inputBuffer, _inputPtr, count);
      _inputPtr += count;
      left -= count;
    }
    _tokenIncomplete = false;
    return total;
  }

  @Override public Number getNumberValue() throws IOException {
    if (_numTypesValid == NR_UNKNOWN) {
      _checkNumericValue(NR_UNKNOWN);
    }
    if (_currToken == JsonToken.VALUE_NUMBER_INT) {
      if ((_numTypesValid & NR_INT) != 0) {
        return _numberInt;
      }
      if ((_numTypesValid & NR_LONG) != 0) {
        return _numberLong;
      }
      if ((_numTypesValid & NR_BIGINT) != 0) {
        return _numberBigInt;
      }
      return _numberBigDecimal;
    }
    if ((_numTypesValid & NR_BIGDECIMAL) != 0) {
      return _numberBigDecimal;
    }
    if ((_numTypesValid & NR_DOUBLE) != 0) {
      return _numberDouble;
    }
    if ((_numTypesValid & NR_FLOAT) == 0) {
      _throwInternal();
    }
    return _numberFloat;
  }

  @Override public NumberType getNumberType() throws IOException {
    if (_numTypesValid == NR_UNKNOWN) {
      _checkNumericValue(NR_UNKNOWN);
    }
    if (_currToken == JsonToken.VALUE_NUMBER_INT) {
      if ((_numTypesValid & NR_INT) != 0) {
        return NumberType.INT;
      }
      if ((_numTypesValid & NR_LONG) != 0) {
        return NumberType.LONG;
      }
      return NumberType.BIG_INTEGER;
    }
    if ((_numTypesValid & NR_BIGDECIMAL) != 0) {
      return NumberType.BIG_DECIMAL;
    }
    if ((_numTypesValid & NR_DOUBLE) != 0) {
      return NumberType.DOUBLE;
    }
    return NumberType.FLOAT;
  }

  @Override public int getIntValue() throws IOException {
    if ((_numTypesValid & NR_INT) == 0) {
      if (_numTypesValid == NR_UNKNOWN) {
        _checkNumericValue(NR_INT);
      }
      if ((_numTypesValid & NR_INT) == 0) {
        convertNumberToInt();
      }
    }
    return _numberInt;
  }

  @Override public long getLongValue() throws IOException {
    if ((_numTypesValid & NR_LONG) == 0) {
      if (_numTypesValid == NR_UNKNOWN) {
        _checkNumericValue(NR_LONG);
      }
      if ((_numTypesValid & NR_LONG) == 0) {
        convertNumberToLong();
      }
    }
    return _numberLong;
  }

  @Override public BigInteger getBigIntegerValue() throws IOException {
    if ((_numTypesValid & NR_BIGINT) == 0) {
      if (_numTypesValid == NR_UNKNOWN) {
        _checkNumericValue(NR_BIGINT);
      }
      if ((_numTypesValid & NR_BIGINT) == 0) {
        convertNumberToBigInteger();
      }
    }
    return _numberBigInt;
  }

  @Override public float getFloatValue() throws IOException {
    if ((_numTypesValid & NR_FLOAT) == 0) {
      if (_numTypesValid == NR_UNKNOWN) {
        _checkNumericValue(NR_FLOAT);
      }
      if ((_numTypesValid & NR_FLOAT) == 0) {
        convertNumberToFloat();
      }
    }
    return _numberFloat;
  }

  @Override public double getDoubleValue() throws IOException {
    if ((_numTypesValid & NR_DOUBLE) == 0) {
      if (_numTypesValid == NR_UNKNOWN) {
        _checkNumericValue(NR_DOUBLE);
      }
      if ((_numTypesValid & NR_DOUBLE) == 0) {
        convertNumberToDouble();
      }
    }
    return _numberDouble;
  }

  @Override public BigDecimal getDecimalValue() throws IOException {
    if ((_numTypesValid & NR_BIGDECIMAL) == 0) {
      if (_numTypesValid == NR_UNKNOWN) {
        _checkNumericValue(NR_BIGDECIMAL);
      }
      if ((_numTypesValid & NR_BIGDECIMAL) == 0) {
        convertNumberToBigDecimal();
      }
    }
    return _numberBigDecimal;
  }

  protected void _checkNumericValue(int expType) throws IOException {
    if (_currToken == JsonToken.VALUE_NUMBER_INT || _currToken == JsonToken.VALUE_NUMBER_FLOAT) {
      return;
    }
    _reportError("Current token (" + getCurrentToken() + ") not numeric, can not use numeric value accessors");
  }

  protected void convertNumberToInt() throws IOException {
    if ((_numTypesValid & NR_LONG) != 0) {
      int result = (int) _numberLong;
      if (((long) result) != _numberLong) {
        _reportError("Numeric value (" + getText() + ") out of range of int");
      }
      _numberInt = result;
    } else {
      if ((_numTypesValid & NR_BIGINT) != 0) {
        if (BI_MIN_INT.compareTo(_numberBigInt) > 0 || BI_MAX_INT.compareTo(_numberBigInt) < 0) {
          reportOverflowInt();
        }
        _numberInt = _numberBigInt.intValue();
      } else {
        if ((_numTypesValid & NR_DOUBLE) != 0) {
          if (_numberDouble < MIN_INT_D || _numberDouble > MAX_INT_D) {
            reportOverflowInt();
          }
          _numberInt = (int) _numberDouble;
        } else {
          if ((_numTypesValid & NR_FLOAT) != 0) {
            if (_numberFloat < MIN_INT_D || _numberFloat > MAX_INT_D) {
              reportOverflowInt();
            }
            _numberInt = (int) _numberFloat;
          } else {
            if ((_numTypesValid & NR_BIGDECIMAL) != 0) {
              if (BD_MIN_INT.compareTo(_numberBigDecimal) > 0 || BD_MAX_INT.compareTo(_numberBigDecimal) < 0) {
                reportOverflowInt();
              }
              _numberInt = _numberBigDecimal.intValue();
            } else {
              _throwInternal();
            }
          }
        }
      }
    }
    _numTypesValid |= NR_INT;
  }

  protected void convertNumberToLong() throws IOException {
    if ((_numTypesValid & NR_INT) != 0) {
      _numberLong = (long) _numberInt;
    } else {
      if ((_numTypesValid & NR_BIGINT) != 0) {
        if (BI_MIN_LONG.compareTo(_numberBigInt) > 0 || BI_MAX_LONG.compareTo(_numberBigInt) < 0) {
          reportOverflowLong();
        }
        _numberLong = _numberBigInt.longValue();
      } else {
        if ((_numTypesValid & NR_DOUBLE) != 0) {
          if (_numberDouble < MIN_LONG_D || _numberDouble > MAX_LONG_D) {
            reportOverflowLong();
          }
          _numberLong = (long) _numberDouble;
        } else {
          if ((_numTypesValid & NR_FLOAT) != 0) {
            if (_numberFloat < MIN_LONG_D || _numberFloat > MAX_LONG_D) {
              reportOverflowInt();
            }
            _numberLong = (long) _numberFloat;
          } else {
            if ((_numTypesValid & NR_BIGDECIMAL) != 0) {
              if (BD_MIN_LONG.compareTo(_numberBigDecimal) > 0 || BD_MAX_LONG.compareTo(_numberBigDecimal) < 0) {
                reportOverflowLong();
              }
              _numberLong = _numberBigDecimal.longValue();
            } else {
              _throwInternal();
            }
          }
        }
      }
    }
    _numTypesValid |= NR_LONG;
  }

  protected void convertNumberToBigInteger() throws IOException {
    if ((_numTypesValid & NR_BIGDECIMAL) != 0) {
      _numberBigInt = _numberBigDecimal.toBigInteger();
    } else {
      if ((_numTypesValid & NR_LONG) != 0) {
        _numberBigInt = BigInteger.valueOf(_numberLong);
      } else {
        if ((_numTypesValid & NR_INT) != 0) {
          _numberBigInt = BigInteger.valueOf(_numberInt);
        } else {
          if ((_numTypesValid & NR_DOUBLE) != 0) {
            _numberBigInt = BigDecimal.valueOf(_numberDouble).toBigInteger();
          } else {
            if ((_numTypesValid & NR_FLOAT) != 0) {
              _numberBigInt = BigDecimal.valueOf(_numberFloat).toBigInteger();
            } else {
              _throwInternal();
            }
          }
        }
      }
    }
    _numTypesValid |= NR_BIGINT;
  }

  protected void convertNumberToFloat() throws IOException {
    if ((_numTypesValid & NR_BIGDECIMAL) != 0) {
      _numberFloat = _numberBigDecimal.floatValue();
    } else {
      if ((_numTypesValid & NR_BIGINT) != 0) {
        _numberFloat = _numberBigInt.floatValue();
      } else {
        if ((_numTypesValid & NR_DOUBLE) != 0) {
          _numberFloat = (float) _numberDouble;
        } else {
          if ((_numTypesValid & NR_LONG) != 0) {
            _numberFloat = (float) _numberLong;
          } else {
            if ((_numTypesValid & NR_INT) != 0) {
              _numberFloat = (float) _numberInt;
            } else {
              _throwInternal();
            }
          }
        }
      }
    }
    _numTypesValid |= NR_FLOAT;
  }

  protected void convertNumberToDouble() throws IOException {
    if ((_numTypesValid & NR_BIGDECIMAL) != 0) {
      _numberDouble = _numberBigDecimal.doubleValue();
    } else {
      if ((_numTypesValid & NR_FLOAT) != 0) {
        _numberDouble = (double) _numberFloat;
      } else {
        if ((_numTypesValid & NR_BIGINT) != 0) {
          _numberDouble = _numberBigInt.doubleValue();
        } else {
          if ((_numTypesValid & NR_LONG) != 0) {
            _numberDouble = (double) _numberLong;
          } else {
            if ((_numTypesValid & NR_INT) != 0) {
              _numberDouble = (double) _numberInt;
            } else {
              _throwInternal();
            }
          }
        }
      }
    }
    _numTypesValid |= NR_DOUBLE;
  }

  protected void convertNumberToBigDecimal() throws IOException {
    if ((_numTypesValid & (NR_DOUBLE | NR_FLOAT)) != 0) {
      _numberBigDecimal = NumberInput.parseBigDecimal(getText());
    } else {
      if ((_numTypesValid & NR_BIGINT) != 0) {
        _numberBigDecimal = new BigDecimal(_numberBigInt);
      } else {
        if ((_numTypesValid & NR_LONG) != 0) {
          _numberBigDecimal = BigDecimal.valueOf(_numberLong);
        } else {
          if ((_numTypesValid & NR_INT) != 0) {
            _numberBigDecimal = BigDecimal.valueOf(_numberInt);
          } else {
            _throwInternal();
          }
        }
      }
    }
    _numTypesValid |= NR_BIGDECIMAL;
  }

  protected void reportOverflowInt() throws IOException {
    _reportError("Numeric value (" + getText() + ") out of range of int (" + Integer.MIN_VALUE + " - " + Integer.MAX_VALUE + ")");
  }

  protected void reportOverflowLong() throws IOException {
    _reportError("Numeric value (" + getText() + ") out of range of long (" + Long.MIN_VALUE + " - " + Long.MAX_VALUE + ")");
  }

  /**
     * Method called to finish parsing of a token so that token contents
     * are retriable
     */
  protected void _finishToken() throws IOException {
    _tokenIncomplete = false;
    int ch = _typeByte;
    final int type = ((ch >> 5) & 0x7);
    ch &= 0x1F;
    if (type != CBORConstants.MAJOR_TYPE_TEXT) {
      if (type == CBORConstants.MAJOR_TYPE_BYTES) {
        _binaryValue = _finishBytes(_decodeExplicitLength(ch));
        return;
      }
      _throwInternal();
    }
    final int len = _decodeExplicitLength(ch);
    if (len <= 0) {
      if (len < 0) {
        _finishChunkedText();
      } else {
        _textBuffer.resetWithEmpty();
      }
      return;
    }
    if (len > (_inputEnd - _inputPtr)) {
      if (len >= _inputBuffer.length) {
        _finishLongText(len);
        return;
      }
      _loadToHaveAtLeast(len);
    }
    _finishShortText(len);
  }

  /**
     * @since 2.6
     */
  protected String _finishTextToken(int ch) throws IOException {
    _tokenIncomplete = false;
    final int type = ((ch >> 5) & 0x7);
    ch &= 0x1F;
    if (type != CBORConstants.MAJOR_TYPE_TEXT) {
      _throwInternal();
    }
    final int len = _decodeExplicitLength(ch);
    if (len <= 0) {
      if (len == 0) {
        _textBuffer.resetWithEmpty();
        return "";
      }
      _finishChunkedText();
      return _textBuffer.contentsAsString();
    }
    if (len > (_inputEnd - _inputPtr)) {
      if (len >= _inputBuffer.length) {
        _finishLongText(len);
        return _textBuffer.contentsAsString();
      }
      _loadToHaveAtLeast(len);
    }
    return _finishShortText(len);
  }

  private final String _finishShortText(int len) throws IOException {
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    if (outBuf.length < len) {
      outBuf = _textBuffer.expandCurrentSegment(len);
    }
    int outPtr = 0;
    int inPtr = _inputPtr;
    _inputPtr += len;
    final byte[] inputBuf = _inputBuffer;
    final int end = inPtr + len;
    int i;
    while ((i = inputBuf[inPtr]) >= 0) {
      outBuf[outPtr++] = (char) i;
      if (++inPtr == end) {
        return _textBuffer.setCurrentAndReturn(outPtr);
      }
    }
    final int[] codes = UTF8_UNIT_CODES;
    do {
      i = inputBuf[inPtr++] & 0xFF;
      switch (codes[i]) {
        case 0:
        break;
        case 1:
        i = ((i & 0x1F) << 6) | (inputBuf[inPtr++] & 0x3F);
        break;
        case 2:
        i = ((i & 0x0F) << 12) | ((inputBuf[inPtr++] & 0x3F) << 6) | (inputBuf[inPtr++] & 0x3F);
        break;
        case 3:
        i = ((i & 0x07) << 18) | ((inputBuf[inPtr++] & 0x3F) << 12) | ((inputBuf[inPtr++] & 0x3F) << 6) | (inputBuf[inPtr++] & 0x3F);
        i -= 0x10000;
        outBuf[outPtr++] = (char) (0xD800 | (i >> 10));
        i = 0xDC00 | (i & 0x3FF);
        break;
        default:
        _reportError("Invalid byte " + Integer.toHexString(i) + " in Unicode text block");
      }
      outBuf[outPtr++] = (char) i;
    } while(inPtr < end);
    return _textBuffer.setCurrentAndReturn(outPtr);
  }

  private final void _finishLongText(int len) throws IOException {
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    int outPtr = 0;
    final int[] codes = UTF8_UNIT_CODES;
    int outEnd = outBuf.length;
    while (--len >= 0) {
      int c = _nextByte() & 0xFF;
      int code = codes[c];
      if (code == 0 && outPtr < outEnd) {
        outBuf[outPtr++] = (char) c;
        continue;
      }
      if ((len -= code) < 0) {
        throw _constructError("Malformed UTF-8 character at end of long (non-chunked) text segment");
      }
      switch (code) {
        case 0:
        break;
        case 1:
        {
          int d = _nextByte();
          if ((d & 0xC0) != 0x080) {
            _reportInvalidOther(d & 0xFF, _inputPtr);
          }
          c = ((c & 0x1F) << 6) | (d & 0x3F);
        }
        break;
        case 2:
        c = _decodeUTF8_3(c);
        break;
        case 3:
        c = _decodeUTF8_4(c);
        outBuf[outPtr++] = (char) (0xD800 | (c >> 10));
        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.finishCurrentSegment();
          outPtr = 0;
          outEnd = outBuf.length;
        }
        c = 0xDC00 | (c & 0x3FF);
        break;
        default:
        _reportInvalidChar(c);
      }
      if (outPtr >= outEnd) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
        outEnd = outBuf.length;
      }
      outBuf[outPtr++] = (char) c;
    }
    _textBuffer.setCurrentLength(outPtr);
  }

  private final void _finishChunkedText() throws IOException {
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    int outPtr = 0;
    final int[] codes = UTF8_UNIT_CODES;
    int outEnd = outBuf.length;
    final byte[] input = _inputBuffer;
    _chunkEnd = _inputPtr;
    _chunkLeft = 0;
    while (true) {
      if (_inputPtr >= _chunkEnd) {
        if (_chunkLeft == 0) {
          int len = _decodeChunkLength(CBORConstants.MAJOR_TYPE_TEXT);
          if (len < 0) {
            break;
          }
          _chunkLeft = len;
          int end = _inputPtr + len;
          if (end <= _inputEnd) {
            _chunkLeft = 0;
            _chunkEnd = end;
          } else {
            _chunkLeft = (end - _inputEnd);
            _chunkEnd = _inputEnd;
          }
        }
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
          int end = _inputPtr + _chunkLeft;
          if (end <= _inputEnd) {
            _chunkLeft = 0;
            _chunkEnd = end;
          } else {
            _chunkLeft = (end - _inputEnd);
            _chunkEnd = _inputEnd;
          }
        }
      }
      int c = input[_inputPtr++] & 0xFF;
      int code = codes[c];
      if (code == 0 && outPtr < outEnd) {
        outBuf[outPtr++] = (char) c;
        continue;
      }
      switch (code) {
        case 0:
        break;
        case 1:
        {
          int d = _nextChunkedByte();
          if ((d & 0xC0) != 0x080) {
            _reportInvalidOther(d & 0xFF, _inputPtr);
          }
          c = ((c & 0x1F) << 6) | (d & 0x3F);
        }
        break;
        case 2:
        c = _decodeChunkedUTF8_3(c);
        break;
        case 3:
        c = _decodeChunkedUTF8_4(c);
        outBuf[outPtr++] = (char) (0xD800 | (c >> 10));
        if (outPtr >= outBuf.length) {
          outBuf = _textBuffer.finishCurrentSegment();
          outPtr = 0;
          outEnd = outBuf.length;
        }
        c = 0xDC00 | (c & 0x3FF);
        break;
        default:
        _reportInvalidChar(c);
      }
      if (outPtr >= outEnd) {
        outBuf = _textBuffer.finishCurrentSegment();
        outPtr = 0;
        outEnd = outBuf.length;
      }
      outBuf[outPtr++] = (char) c;
    }
    _textBuffer.setCurrentLength(outPtr);
  }

  private final int _nextByte() throws IOException {
    int inPtr = _inputPtr;
    if (inPtr < _inputEnd) {
      int ch = _inputBuffer[inPtr];
      _inputPtr = inPtr + 1;
      return ch;
    }
    loadMoreGuaranteed();
    return _inputBuffer[_inputPtr++];
  }

  private final int _nextChunkedByte() throws IOException {
    int inPtr = _inputPtr;
    if (inPtr >= _chunkEnd) {
      return _nextChunkedByte2();
    }
    int ch = _inputBuffer[inPtr];
    _inputPtr = inPtr + 1;
    return ch;
  }

  private final int _nextChunkedByte2() throws IOException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
      if (_chunkLeft > 0) {
        int end = _inputPtr + _chunkLeft;
        if (end <= _inputEnd) {
          _chunkLeft = 0;
          _chunkEnd = end;
        } else {
          _chunkLeft = (end - _inputEnd);
          _chunkEnd = _inputEnd;
        }
        return _inputBuffer[_inputPtr++];
      }
    }
    int len = _decodeChunkLength(CBORConstants.MAJOR_TYPE_TEXT);
    if (len < 0) {
      _reportInvalidEOF(": chunked Text ends with partial UTF-8 character");
    }
    int end = _inputPtr + len;
    if (end <= _inputEnd) {
      _chunkLeft = 0;
      _chunkEnd = end;
    } else {
      _chunkLeft = (end - _inputEnd);
      _chunkEnd = _inputEnd;
    }
    return _inputBuffer[_inputPtr++];
  }

  @SuppressWarnings(value = { "resource" }) protected byte[] _finishBytes(int len) throws IOException {
    if (len >= 0) {
      if (len == 0) {
        return NO_BYTES;
      }
      byte[] b = new byte[len];
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      int ptr = 0;
      while (true) {
        int toAdd = Math.min(len, _inputEnd - _inputPtr);
        System.arraycopy(_inputBuffer, _inputPtr, b, ptr, toAdd);
        _inputPtr += toAdd;
        ptr += toAdd;
        len -= toAdd;
        if (len <= 0) {
          return b;
        }
        loadMoreGuaranteed();
      }
    }
    ByteArrayBuilder bb = _getByteArrayBuilder();
    while (true) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      int ch = _inputBuffer[_inputPtr++] & 0xFF;
      if (ch == 0xFF) {
        break;
      }
      int type = (ch >> 5);
      if (type != CBORConstants.MAJOR_TYPE_BYTES) {
        throw _constructError("Mismatched chunk in chunked content: expected " + CBORConstants.MAJOR_TYPE_BYTES + " but encountered " + type);
      }
      len = _decodeExplicitLength(ch & 0x1F);
      if (len < 0) {
        throw _constructError("Illegal chunked-length indicator within chunked-length value (type " + CBORConstants.MAJOR_TYPE_BYTES + ")");
      }
      while (len > 0) {
        int avail = _inputEnd - _inputPtr;
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
          avail = _inputEnd - _inputPtr;
        }
        int count = Math.min(avail, len);
        bb.write(_inputBuffer, _inputPtr, count);
        _inputPtr += count;
        len -= count;
      }
    }
    return bb.toByteArray();
  }

  protected final JsonToken _decodeFieldName() throws IOException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    final int ch = _inputBuffer[_inputPtr++];
    final int type = ((ch >> 5) & 0x7);
    if (type != CBORConstants.MAJOR_TYPE_TEXT) {
      if (ch == -1) {
        if (!_parsingContext.hasExpectedLength()) {
          _parsingContext = _parsingContext.getParent();
          return JsonToken.END_OBJECT;
        }
        _reportUnexpectedBreak();
      }
      _decodeNonStringName(ch);
      return JsonToken.FIELD_NAME;
    }
    final int lenMarker = ch & 0x1F;
    String name;
    if (lenMarker <= 23) {
      if (lenMarker == 0) {
        name = "";
      } else {
        name = _findDecodedFromSymbols(lenMarker);
        if (name != null) {
          _inputPtr += lenMarker;
        } else {
          name = _decodeShortName(lenMarker);
          name = _addDecodedToSymbols(lenMarker, name);
        }
      }
    } else {
      final int actualLen = _decodeExplicitLength(lenMarker);
      if (actualLen < 0) {
        name = _decodeChunkedName();
      } else {
        name = _decodeLongerName(actualLen);
      }
    }
    _parsingContext.setCurrentName(name);
    return JsonToken.FIELD_NAME;
  }

  private final String _decodeShortName(int len) throws IOException {
    int outPtr = 0;
    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
    if (outBuf.length < len) {
      outBuf = _textBuffer.expandCurrentSegment(len);
    }
    int inPtr = _inputPtr;
    _inputPtr += len;
    final int[] codes = UTF8_UNIT_CODES;
    final byte[] inBuf = _inputBuffer;
    final int end = inPtr + len;
    while (true) {
      int i = inBuf[inPtr] & 0xFF;
      int code = codes[i];
      if (code != 0) {
        break;
      }
      outBuf[outPtr++] = (char) i;
      if (++inPtr == end) {
        return _textBuffer.setCurrentAndReturn(outPtr);
      }
    }
    while (inPtr < end) {
      int i = inBuf[inPtr++] & 0xFF;
      int code = codes[i];
      if (code != 0) {
        switch (code) {
          case 1:
          i = ((i & 0x1F) << 6) | (inBuf[inPtr++] & 0x3F);
          break;
          case 2:
          i = ((i & 0x0F) << 12) | ((inBuf[inPtr++] & 0x3F) << 6) | (inBuf[inPtr++] & 0x3F);
          break;
          case 3:
          i = ((i & 0x07) << 18) | ((inBuf[inPtr++] & 0x3F) << 12) | ((inBuf[inPtr++] & 0x3F) << 6) | (inBuf[inPtr++] & 0x3F);
          i -= 0x10000;
          outBuf[outPtr++] = (char) (0xD800 | (i >> 10));
          i = 0xDC00 | (i & 0x3FF);
          break;
          default:
          _reportError("Invalid byte " + Integer.toHexString(i) + " in Object name");
        }
      }
      outBuf[outPtr++] = (char) i;
    }
    return _textBuffer.setCurrentAndReturn(outPtr);
  }

  private final String _decodeLongerName(int len) throws IOException {
    if ((_inputEnd - _inputPtr) < len) {
      if (len >= _inputBuffer.length) {
        _finishLongText(len);
        return _textBuffer.contentsAsString();
      }
      _loadToHaveAtLeast(len);
    }
    String name = _findDecodedFromSymbols(len);
    if (name != null) {
      _inputPtr += len;
      return name;
    }
    name = _decodeShortName(len);
    return _addDecodedToSymbols(len, name);
  }

  private final String _decodeChunkedName() throws IOException {
    _finishChunkedText();
    return _textBuffer.contentsAsString();
  }

  /**
     * Method that handles initial token type recognition for token
     * that has to be either FIELD_NAME or END_OBJECT.
     */
  protected final void _decodeNonStringName(int ch) throws IOException {
    final int type = ((ch >> 5) & 0x7);
    String name;
    if (type == CBORConstants.MAJOR_TYPE_INT_POS) {
      name = _numberToName(ch, false);
    } else {
      if (type == CBORConstants.MAJOR_TYPE_INT_NEG) {
        name = _numberToName(ch, true);
      } else {
        if (type == CBORConstants.MAJOR_TYPE_BYTES) {
          final int blen = _decodeExplicitLength(ch & 0x1F);
          byte[] b = _finishBytes(blen);
          name = new String(b, UTF8);
        } else {
          if ((ch & 0xFF) == CBORConstants.INT_BREAK) {
            _reportUnexpectedBreak();
          }
          throw _constructError("Unsupported major type (" + type + ") for CBOR Objects, not (yet?) supported, only Strings");
        }
      }
    }
    _parsingContext.setCurrentName(name);
  }

  private final String _findDecodedFromSymbols(final int len) throws IOException {
    if ((_inputEnd - _inputPtr) < len) {
      _loadToHaveAtLeast(len);
    }
    if (len < 5) {
      int inPtr = _inputPtr;
      final byte[] inBuf = _inputBuffer;
      int q = inBuf[inPtr] & 0xFF;
      if (len > 1) {
        q = (q << 8) + (inBuf[++inPtr] & 0xFF);
        if (len > 2) {
          q = (q << 8) + (inBuf[++inPtr] & 0xFF);
          if (len > 3) {
            q = (q << 8) + (inBuf[++inPtr] & 0xFF);
          }
        }
      }
      _quad1 = q;
      return _symbols.findName(q);
    }
    final byte[] inBuf = _inputBuffer;
    int inPtr = _inputPtr;
    int q1 = (inBuf[inPtr++] & 0xFF);
    q1 = (q1 << 8) | (inBuf[inPtr++] & 0xFF);
    q1 = (q1 << 8) | (inBuf[inPtr++] & 0xFF);
    q1 = (q1 << 8) | (inBuf[inPtr++] & 0xFF);
    if (len < 9) {
      int q2 = (inBuf[inPtr++] & 0xFF);
      int left = len - 5;
      if (left > 0) {
        q2 = (q2 << 8) + (inBuf[inPtr++] & 0xFF);
        if (left > 1) {
          q2 = (q2 << 8) + (inBuf[inPtr++] & 0xFF);
          if (left > 2) {
            q2 = (q2 << 8) + (inBuf[inPtr++] & 0xFF);
          }
        }
      }
      _quad1 = q1;
      _quad2 = q2;
      return _symbols.findName(q1, q2);
    }
    int q2 = (inBuf[inPtr++] & 0xFF);
    q2 = (q2 << 8) | (inBuf[inPtr++] & 0xFF);
    q2 = (q2 << 8) | (inBuf[inPtr++] & 0xFF);
    q2 = (q2 << 8) | (inBuf[inPtr++] & 0xFF);
    if (len < 13) {
      int q3 = (inBuf[inPtr++] & 0xFF);
      int left = len - 9;
      if (left > 0) {
        q3 = (q3 << 8) + (inBuf[inPtr++] & 0xFF);
        if (left > 1) {
          q3 = (q3 << 8) + (inBuf[inPtr++] & 0xFF);
          if (left > 2) {
            q3 = (q3 << 8) + (inBuf[inPtr++] & 0xFF);
          }
        }
      }
      _quad1 = q1;
      _quad2 = q2;
      _quad3 = q3;
      return _symbols.findName(q1, q2, q3);
    }
    return _findDecodedLong(len, q1, q2);
  }

  /**
     * Method for locating names longer than 8 bytes (in UTF-8)
     */
  private final String _findDecodedLong(int len, int q1, int q2) throws IOException {
    {
      int bufLen = (len + 3) >> 2;
      if (bufLen > _quadBuffer.length) {
        _quadBuffer = _growArrayTo(_quadBuffer, bufLen);
      }
    }
    _quadBuffer[0] = q1;
    _quadBuffer[1] = q2;
    int offset = 2;
    int inPtr = _inputPtr + 8;
    len -= 8;
    final byte[] inBuf = _inputBuffer;
    do {
      int q = (inBuf[inPtr++] & 0xFF);
      q = (q << 8) | inBuf[inPtr++] & 0xFF;
      q = (q << 8) | inBuf[inPtr++] & 0xFF;
      q = (q << 8) | inBuf[inPtr++] & 0xFF;
      _quadBuffer[offset++] = q;
    } while((len -= 4) > 3);
    if (len > 0) {
      int q = inBuf[inPtr] & 0xFF;
      if (len > 1) {
        q = (q << 8) + (inBuf[++inPtr] & 0xFF);
        if (len > 2) {
          q = (q << 8) + (inBuf[++inPtr] & 0xFF);
        }
      }
      _quadBuffer[offset++] = q;
    }
    return _symbols.findName(_quadBuffer, offset);
  }

  private final String _addDecodedToSymbols(int len, String name) {
    if (len < 5) {
      return _symbols.addName(name, _quad1);
    }
    if (len < 9) {
      return _symbols.addName(name, _quad1, _quad2);
    }
    if (len < 13) {
      return _symbols.addName(name, _quad1, _quad2, _quad3);
    }
    int qlen = (len + 3) >> 2;
    return _symbols.addName(name, _quadBuffer, qlen);
  }

  private static int[] _growArrayTo(int[] arr, int minSize) {
    return Arrays.copyOf(arr, minSize + 4);
  }

  /**
     * Method called to skip remainders of an incomplete token, when
     * contents themselves will not be needed any more.
     * Only called or byte array and text.
     */
  protected void _skipIncomplete() throws IOException {
    _tokenIncomplete = false;
    final int type = ((_typeByte >> 5) & 0x7);
    if (type != CBORConstants.MAJOR_TYPE_TEXT && type == CBORConstants.MAJOR_TYPE_TEXT) {
      _throwInternal();
    }
    final int lowBits = _typeByte & 0x1F;
    if (lowBits <= 23) {
      if (lowBits > 0) {
        _skipBytes(lowBits);
      }
      return;
    }
    switch (lowBits) {
      case 24:
      _skipBytes(_decode8Bits());
      break;
      case 25:
      _skipBytes(_decode16Bits());
      break;
      case 26:
      _skipBytes(_decode32Bits());
      break;
      case 27:
      _skipBytesL(_decode64Bits());
      break;
      case 31:
      _skipChunked(type);
      break;
      default:
      _invalidToken(_typeByte);
    }
  }

  protected void _skipChunked(int expectedType) throws IOException {
    while (true) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      int ch = _inputBuffer[_inputPtr++] & 0xFF;
      if (ch == 0xFF) {
        return;
      }
      int type = (ch >> 5);
      if (type != expectedType) {
        throw _constructError("Mismatched chunk in chunked content: expected " + expectedType + " but encountered " + type);
      }
      final int lowBits = ch & 0x1F;
      if (lowBits <= 23) {
        if (lowBits > 0) {
          _skipBytes(lowBits);
        }
        continue;
      }
      switch (lowBits) {
        case 24:
        _skipBytes(_decode8Bits());
        break;
        case 25:
        _skipBytes(_decode16Bits());
        break;
        case 26:
        _skipBytes(_decode32Bits());
        break;
        case 27:
        _skipBytesL(_decode64Bits());
        break;
        case 31:
        throw _constructError("Illegal chunked-length indicator within chunked-length value (type " + expectedType + ")");
        default:
        _invalidToken(_typeByte);
      }
    }
  }

  protected void _skipBytesL(long llen) throws IOException {
    while (llen > MAX_INT_L) {
      _skipBytes((int) MAX_INT_L);
      llen -= MAX_INT_L;
    }
    _skipBytes((int) llen);
  }

  protected void _skipBytes(int len) throws IOException {
    while (true) {
      int toAdd = Math.min(len, _inputEnd - _inputPtr);
      _inputPtr += toAdd;
      len -= toAdd;
      if (len <= 0) {
        return;
      }
      loadMoreGuaranteed();
    }
  }

  private final int _decodeTag(int lowBits) throws IOException {
    if (lowBits <= 23) {
      return lowBits;
    }
    switch (lowBits - 24) {
      case 0:
      return _decode8Bits();
      case 1:
      return _decode16Bits();
      case 2:
      return _decode32Bits();
      case 3:
      long l = _decode64Bits();
      if (l < MIN_INT_L || l > MAX_INT_L) {
        _reportError("Illegal Tag value: " + l);
      }
      return (int) l;
    }
    throw _constructError("Invalid low bits for Tag token: 0x" + Integer.toHexString(lowBits));
  }

  /**
     * Method used to decode explicit length of a variable-length value
     * (or, for indefinite/chunked, indicate that one is not known).
     * Note that long (64-bit) length is only allowed if it fits in
     * 32-bit signed int, for now; expectation being that longer values
     * are always encoded as chunks.
     */
  private final int _decodeExplicitLength(int lowBits) throws IOException {
    if (lowBits == 31) {
      return -1;
    }
    if (lowBits <= 23) {
      return lowBits;
    }
    switch (lowBits - 24) {
      case 0:
      return _decode8Bits();
      case 1:
      return _decode16Bits();
      case 2:
      return _decode32Bits();
      case 3:
      long l = _decode64Bits();
      if (l < 0 || l > MAX_INT_L) {
        throw _constructError("Illegal length for " + getCurrentToken() + ": " + l);
      }
      return (int) l;
    }
    throw _constructError("Invalid length for " + getCurrentToken() + ": 0x" + Integer.toHexString(lowBits));
  }

  private int _decodeChunkLength(int expType) throws IOException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    int ch = (int) _inputBuffer[_inputPtr++] & 0xFF;
    if (ch == CBORConstants.INT_BREAK) {
      return -1;
    }
    int type = (ch >> 5);
    if (type != expType) {
      throw _constructError("Mismatched chunk in chunked content: expected " + expType + " but encountered " + type + " (byte 0x" + Integer.toHexString(ch) + ")");
    }
    int len = _decodeExplicitLength(ch & 0x1F);
    if (len < 0) {
      throw _constructError("Illegal chunked-length indicator within chunked-length value (type " + expType + ")");
    }
    return len;
  }

  private float _decodeHalfSizeFloat() throws IOException {
    int i16 = _decode16Bits() & 0xFFFF;
    boolean neg = (i16 >> 15) != 0;
    int e = (i16 >> 10) & 0x1F;
    int f = i16 & 0x03FF;
    if (e == 0) {
      float result = (float) (MATH_POW_2_NEG14 * (f / MATH_POW_2_10));
      return neg ? -result : result;
    }
    if (e == 0x1F) {
      if (f != 0) {
        return Float.NaN;
      }
      return neg ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
    }
    float result = (float) (Math.pow(2, e - 15) * (1 + f / MATH_POW_2_10));
    return neg ? -result : result;
  }

  private final int _decode8Bits() throws IOException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    return _inputBuffer[_inputPtr++] & 0xFF;
  }

  private final int _decode16Bits() throws IOException {
    int ptr = _inputPtr;
    if ((ptr + 1) >= _inputEnd) {
      return _slow16();
    }
    final byte[] b = _inputBuffer;
    int v = ((b[ptr] & 0xFF) << 8) + (b[ptr + 1] & 0xFF);
    _inputPtr = ptr + 2;
    return v;
  }

  private final int _slow16() throws IOException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    int v = (_inputBuffer[_inputPtr++] & 0xFF);
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    return (v << 8) + (_inputBuffer[_inputPtr++] & 0xFF);
  }

  private final int _decode32Bits() throws IOException {
    int ptr = _inputPtr;
    if ((ptr + 3) >= _inputEnd) {
      return _slow32();
    }
    final byte[] b = _inputBuffer;
    int v = (b[ptr++] << 24) + ((b[ptr++] & 0xFF) << 16) + ((b[ptr++] & 0xFF) << 8) + (b[ptr++] & 0xFF);
    _inputPtr = ptr;
    return v;
  }

  private final int _slow32() throws IOException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    int v = _inputBuffer[_inputPtr++];
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    v = (v << 8) + (_inputBuffer[_inputPtr++] & 0xFF);
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    v = (v << 8) + (_inputBuffer[_inputPtr++] & 0xFF);
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    return (v << 8) + (_inputBuffer[_inputPtr++] & 0xFF);
  }

  private final long _decode64Bits() throws IOException {
    int ptr = _inputPtr;
    if ((ptr + 7) >= _inputEnd) {
      return _slow64();
    }
    final byte[] b = _inputBuffer;
    int i1 = (b[ptr++] << 24) + ((b[ptr++] & 0xFF) << 16) + ((b[ptr++] & 0xFF) << 8) + (b[ptr++] & 0xFF);
    int i2 = (b[ptr++] << 24) + ((b[ptr++] & 0xFF) << 16) + ((b[ptr++] & 0xFF) << 8) + (b[ptr++] & 0xFF);
    _inputPtr = ptr;
    return _long(i1, i2);
  }

  private final long _slow64() throws IOException {
    return _long(_decode32Bits(), _decode32Bits());
  }

  private final static long _long(int i1, int i2) {
    long l1 = i1;
    long l2 = i2;
    l2 = (l2 << 32) >>> 32;
    return (l1 << 32) + l2;
  }

  private final int _decodeUTF8_3(int c1) throws IOException {
    c1 &= 0x0F;
    int d = _nextByte();
    if ((d & 0xC0) != 0x080) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    int c = (c1 << 6) | (d & 0x3F);
    d = _nextByte();
    if ((d & 0xC0) != 0x080) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    c = (c << 6) | (d & 0x3F);
    return c;
  }

  private final int _decodeChunkedUTF8_3(int c1) throws IOException {
    c1 &= 0x0F;
    int d = _nextChunkedByte();
    if ((d & 0xC0) != 0x080) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    int c = (c1 << 6) | (d & 0x3F);
    d = _nextChunkedByte();
    if ((d & 0xC0) != 0x080) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    c = (c << 6) | (d & 0x3F);
    return c;
  }

  /**
     * @return Character value <b>minus 0x10000</c>; this so that caller
     *    can readily expand it to actual surrogates
     */
  private final int _decodeUTF8_4(int c) throws IOException {
    int d = _nextByte();
    if ((d & 0xC0) != 0x080) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    c = ((c & 0x07) << 6) | (d & 0x3F);
    d = _nextByte();
    if ((d & 0xC0) != 0x080) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    c = (c << 6) | (d & 0x3F);
    d = _nextByte();
    if ((d & 0xC0) != 0x080) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    return ((c << 6) | (d & 0x3F)) - 0x10000;
  }

  private final int _decodeChunkedUTF8_4(int c) throws IOException {
    int d = _nextChunkedByte();
    if ((d & 0xC0) != 0x080) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    c = ((c & 0x07) << 6) | (d & 0x3F);
    d = _nextChunkedByte();
    if ((d & 0xC0) != 0x080) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    c = (c << 6) | (d & 0x3F);
    d = _nextChunkedByte();
    if ((d & 0xC0) != 0x080) {
      _reportInvalidOther(d & 0xFF, _inputPtr);
    }
    return ((c << 6) | (d & 0x3F)) - 0x10000;
  }

  protected final boolean loadMore() throws IOException {
    if (_inputStream != null) {
      _currInputProcessed += _inputEnd;
      int count = _inputStream.read(_inputBuffer, 0, _inputBuffer.length);
      if (count > 0) {
        _inputPtr = 0;
        _inputEnd = count;
        return true;
      }
      _closeInput();
      if (count == 0) {
        throw new IOException("InputStream.read() returned 0 characters when trying to read " + _inputBuffer.length + " bytes");
      }
    }
    return false;
  }

  protected final void loadMoreGuaranteed() throws IOException {
    if (!loadMore()) {
      _reportInvalidEOF();
    }
  }

  /**
     * Helper method that will try to load at least specified number bytes in
     * input buffer, possible moving existing data around if necessary
     */
  protected final void _loadToHaveAtLeast(int minAvailable) throws IOException {
    if (_inputStream == null) {
      throw _constructError("Needed to read " + minAvailable + " bytes, reached end-of-input");
    }
    int amount = _inputEnd - _inputPtr;
    if (amount > 0 && _inputPtr > 0) {
      _currInputProcessed += _inputPtr;
      System.arraycopy(_inputBuffer, _inputPtr, _inputBuffer, 0, amount);
      _inputEnd = amount;
    } else {
      _inputEnd = 0;
    }
    _inputPtr = 0;
    while (_inputEnd < minAvailable) {
      int count = _inputStream.read(_inputBuffer, _inputEnd, _inputBuffer.length - _inputEnd);
      if (count < 1) {
        _closeInput();
        if (count == 0) {
          throw new IOException("InputStream.read() returned 0 characters when trying to read " + amount + " bytes");
        }
        throw _constructError("Needed to read " + minAvailable + " bytes, missed " + minAvailable + " before end-of-input");
      }
      _inputEnd += count;
    }
  }

  protected ByteArrayBuilder _getByteArrayBuilder() {
    if (_byteArrayBuilder == null) {
      _byteArrayBuilder = new ByteArrayBuilder();
    } else {
      _byteArrayBuilder.reset();
    }
    return _byteArrayBuilder;
  }

  protected void _closeInput() throws IOException {
    if (_inputStream != null) {
      if (_ioContext.isResourceManaged() || isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
        _inputStream.close();
      }
      _inputStream = null;
    }
  }

  @Override protected void _handleEOF() throws JsonParseException {
    if (!_parsingContext.inRoot()) {
      _reportInvalidEOF(": expected close marker for " + _parsingContext.getTypeDesc() + " (from " + _parsingContext.getStartLocation(_ioContext.getSourceReference()) + ")");
    }
  }

  protected void _reportUnexpectedBreak() throws IOException {
    if (_parsingContext.inRoot()) {
      throw _constructError("Unexpected Break (0xFF) token in Root context");
    }
    throw _constructError("Unexpected Break (0xFF) token in definite length (" + _parsingContext.getExpectedLength() + ") " + (_parsingContext.inObject() ? "Object" : "Array"));
  }

  protected void _reportInvalidChar(int c) throws JsonParseException {
    if (c < ' ') {
      _throwInvalidSpace(c);
    }
    _reportInvalidInitial(c);
  }

  protected void _reportInvalidInitial(int mask) throws JsonParseException {
    _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
  }

  protected void _reportInvalidOther(int mask) throws JsonParseException {
    _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
  }

  protected void _reportInvalidOther(int mask, int ptr) throws JsonParseException {
    _inputPtr = ptr;
    _reportInvalidOther(mask);
  }
}