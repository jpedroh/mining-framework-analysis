package com.fasterxml.jackson.dataformat.xml.deser;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.dataformat.xml.PackageVersion;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * {@link JsonParser} implementation that exposes XML structure as
 * set of JSON events that can be used for data binding.
 */
public class FromXmlParser extends ParserMinimalBase {
  /**
     * The default name placeholder for XML text segments is empty
     * String ("").
     */
  public final static String DEFAULT_UNNAMED_TEXT_PROPERTY = "";

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
      return (flags & getMask()) != 0;
    }
  }

  /**
     * In cases where a start element has both attributes and non-empty textual
     * value, we have to create a bogus property; we will use this as
     * the property name.
     *<p>
     * Name used for pseudo-property used for returning XML Text value (which does
     * not have actual element name to use). Defaults to empty String, but
     * may be changed for interoperability reasons: JAXB, for example, uses
     * "value" as name.
     * 
     * @since 2.1
     */
  protected String _cfgNameForTextElement = DEFAULT_UNNAMED_TEXT_PROPERTY;

  /**
     * Bit flag composed of bits that indicate which
     * {@link FromXmlParser.Feature}s
     * are enabled.
     */
  protected int _formatFeatures;

  protected ObjectCodec _objectCodec;

  /**
     * Flag that indicates whether parser is closed or not. Gets
     * set when parser is either closed by explicit call
     * ({@link #close}) or when end-of-input is reached.
     */
  protected boolean _closed;

  final protected IOContext _ioContext;

  /**
     * Information about parser context, context in which
     * the next token is to be parsed (root, array, object).
     */
  protected XmlReadContext _parsingContext;

  protected final XmlTokenStream _xmlTokens;

  /**
     * 
     * We need special handling to keep track of whether a value
     * may be exposed as simple leaf value.
     */
  protected boolean _mayBeLeaf;

  protected JsonToken _nextToken;

  protected String _currText;

  protected Set<String> _namesToWrap;

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

  public FromXmlParser(IOContext ctxt, int genericParserFeatures, int xmlFeatures, ObjectCodec codec, XMLStreamReader xmlReader) {
    super(genericParserFeatures);
    _formatFeatures = xmlFeatures;
    _ioContext = ctxt;
    _objectCodec = codec;
    _parsingContext = XmlReadContext.createRootContext(-1, -1);
    _nextToken = JsonToken.START_OBJECT;
    _xmlTokens = new XmlTokenStream(xmlReader, ctxt.getSourceReference());
  }

  @Override public Version version() {
    return PackageVersion.VERSION;
  }

  @Override public ObjectCodec getCodec() {
    return _objectCodec;
  }

  @Override public void setCodec(ObjectCodec c) {
    _objectCodec = c;
  }

  /**
     * @since 2.1
     */
  public void setXMLTextElementName(String name) {
    _cfgNameForTextElement = name;
  }

  /**
     * XML format does require support from custom {@link ObjectCodec}
     * (that is, {@link XmlMapper}), so need to return true here.
     * 
     * @return True since XML format does require support from codec
     */
  @Override public boolean requiresCustomCodec() {
    return true;
  }

  public FromXmlParser enable(Feature f) {
    _formatFeatures |= f.getMask();
    return this;
  }

  public FromXmlParser disable(Feature f) {
    _formatFeatures &= ~f.getMask();
    return this;
  }

  public final boolean isEnabled(Feature f) {
    return (_formatFeatures & f.getMask()) != 0;
  }

  public FromXmlParser configure(Feature f, boolean state) {
    if (state) {
      enable(f);
    } else {
      disable(f);
    }
    return this;
  }

  @Override public int getFormatFeatures() {
    return _formatFeatures;
  }

  @Override public JsonParser overrideFormatFeatures(int values, int mask) {
    _formatFeatures = (_formatFeatures & ~mask) | (values & mask);
    return this;
  }

  /**
     * Method that allows application direct access to underlying
     * Stax {@link XMLStreamWriter}. Note that use of writer is
     * discouraged, and may interfere with processing of this writer;
     * however, occasionally it may be necessary.
     *<p>
     * Note: writer instance will always be of type
     * {@link org.codehaus.stax2.XMLStreamWriter2} (including
     * Typed Access API) so upcasts are safe.
     */
  public XMLStreamReader getStaxReader() {
    return _xmlTokens.getXmlReader();
  }

  /**
     * Method that may be called to indicate that specified names
     * (only local parts retained currently: this may be changed in
     * future) should be considered "auto-wrapping", meaning that
     * they will be doubled to contain two opening elements, two
     * matching closing elements. This is needed for supporting
     * handling of so-called "unwrapped" array types, something
     * XML mappings like JAXB often use.
     *<p>
     * NOTE: this method is considered part of internal implementation
     * interface, and it is <b>NOT</b> guaranteed to remain unchanged
     * between minor versions (it is however expected not to change in
     * patch versions). So if you have to use it, be prepared for
     * possible additional work.
     * 
     * @since 2.1
     */
  public void addVirtualWrapping(Set<String> namesToWrap) {
    String name = _xmlTokens.getLocalName();
    if (name != null && namesToWrap.contains(name)) {
      _xmlTokens.repeatStartElement();
    }
    _namesToWrap = namesToWrap;
    _parsingContext.setNamesToWrap(namesToWrap);
  }

  /**
     * Method that can be called to get the name associated with
     * the current event.
     */
  @Override public String getCurrentName() throws IOException {
    String name;
    if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
      XmlReadContext parent = _parsingContext.getParent();
      name = parent.getCurrentName();
    } else {
      name = _parsingContext.getCurrentName();
    }
    if (name == null) {
      throw new IllegalStateException("Missing name, in state: " + _currToken);
    }
    return name;
  }

  @Override public void overrideCurrentName(String name) {
    XmlReadContext ctxt = _parsingContext;
    if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
      ctxt = ctxt.getParent();
    }
    ctxt.setCurrentName(name);
  }

  @Override public void close() throws IOException {
    if (!_closed) {
      _closed = true;
      try {
        if (_ioContext.isResourceManaged() || isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
          _xmlTokens.closeCompletely();
        } else {
          _xmlTokens.close();
        }
      }  finally {
        _releaseBuffers();
      }
    }
  }

  @Override public boolean isClosed() {
    return _closed;
  }

  @Override public XmlReadContext getParsingContext() {
    return _parsingContext;
  }

  /**
     * Method that return the <b>starting</b> location of the current
     * token; that is, position of the first character from input
     * that starts the current token.
     */
  @Override public JsonLocation getTokenLocation() {
    return _xmlTokens.getTokenLocation();
  }

  /**
     * Method that returns location of the last processed character;
     * usually for error reporting purposes
     */
  @Override public JsonLocation getCurrentLocation() {
    return _xmlTokens.getCurrentLocation();
  }

  /**
     * Since xml representation can not really distinguish between array
     * and object starts (both are represented with elements), this method
     * is overridden and taken to mean that expecation is that the current
     * start element is to mean 'start array', instead of default of
     * 'start object'.
     */
  @Override public boolean isExpectedStartArrayToken() {
    JsonToken t = _currToken;
    if (t == JsonToken.START_OBJECT) {
      _currToken = JsonToken.START_ARRAY;
      _parsingContext.convertToArray();
      if (_nextToken == JsonToken.END_OBJECT) {
        _nextToken = JsonToken.END_ARRAY;
      } else {
        _nextToken = null;
      }
      _xmlTokens.skipAttributes();
      return true;
    }
    return (t == JsonToken.START_ARRAY);
  }

  @Override public JsonToken nextToken() throws IOException {
    _binaryValue = null;
    if (_nextToken != null) {
      JsonToken t = _nextToken;
      _currToken = t;
      _nextToken = null;
      switch (t) {
        case START_OBJECT:
        _parsingContext = _parsingContext.createChildObjectContext(-1, -1);
        break;
        case START_ARRAY:
        _parsingContext = _parsingContext.createChildArrayContext(-1, -1);
        break;
        case END_OBJECT:
        case END_ARRAY:
        _parsingContext = _parsingContext.getParent();
        _namesToWrap = _parsingContext.getNamesToWrap();
        break;
        case FIELD_NAME:
        _parsingContext.setCurrentName(_xmlTokens.getLocalName());
        break;
        default:
      }
      return t;
    }
    int token = _xmlTokens.next();
    while (token == XmlTokenStream.XML_START_ELEMENT) {
      if (_mayBeLeaf) {
        _nextToken = JsonToken.FIELD_NAME;
        _parsingContext = _parsingContext.createChildObjectContext(-1, -1);
        return (_currToken = JsonToken.START_OBJECT);
      }
      if (_parsingContext.inArray()) {
        token = _xmlTokens.next();
        _mayBeLeaf = true;
        continue;
      }
      String name = _xmlTokens.getLocalName();
      _parsingContext.setCurrentName(name);
      if (_namesToWrap != null && _namesToWrap.contains(name)) {
        _xmlTokens.repeatStartElement();
      }
      _mayBeLeaf = true;
      return (_currToken = JsonToken.FIELD_NAME);
    }
    while (true) {
      switch (token) {
        case XmlTokenStream.XML_END_ELEMENT:
        if (_mayBeLeaf) {
          _mayBeLeaf = false;
          if (_parsingContext.inArray()) {
            _nextToken = JsonToken.END_OBJECT;
            _parsingContext = _parsingContext.createChildObjectContext(-1, -1);
            return (_currToken = JsonToken.START_OBJECT);
          }
          return (_currToken = JsonToken.VALUE_NULL);
        }
        _currToken = _parsingContext.inArray() ? JsonToken.END_ARRAY : JsonToken.END_OBJECT;
        _parsingContext = _parsingContext.getParent();
        _namesToWrap = _parsingContext.getNamesToWrap();
        return _currToken;
        case XmlTokenStream.XML_ATTRIBUTE_NAME:
        if (_mayBeLeaf) {
          _mayBeLeaf = false;
          _nextToken = JsonToken.FIELD_NAME;
          _currText = _xmlTokens.getText();
          _parsingContext = _parsingContext.createChildObjectContext(-1, -1);
          return (_currToken = JsonToken.START_OBJECT);
        }
        _parsingContext.setCurrentName(_xmlTokens.getLocalName());
        return (_currToken = JsonToken.FIELD_NAME);
        case XmlTokenStream.XML_ATTRIBUTE_VALUE:
        _currText = _xmlTokens.getText();
        return (_currToken = JsonToken.VALUE_STRING);
        case XmlTokenStream.XML_TEXT:
        _currText = _xmlTokens.getText();
        if (_mayBeLeaf) {
          _mayBeLeaf = false;
          _xmlTokens.skipEndElement();
          if (_parsingContext.inArray()) {
            if (_isEmpty(_currText)) {
              _nextToken = JsonToken.END_OBJECT;
              _parsingContext = _parsingContext.createChildObjectContext(-1, -1);
              return (_currToken = JsonToken.START_OBJECT);
            }
          }
          return (_currToken = JsonToken.VALUE_STRING);
        } else {
          if (_parsingContext.inObject()) {
            if ((_currToken != JsonToken.FIELD_NAME) && _isEmpty(_currText)) {
              token = _xmlTokens.next();
              continue;
            }
          }
        }
        _parsingContext.setCurrentName(_cfgNameForTextElement);
        _nextToken = JsonToken.VALUE_STRING;
        return (_currToken = JsonToken.FIELD_NAME);
        case XmlTokenStream.XML_END:
        return (_currToken = null);
      }
    }
  }

  /**
     * Method overridden to support more reliable deserialization of
     * String collections.
     */
  @Override public String nextTextValue() throws IOException {
    _binaryValue = null;
    if (_nextToken != null) {
      JsonToken t = _nextToken;
      _currToken = t;
      _nextToken = null;
      if (t == JsonToken.VALUE_STRING) {
        return _currText;
      }
      _updateState(t);
      return null;
    }
    int token = _xmlTokens.next();
    while (token == XmlTokenStream.XML_START_ELEMENT) {
      if (_mayBeLeaf) {
        _nextToken = JsonToken.FIELD_NAME;
        _parsingContext = _parsingContext.createChildObjectContext(-1, -1);
        _currToken = JsonToken.START_OBJECT;
        return null;
      }
      if (_parsingContext.inArray()) {
        token = _xmlTokens.next();
        _mayBeLeaf = true;
        continue;
      }
      String name = _xmlTokens.getLocalName();
      _parsingContext.setCurrentName(name);
      if (_namesToWrap != null && _namesToWrap.contains(name)) {
        _xmlTokens.repeatStartElement();
      }
      _mayBeLeaf = true;
      _currToken = JsonToken.FIELD_NAME;
      return null;
    }
    switch (token) {
      case XmlTokenStream.XML_END_ELEMENT:
      if (_mayBeLeaf) {
        _mayBeLeaf = false;
        _currToken = JsonToken.VALUE_STRING;
        return (_currText = "");
      }
      _currToken = _parsingContext.inArray() ? JsonToken.END_ARRAY : JsonToken.END_OBJECT;
      _parsingContext = _parsingContext.getParent();
      _namesToWrap = _parsingContext.getNamesToWrap();
      break;
      case XmlTokenStream.XML_ATTRIBUTE_NAME:
      if (_mayBeLeaf) {
        _mayBeLeaf = false;
        _nextToken = JsonToken.FIELD_NAME;
        _currText = _xmlTokens.getText();
        _parsingContext = _parsingContext.createChildObjectContext(-1, -1);
        _currToken = JsonToken.START_OBJECT;
      } else {
        _parsingContext.setCurrentName(_xmlTokens.getLocalName());
        _currToken = JsonToken.FIELD_NAME;
      }
      break;
      case XmlTokenStream.XML_ATTRIBUTE_VALUE:
      _currToken = JsonToken.VALUE_STRING;
      return (_currText = _xmlTokens.getText());
      case XmlTokenStream.XML_TEXT:
      _currText = _xmlTokens.getText();
      if (_mayBeLeaf) {
        _mayBeLeaf = false;
        _xmlTokens.skipEndElement();
        _currToken = JsonToken.VALUE_STRING;
        return _currText;
      }
      _parsingContext.setCurrentName(_cfgNameForTextElement);
      _nextToken = JsonToken.VALUE_STRING;
      _currToken = JsonToken.FIELD_NAME;
      break;
      case XmlTokenStream.XML_END:
      _currToken = null;
    }
    return null;
  }

  private void _updateState(JsonToken t) {
    switch (t) {
      case START_OBJECT:
      _parsingContext = _parsingContext.createChildObjectContext(-1, -1);
      break;
      case START_ARRAY:
      _parsingContext = _parsingContext.createChildArrayContext(-1, -1);
      break;
      case END_OBJECT:
      case END_ARRAY:
      _parsingContext = _parsingContext.getParent();
      _namesToWrap = _parsingContext.getNamesToWrap();
      break;
      case FIELD_NAME:
      _parsingContext.setCurrentName(_xmlTokens.getLocalName());
      break;
      default:
    }
  }

  @Override public String getText() throws IOException {
    if (_currToken == null) {
      return null;
    }
    switch (_currToken) {
      case FIELD_NAME:
      return getCurrentName();
      case VALUE_STRING:
      return _currText;
      default:
      return _currToken.asString();
    }
  }

  @Override public final String getValueAsString() throws IOException {
    return getValueAsString(null);
  }

  @Override public String getValueAsString(String defValue) throws IOException {
    JsonToken t = _currToken;
    if (t == null) {
      return null;
    }
    switch (t) {
      case FIELD_NAME:
      return getCurrentName();
      case VALUE_STRING:
      return _currText;
      case START_OBJECT:
      {
        String str = _xmlTokens.convertToString();
        if (str != null) {
          _parsingContext = _parsingContext.getParent();
          _namesToWrap = _parsingContext.getNamesToWrap();
          _currToken = JsonToken.VALUE_STRING;
          _nextToken = null;
          _xmlTokens.skipEndElement();
          return (_currText = str);
        }
      }
      return null;
      default:
      if (_currToken.isScalarValue()) {
        return _currToken.asString();
      }
    }
    return defValue;
  }

  @Override public char[] getTextCharacters() throws IOException {
    String text = getText();
    return (text == null) ? null : text.toCharArray();
  }

  @Override public int getTextLength() throws IOException {
    String text = getText();
    return (text == null) ? 0 : text.length();
  }

  @Override public int getTextOffset() throws IOException {
    return 0;
  }

  /**
     * XML input actually would offer access to character arrays; but since
     * we must coalesce things it cannot really be exposed.
     */
  @Override public boolean hasTextCharacters() {
    return false;
  }

  @Override public int getText(Writer writer) throws IOException {
    String str = getText();
    if (str == null) {
      return 0;
    }
    writer.write(str);
    return str.length();
  }

  @Override public Object getEmbeddedObject() throws IOException {
    return null;
  }

  @Override public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
    if (_currToken != JsonToken.VALUE_STRING && (_currToken != JsonToken.VALUE_EMBEDDED_OBJECT || _binaryValue == null)) {
      _reportError("Current token (" + _currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
    }
    if (_binaryValue == null) {
      try {
        _binaryValue = _decodeBase64(b64variant);
      } catch (IllegalArgumentException iae) {
        throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
      }
    }
    return _binaryValue;
  }

  @SuppressWarnings(value = { "resource" }) protected byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
    ByteArrayBuilder builder = _getByteArrayBuilder();
    final String str = getText();
    _decodeBase64(str, builder, b64variant);
    return builder.toByteArray();
  }

  @Override public BigInteger getBigIntegerValue() throws IOException {
    return null;
  }

  @Override public BigDecimal getDecimalValue() throws IOException {
    return null;
  }

  @Override public double getDoubleValue() throws IOException {
    return 0;
  }

  @Override public float getFloatValue() throws IOException {
    return 0;
  }

  @Override public int getIntValue() throws IOException {
    return 0;
  }

  @Override public long getLongValue() throws IOException {
    return 0;
  }

  @Override public NumberType getNumberType() throws IOException {
    return null;
  }

  @Override public Number getNumberValue() throws IOException {
    return null;
  }

  /**
     * Method called when an EOF is encountered between tokens.
     * If so, it may be a legitimate EOF, but only iff there
     * is no open non-root context.
     */
  @Override protected void _handleEOF() throws JsonParseException {
    if (!_parsingContext.inRoot()) {
      String marker = _parsingContext.inArray() ? "Array" : "Object";
      _reportInvalidEOF(String.format(": expected close marker for %s (start marker at %s)", marker, _parsingContext.getStartLocation(_ioContext.getSourceReference())), null);
    }
  }

  /**
     * Method called to release internal buffers owned by the base
     * parser.
     */
  protected void _releaseBuffers() throws IOException {
  }

  protected ByteArrayBuilder _getByteArrayBuilder() {
    if (_byteArrayBuilder == null) {
      _byteArrayBuilder = new ByteArrayBuilder();
    } else {
      _byteArrayBuilder.reset();
    }
    return _byteArrayBuilder;
  }

  protected boolean _isEmpty(String str) {
    int len = (str == null) ? 0 : str.length();
    if (len > 0) {
      for (int i = 0; i < len; ++i) {
        if (str.charAt(i) > ' ') {
          return false;
        }
      }
    }
    return true;
  }
}