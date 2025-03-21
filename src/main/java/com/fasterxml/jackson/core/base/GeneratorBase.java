package com.fasterxml.jackson.core.base;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.core.json.PackageVersion;
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

  /**
     * Set of feature masks related to features that need updates of other
     * local configuration or state.
     */
  protected final static int DERIVED_FEATURES_MASK = StreamWriteFeature.WRITE_NUMBERS_AS_STRINGS.getMask() | StreamWriteFeature.STRICT_DUPLICATE_DETECTION.getMask();

  protected final static String WRITE_BINARY = "write a binary value";

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
     * Context object used both to pass some initial settings and to allow
     * triggering of Object serialization through generator.
     *
     * @since 3.0
     */
  protected final ObjectWriteContext _objectWriteContext;

  /**
     * Bit flag composed of bits that indicate which
     * {@link com.fasterxml.jackson.core.StreamWriteFeature}s
     * are enabled.
     */
  protected int _streamWriteFeatures;

  /**
     * Flag set to indicate that implicit conversion from number
     * to JSON String is needed (as per
     * {@link com.fasterxml.jackson.core.StreamWriteFeature#WRITE_NUMBERS_AS_STRINGS}).
     */
  protected boolean _cfgNumbersAsStrings;

  /**
     * Object that keeps track of the current contextual state
     * of the generator.
     */
  protected JsonWriteContext _outputContext;

  /**
     * Flag that indicates whether generator is closed or not. Gets
     * set when it is closed by an explicit call
     * ({@link #close}).
     */
  protected boolean _closed;

  protected GeneratorBase(ObjectWriteContext writeCtxt, int features) {
    super();
    _objectWriteContext = writeCtxt;
    _streamWriteFeatures = features;
    DupDetector dups = StreamWriteFeature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector(this) : null;
    _outputContext = JsonWriteContext.createRootContext(dups);
    _cfgNumbersAsStrings = StreamWriteFeature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
  }

  protected GeneratorBase(ObjectWriteContext writeCtxt, int features, JsonWriteContext ctxt) {
    super();
    _objectWriteContext = writeCtxt;
    _streamWriteFeatures = features;
    _outputContext = ctxt;
    _cfgNumbersAsStrings = StreamWriteFeature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
  }

  /**
     * Implemented with standard version number detection algorithm, typically using
     * a simple generated class, with information extracted from Maven project file
     * during build.
     */
  @Override public Version version() {
    return PackageVersion.VERSION;
  }

  @Override public Object getCurrentValue() {
    return _outputContext.getCurrentValue();
  }

  @Override public void setCurrentValue(Object v) {
    if (_outputContext != null) {
      _outputContext.setCurrentValue(v);
    }
  }

  @Override public final boolean isEnabled(StreamWriteFeature f) {
    return (_streamWriteFeatures & f.getMask()) != 0;
  }

  @Override public int streamWriteFeatures() {
    return _streamWriteFeatures;
  }

  @Override public int formatWriteFeatures() {
    return 0;
  }

  @Override public JsonGenerator enable(StreamWriteFeature f) {
    final int mask = f.getMask();
    _streamWriteFeatures |= mask;
    if ((mask & DERIVED_FEATURES_MASK) != 0) {
      if (f == StreamWriteFeature.WRITE_NUMBERS_AS_STRINGS) {
        _cfgNumbersAsStrings = true;
      }
    }
    return this;
  }

  @Override public JsonGenerator disable(StreamWriteFeature f) {
    final int mask = f.getMask();
    _streamWriteFeatures &= ~mask;
    if ((mask & DERIVED_FEATURES_MASK) != 0) {
      if (f == StreamWriteFeature.WRITE_NUMBERS_AS_STRINGS) {
        _cfgNumbersAsStrings = false;
      }
    }
    return this;
  }

  @Override public TokenStreamContext getOutputContext() {
    return _outputContext;
  }

  @Override public ObjectWriteContext getObjectWriteContext() {
    return _objectWriteContext;
  }

  @Override public void writeStartArray(Object forValue, int size) throws IOException {
    writeStartArray(size);
    if ((_outputContext != null) && (forValue != null)) {
      _outputContext.setCurrentValue(forValue);
    }
  }

  @Override public void writeStartObject(Object forValue) throws IOException {
    writeStartObject();
    if (
<<<<<<< /usr/src/app/output/fasterxml/jackson-core/705123afb6b18502839b1e93b4d942f876b1a410/src/main/java/com/fasterxml/jackson/core/base/GeneratorBase.java/left.java
    (_outputContext != null) && (forValue != null)
=======
    forValue != null
>>>>>>> /usr/src/app/output/fasterxml/jackson-core/705123afb6b18502839b1e93b4d942f876b1a410/src/main/java/com/fasterxml/jackson/core/base/GeneratorBase.java/right.java
    ) {

<<<<<<< /usr/src/app/output/fasterxml/jackson-core/705123afb6b18502839b1e93b4d942f876b1a410/src/main/java/com/fasterxml/jackson/core/base/GeneratorBase.java/left.java
      _outputContext.setCurrentValue(forValue)
=======
      setCurrentValue(forValue)
>>>>>>> /usr/src/app/output/fasterxml/jackson-core/705123afb6b18502839b1e93b4d942f876b1a410/src/main/java/com/fasterxml/jackson/core/base/GeneratorBase.java/right.java
      ;
    }
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