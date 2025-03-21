package com.fasterxml.jackson.core.base;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * This base class implements part of API that a JSON generator exposes
 * to applications, adds shared internal methods that sub-classes
 * can use and adds some abstract methods sub-classes must implement.
 */
public abstract class GeneratorBase extends JsonGenerator {
  public final static int SURR1_FIRST = 0xD800;

  public final static int SURR1_LAST = 0xDBFF;

  public final static int SURR2_FIRST = 0xDC00;

  public final static int SURR2_LAST = 0xDFFF;

  protected final static String WRITE_BINARY = "write a binary value";

  /**
     * Set of feature masks related to features that need updates of other
     * local configuration or state.
     * 
     * @since 2.5
     */
  protected final ObjectWriteContext _objectWriteContext;

  protected final static String WRITE_BOOLEAN = "write a boolean value";

  protected final static String WRITE_NULL = "write a null";

  protected final static String WRITE_NUMBER = "write a number";

  protected final static String WRITE_RAW = "write a raw (unencoded) value";

  protected final static String WRITE_STRING = "write a string";

  /**
     * This value is the limit of scale allowed for serializing {@link BigDecimal}
     * in "plain" (non-engineering) notation; intent is to prevent asymmetric
     * attack whereupon simple eng-notation with big scale is used to generate
     * huge "plain" serialization. See [core#315] for details.
     */
  protected final static int MAX_BIG_DECIMAL_SCALE = 9999;

  /**
     * Bit flag composed of bits that indicate which
     * {@link com.fasterxml.jackson.core.StreamWriteFeature}s
     * are enabled.
     */
  protected int _streamWriteFeatures;

  /**
     * Flag that indicates whether generator is closed or not. Gets
     * set when it is closed by an explicit call
     * ({@link #close}).
     */
  protected boolean _closed;

  @SuppressWarnings(value = { "deprecation" }) protected GeneratorBase(ObjectWriteContext writeCtxt, int streamWriteFeatures) {
    super();
    _objectWriteContext = writeCtxt;
    _streamWriteFeatures = streamWriteFeatures;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * @since 2.5
     */
  @SuppressWarnings(value = { "deprecation" }) protected GeneratorBase(int features, ObjectCodec codec, JsonWriteContext ctxt) {
    super();
    _features = features;
    _objectCodec = codec;
    _writeContext = ctxt;
    _cfgNumbersAsStrings = Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
  }
>>>>>>> /usr/src/app/output/fasterxml/jackson-core/1755de36114aad71607db1b42c58714631d470a6/src/main/java/com/fasterxml/jackson/core/base/GeneratorBase.java/right.java


  @Override public final boolean isEnabled(StreamWriteFeature f) {
    return (_streamWriteFeatures & f.getMask()) != 0;
  }

  @Override public int streamWriteFeatures() {
    return _streamWriteFeatures;
  }

  @Override public JsonGenerator enable(StreamWriteFeature f) {
    _streamWriteFeatures |= f.getMask();
    return this;
  }

  @Override public JsonGenerator disable(StreamWriteFeature f) {
    _streamWriteFeatures &= ~f.getMask();
    return this;
  }

  @Override public ObjectWriteContext getObjectWriteContext() {
    return _objectWriteContext;
  }

  @Override public void writeStartArray(Object forValue, int size) throws IOException {
    writeStartArray(forValue);
  }

  @Override public void writeStartObject(Object forValue, int size) throws IOException {
    writeStartObject(forValue);
  }

  @Override public void writeFieldName(SerializableString name) throws IOException {
    writeFieldName(name.getValue());
  }

  @Override public void writeString(SerializableString text) throws IOException {
    writeString(text.getValue());
  }

  @Override public void writeRawValue(String text) throws IOException {
    _verifyValueWrite("write raw value");
    writeRaw(text);
  }

  @Override public void writeRawValue(String text, int offset, int len) throws IOException {
    _verifyValueWrite("write raw value");
    writeRaw(text, offset, len);
  }

  @Override public void writeRawValue(char[] text, int offset, int len) throws IOException {
    _verifyValueWrite("write raw value");
    writeRaw(text, offset, len);
  }

  @Override public void writeRawValue(SerializableString text) throws IOException {
    _verifyValueWrite("write raw value");
    writeRaw(text);
  }

  @Override public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
    _reportUnsupportedOperation();
    return 0;
  }

  @Override public void writeObject(Object value) throws IOException {
    if (value == null) {
      writeNull();
    } else {
      _objectWriteContext.writeValue(this, value);
    }
  }

  @Override public void writeTree(TreeNode rootNode) throws IOException {
    if (rootNode == null) {
      writeNull();
    } else {
      _objectWriteContext.writeTree(this, rootNode);
    }
  }

  @Override public abstract void flush() throws IOException;

  @Override public void close() throws IOException {
    _closed = true;
  }

  @Override public boolean isClosed() {
    return _closed;
  }

  /**
     * Method called to release any buffers generator may be holding,
     * once generator is being closed.
     */
  protected abstract void _releaseBuffers();

  /**
     * Method called before trying to write a value (scalar or structured),
     * to verify that this is legal in current output state, as well as to
     * output separators if and as necessary.
     * 
     * @param typeMsg Additional message used for generating exception message
     *   if value output is NOT legal in current generator output state.
     */
  protected abstract void _verifyValueWrite(String typeMsg) throws IOException;

  /**
     * Overridable factory method called to instantiate an appropriate {@link PrettyPrinter}
     * for case of "just use the default one", when {@link #useDefaultPrettyPrinter()} is called.
     */
  protected PrettyPrinter _constructDefaultPrettyPrinter() {
    return new DefaultPrettyPrinter();
  }

  /**
     * Helper method used to serialize a {@link java.math.BigDecimal} as a String,
     * for serialization, taking into account configuration settings
     */
  protected String _asString(BigDecimal value) throws IOException {
    if (StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(_streamWriteFeatures)) {
      int scale = value.scale();
      if ((scale < -MAX_BIG_DECIMAL_SCALE) || (scale > MAX_BIG_DECIMAL_SCALE)) {
        _reportError(String.format("Attempt to write plain `java.math.BigDecimal` (see JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) with illegal scale (%d): needs to be between [-%d, %d]", scale, MAX_BIG_DECIMAL_SCALE, MAX_BIG_DECIMAL_SCALE));
      }
      return value.toPlainString();
    }
    return value.toString();
  }

  protected final int _decodeSurrogate(int surr1, int surr2) throws IOException {
    if (surr2 < SURR2_FIRST || surr2 > SURR2_LAST) {
      String msg = "Incomplete surrogate pair: first char 0x" + Integer.toHexString(surr1) + ", second 0x" + Integer.toHexString(surr2);
      _reportError(msg);
    }
    int c = 0x10000 + ((surr1 - SURR1_FIRST) << 10) + (surr2 - SURR2_FIRST);
    return c;
  }
}