package com.fasterxml.jackson.core.filter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;

/**
 * Specialized {@link JsonGeneratorDelegate} that allows use of
 * {@link TokenFilter} for outputting a subset of content that
 * caller tries to generate.
 * 
 * @since 2.6
 */
public class FilteringGeneratorDelegate extends JsonGeneratorDelegate {
  /**
     * Object consulted to determine whether to write parts of content generator
     * is asked to write or not.
     */
  protected TokenFilter rootFilter;

  /**
     * Flag that determines whether filtering will continue after the first
     * match is indicated or not: if `false`, output is based on just the first
     * full match (returning {@link TokenFilter#INCLUDE_ALL}) and no more
     * checks are made; if `true` then filtering will be applied as necessary
     * until end of content.
     */
  protected boolean _allowMultipleMatches;

  /**
     * Flag that determines whether path leading up to included content should
     * also be automatically included or not. If `false`, no path inclusion is
     * done and only explicitly included entries are output; if `true` then
     * path from main level down to match is also included as necessary.
     */
  protected boolean _includePath;

  @Deprecated protected boolean _includeImmediateParent;

  /**
     * Although delegate has its own output context it is not sufficient since we actually
     * have to keep track of excluded (filtered out) structures as well as ones delegate
     * actually outputs.
     */
  protected TokenFilterContext _filterContext;

  /**
     * State that applies to the item within container, used where applicable.
     * Specifically used to pass inclusion state between property name and
     * property, and also used for array elements.
     */
  protected TokenFilter _itemFilter;

  /**
     * Number of tokens for which {@link TokenFilter#INCLUDE_ALL}
     * has been returned
     */
  protected int _matchCount;

  public FilteringGeneratorDelegate(JsonGenerator d, TokenFilter f, boolean includePath, boolean allowMultipleMatches) {
    super(d, false);
    rootFilter = f;
    _itemFilter = f;
    _filterContext = TokenFilterContext.createRootContext(f);
    _includePath = includePath;
    _allowMultipleMatches = allowMultipleMatches;
  }

  public TokenFilter getFilter() {
    return rootFilter;
  }

  public JsonStreamContext getFilterContext() {
    return _filterContext;
  }

  /**
     * Accessor for finding number of matches, where specific token and sub-tree
     * starting (if structured type) are passed.
     */
  public int getMatchCount() {
    return _matchCount;
  }

  @Override public JsonStreamContext getOutputContext() {
    return _filterContext;
  }

  @Override public void writeStartArray() throws IOException {
    if (_itemFilter == null) {
      _filterContext = _filterContext.createChildArrayContext(null, false);
      return;
    }
    if (_itemFilter == TokenFilter.INCLUDE_ALL) {
      _filterContext = _filterContext.createChildArrayContext(_itemFilter, true);
      delegate.writeStartArray();
      return;
    }
    _itemFilter = _filterContext.checkValue(_itemFilter);
    if (_itemFilter == null) {
      _filterContext = _filterContext.createChildArrayContext(null, false);
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      _itemFilter = _itemFilter.filterStartArray();
    }
    if (_itemFilter == TokenFilter.INCLUDE_ALL) {
      _checkParentPath();
      _filterContext = _filterContext.createChildArrayContext(_itemFilter, true);
      delegate.writeStartArray();
    } else {
      _filterContext = _filterContext.createChildArrayContext(_itemFilter, false);
    }
  }

  @Override public void writeStartArray(int size) throws IOException {
    if (_itemFilter == null) {
      _filterContext = _filterContext.createChildArrayContext(null, false);
      return;
    }
    if (_itemFilter == TokenFilter.INCLUDE_ALL) {
      _filterContext = _filterContext.createChildArrayContext(_itemFilter, true);
      delegate.writeStartArray(size);
      return;
    }
    _itemFilter = _filterContext.checkValue(_itemFilter);
    if (_itemFilter == null) {
      _filterContext = _filterContext.createChildArrayContext(null, false);
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      _itemFilter = _itemFilter.filterStartArray();
    }
    if (_itemFilter == TokenFilter.INCLUDE_ALL) {
      _checkParentPath();
      _filterContext = _filterContext.createChildArrayContext(_itemFilter, true);
      delegate.writeStartArray(size);
    } else {
      _filterContext = _filterContext.createChildArrayContext(_itemFilter, false);
    }
  }

  @Override public void writeEndArray() throws IOException {
    _filterContext = _filterContext.closeArray(delegate);
    if (_filterContext != null) {
      _itemFilter = _filterContext.getFilter();
    }
  }

  @Override public void writeStartObject() throws IOException {
    if (_itemFilter == null) {
      _filterContext = _filterContext.createChildObjectContext(_itemFilter, false);
      return;
    }
    if (_itemFilter == TokenFilter.INCLUDE_ALL) {
      _filterContext = _filterContext.createChildObjectContext(_itemFilter, true);
      delegate.writeStartObject();
      return;
    }
    TokenFilter f = _filterContext.checkValue(_itemFilter);
    if (f == null) {
      return;
    }
    if (f != TokenFilter.INCLUDE_ALL) {
      f = f.filterStartObject();
    }
    if (f == TokenFilter.INCLUDE_ALL) {
      _checkParentPath();
      _filterContext = _filterContext.createChildObjectContext(f, true);
      delegate.writeStartObject();
    } else {
      _filterContext = _filterContext.createChildObjectContext(f, false);
    }
  }

  @Override public void writeEndObject() throws IOException {
    _filterContext = _filterContext.closeObject(delegate);
    if (_filterContext != null) {
      _itemFilter = _filterContext.getFilter();
    }
  }

  @Override public void writeFieldName(String name) throws IOException {
    TokenFilter state = _filterContext.setFieldName(name);
    if (state == null) {
      _itemFilter = null;
      return;
    }
    if (state == TokenFilter.INCLUDE_ALL) {
      _itemFilter = state;
      delegate.writeFieldName(name);
      return;
    }
    state = state.includeProperty(name);
    _itemFilter = state;
    if (state == TokenFilter.INCLUDE_ALL) {
      _checkPropertyParentPath();
    }
  }

  @Override public void writeFieldName(SerializableString name) throws IOException {
    TokenFilter state = _filterContext.setFieldName(name.getValue());
    if (state == null) {
      _itemFilter = null;
      return;
    }
    if (state == TokenFilter.INCLUDE_ALL) {
      _itemFilter = state;
      delegate.writeFieldName(name);
      return;
    }
    state = state.includeProperty(name.getValue());
    _itemFilter = state;
    if (state == TokenFilter.INCLUDE_ALL) {
      _checkPropertyParentPath();
    }
  }

  @Override public void writeString(String value) throws IOException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeString(value)) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeString(value);
  }

  @Override public void writeString(char[] text, int offset, int len) throws IOException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      String value = new String(text, offset, len);
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeString(value)) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeString(text, offset, len);
  }

  @Override public void writeString(SerializableString value) throws IOException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeString(value.getValue())) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeString(value);
  }

  @Override public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
    if (_checkRawValueWrite()) {
      delegate.writeRawUTF8String(text, offset, length);
    }
  }

  @Override public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
    if (_checkRawValueWrite()) {
      delegate.writeUTF8String(text, offset, length);
    }
  }

  @Override public void writeRaw(String text) throws IOException {
    if (_checkRawValueWrite()) {
      delegate.writeRaw(text);
    }
  }

  @Override public void writeRaw(String text, int offset, int len) throws IOException {
    if (_checkRawValueWrite()) {
      delegate.writeRaw(text);
    }
  }

  @Override public void writeRaw(SerializableString text) throws IOException {
    if (_checkRawValueWrite()) {
      delegate.writeRaw(text);
    }
  }

  @Override public void writeRaw(char[] text, int offset, int len) throws IOException {
    if (_checkRawValueWrite()) {
      delegate.writeRaw(text, offset, len);
    }
  }

  @Override public void writeRaw(char c) throws IOException {
    if (_checkRawValueWrite()) {
      delegate.writeRaw(c);
    }
  }

  @Override public void writeRawValue(String text) throws IOException {
    if (_checkRawValueWrite()) {
      delegate.writeRaw(text);
    }
  }

  @Override public void writeRawValue(String text, int offset, int len) throws IOException {
    if (_checkRawValueWrite()) {
      delegate.writeRaw(text, offset, len);
    }
  }

  @Override public void writeRawValue(char[] text, int offset, int len) throws IOException {
    if (_checkRawValueWrite()) {
      delegate.writeRaw(text, offset, len);
    }
  }

  @Override public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
    if (_checkBinaryWrite()) {
      delegate.writeBinary(b64variant, data, offset, len);
    }
  }

  @Override public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
    if (_checkBinaryWrite()) {
      return delegate.writeBinary(b64variant, data, dataLength);
    }
    return -1;
  }

  @Override public void writeNumber(short v) throws IOException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeNumber(v)) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeNumber(v);
  }

  @Override public void writeNumber(int v) throws IOException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeNumber(v)) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeNumber(v);
  }

  @Override public void writeNumber(long v) throws IOException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeNumber(v)) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeNumber(v);
  }

  @Override public void writeNumber(BigInteger v) throws IOException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeNumber(v)) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeNumber(v);
  }

  @Override public void writeNumber(double v) throws IOException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeNumber(v)) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeNumber(v);
  }

  @Override public void writeNumber(float v) throws IOException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeNumber(v)) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeNumber(v);
  }

  @Override public void writeNumber(BigDecimal v) throws IOException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeNumber(v)) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeNumber(v);
  }

  @Override public void writeNumber(String encodedValue) throws IOException, UnsupportedOperationException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeRawValue()) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeNumber(encodedValue);
  }

  @Override public void writeBoolean(boolean v) throws IOException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeBoolean(v)) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeBoolean(v);
  }

  @Override public void writeNull() throws IOException {
    if (_itemFilter == null) {
      return;
    }
    if (_itemFilter != TokenFilter.INCLUDE_ALL) {
      TokenFilter state = _filterContext.checkValue(_itemFilter);
      if (state == null) {
        return;
      }
      if (state != TokenFilter.INCLUDE_ALL) {
        if (!state.includeNull()) {
          return;
        }
      }
      _checkParentPath();
    }
    delegate.writeNull();
  }

  @Override public void writeOmittedField(String fieldName) throws IOException {
    if (_itemFilter != null) {
      delegate.writeOmittedField(fieldName);
    }
  }

  @Override public void writeObjectId(Object id) throws IOException {
    if (_itemFilter != null) {
      delegate.writeObjectId(id);
    }
  }

  @Override public void writeObjectRef(Object id) throws IOException {
    if (_itemFilter != null) {
      delegate.writeObjectRef(id);
    }
  }

  @Override public void writeTypeId(Object id) throws IOException {
    if (_itemFilter != null) {
      delegate.writeTypeId(id);
    }
  }

  protected void _checkParentPath() throws IOException {
    ++_matchCount;
    if (_includePath) {
      _filterContext.writePath(delegate);
    }
    if (!_allowMultipleMatches) {
      _filterContext.skipParentChecks();
    }
  }

  /**
     * Specialized variant of {@link #_checkParentPath} used when checking
     * parent for a property name to be included with value: rules are slightly
     * different.
     */
  protected void _checkPropertyParentPath() throws IOException {
    ++_matchCount;
    if (_includePath) {
      _filterContext.writePath(delegate);
    } else {
      if (_includeImmediateParent) {
        _filterContext.writeImmediatePath(delegate);
      }
    }
    if (!_allowMultipleMatches) {
      _filterContext.skipParentChecks();
    }
  }

  protected boolean _checkBinaryWrite() throws IOException {
    if (_itemFilter == null) {
      return false;
    }
    if (_itemFilter == TokenFilter.INCLUDE_ALL) {
      return true;
    }
    if (_itemFilter.includeBinary()) {
      _checkParentPath();
      return true;
    }
    return false;
  }

  protected boolean _checkRawValueWrite() throws IOException {
    if (_itemFilter == null) {
      return false;
    }
    if (_itemFilter == TokenFilter.INCLUDE_ALL) {
      return true;
    }
    if (_itemFilter.includeRawValue()) {
      _checkParentPath();
      return true;
    }
    return false;
  }
}