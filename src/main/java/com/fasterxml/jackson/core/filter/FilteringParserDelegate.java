package com.fasterxml.jackson.core.filter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.core.util.JsonParserDelegate;

public class FilteringParserDelegate extends JsonParserDelegate {
  protected TokenFilter rootFilter;

  protected boolean _allowMultipleMatches;

  protected boolean _includePath;

  protected JsonToken _currToken;

  protected JsonToken _lastClearedToken;

  protected TokenFilterContext _headContext;

  protected TokenFilterContext _exposedContext;

  protected TokenFilter _itemFilter;

  protected int _matchCount;

  public FilteringParserDelegate(JsonParser p, TokenFilter f, boolean includePath, boolean allowMultipleMatches) {
    super(p);
    rootFilter = f;
    _itemFilter = f;
    _headContext = TokenFilterContext.createRootContext(f);
    _includePath = includePath;
    _allowMultipleMatches = allowMultipleMatches;
  }

  public TokenFilter getFilter() {
    return rootFilter;
  }

  public int getMatchCount() {
    return _matchCount;
  }

  @Override public JsonToken getCurrentToken() {
    return _currToken;
  }

  @Override public final int getCurrentTokenId() {
    final JsonToken t = _currToken;
    return (t == null) ? JsonTokenId.ID_NO_TOKEN : t.id();
  }

  @Override public boolean hasCurrentToken() {
    return _currToken != null;
  }

  @Override public boolean hasTokenId(int id) {
    final JsonToken t = _currToken;
    if (t == null) {
      return (JsonTokenId.ID_NO_TOKEN == id);
    }
    return t.id() == id;
  }

  @Override public final boolean hasToken(JsonToken t) {
    return (_currToken == t);
  }

  @Override public boolean isExpectedStartArrayToken() {
    return _currToken == JsonToken.START_ARRAY;
  }

  @Override public boolean isExpectedStartObjectToken() {
    return _currToken == JsonToken.START_OBJECT;
  }

  @Override public JsonLocation getCurrentLocation() {
    return delegate.getCurrentLocation();
  }

  @Override public JsonStreamContext getParsingContext() {
    return _filterContext();
  }

  @Override public String getCurrentName() throws IOException {
    JsonStreamContext ctxt = _filterContext();
    if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
      JsonStreamContext parent = ctxt.getParent();
      return (parent == null) ? null : parent.getCurrentName();
    }
    return ctxt.getCurrentName();
  }

  @Override public void clearCurrentToken() {
    if (_currToken != null) {
      _lastClearedToken = _currToken;
      _currToken = null;
    }
  }

  @Override public JsonToken getLastClearedToken() {
    return _lastClearedToken;
  }

  @Override public void overrideCurrentName(String name) {
    throw new UnsupportedOperationException("Can not currently override name during filtering read");
  }

  @Override public JsonToken nextToken() throws IOException {
    return delegate.nextToken();
  }

  @Override public JsonToken nextValue() throws IOException {
    JsonToken t = nextToken();
    if (t == JsonToken.FIELD_NAME) {
      t = nextToken();
    }
    return t;
  }

  @Override public JsonParser skipChildren() throws IOException {
    if ((_currToken != JsonToken.START_OBJECT) && (_currToken != JsonToken.START_ARRAY)) {
      return this;
    }
    int open = 1;
    while (true) {
      JsonToken t = nextToken();
      if (t == null) {
        return this;
      }
      if (t.isStructStart()) {
        ++open;
      } else {
        if (t.isStructEnd()) {
          if (--open == 0) {
            return this;
          }
        }
      }
    }
  }

  @Override public String getText() throws IOException {
    return delegate.getText();
  }

  @Override public boolean hasTextCharacters() {
    return delegate.hasTextCharacters();
  }

  @Override public char[] getTextCharacters() throws IOException {
    return delegate.getTextCharacters();
  }

  @Override public int getTextLength() throws IOException {
    return delegate.getTextLength();
  }

  @Override public int getTextOffset() throws IOException {
    return delegate.getTextOffset();
  }

  @Override public BigInteger getBigIntegerValue() throws IOException {
    return delegate.getBigIntegerValue();
  }

  @Override public boolean getBooleanValue() throws IOException {
    return delegate.getBooleanValue();
  }

  @Override public byte getByteValue() throws IOException {
    return delegate.getByteValue();
  }

  @Override public short getShortValue() throws IOException {
    return delegate.getShortValue();
  }

  @Override public BigDecimal getDecimalValue() throws IOException {
    return delegate.getDecimalValue();
  }

  @Override public double getDoubleValue() throws IOException {
    return delegate.getDoubleValue();
  }

  @Override public float getFloatValue() throws IOException {
    return delegate.getFloatValue();
  }

  @Override public int getIntValue() throws IOException {
    return delegate.getIntValue();
  }

  @Override public long getLongValue() throws IOException {
    return delegate.getLongValue();
  }

  @Override public NumberType getNumberType() throws IOException {
    return delegate.getNumberType();
  }

  @Override public Number getNumberValue() throws IOException {
    return delegate.getNumberValue();
  }

  @Override public int getValueAsInt() throws IOException {
    return delegate.getValueAsInt();
  }

  @Override public int getValueAsInt(int defaultValue) throws IOException {
    return delegate.getValueAsInt(defaultValue);
  }

  @Override public long getValueAsLong() throws IOException {
    return delegate.getValueAsLong();
  }

  @Override public long getValueAsLong(long defaultValue) throws IOException {
    return delegate.getValueAsLong(defaultValue);
  }

  @Override public double getValueAsDouble() throws IOException {
    return delegate.getValueAsDouble();
  }

  @Override public double getValueAsDouble(double defaultValue) throws IOException {
    return delegate.getValueAsDouble(defaultValue);
  }

  @Override public boolean getValueAsBoolean() throws IOException {
    return delegate.getValueAsBoolean();
  }

  @Override public boolean getValueAsBoolean(boolean defaultValue) throws IOException {
    return delegate.getValueAsBoolean(defaultValue);
  }

  @Override public String getValueAsString() throws IOException {
    return delegate.getValueAsString();
  }

  @Override public String getValueAsString(String defaultValue) throws IOException {
    return delegate.getValueAsString(defaultValue);
  }

  @Override public Object getEmbeddedObject() throws IOException {
    return delegate.getEmbeddedObject();
  }

  @Override public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
    return delegate.getBinaryValue(b64variant);
  }

  @Override public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
    return delegate.readBinaryValue(b64variant, out);
  }

  @Override public JsonLocation getTokenLocation() {
    return delegate.getTokenLocation();
  }

  protected JsonStreamContext _filterContext() {
    if (_exposedContext != null) {
      return _exposedContext;
    }
    return _headContext;
  }

  protected TokenFilterContext _replayContext;
}