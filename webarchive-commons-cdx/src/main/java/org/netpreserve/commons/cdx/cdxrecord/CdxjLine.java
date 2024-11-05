package org.netpreserve.commons.cdx.cdxrecord;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.netpreserve.commons.cdx.CdxRecordKey;
import org.netpreserve.commons.cdx.FieldName;
import org.netpreserve.commons.cdx.HasUnparsedData;
import org.netpreserve.commons.cdx.json.NullValue;
import org.netpreserve.commons.cdx.json.SimpleJsonParser;
import org.netpreserve.commons.cdx.json.Value;
import org.netpreserve.commons.util.ArrayUtil;

/**
 * A CdxRecord constructed from a CDXJ formatted input line.
 */
public class CdxjLine extends BaseCdxRecord<CdxjLineFormat> implements HasUnparsedData {
  final char[] data;

  final int jsonBlockOffset;

  Map<FieldName, Field> fields;

  /**
     * Construct a new CdxjLine.
     * <p>
     * @param line a string containing the raw data.
     * @param format the format of the input line.
     */
  public CdxjLine(final String line, final CdxjLineFormat format) {
    this(line.toCharArray(), format);
  }

  /**
     * Construct a new CdxjLine.
     * <p>
     * @param line a character array containing the raw data.
     * @param format the format of the input line.
     */
  public CdxjLine(final char[] line, final CdxjLineFormat format) {
    super(getKeyFromLine(line), format);
    this.data = line;
    this.jsonBlockOffset = ((CdxjLineRecordKey) getKey()).getUnparsed().length;
  }

  @Override public <T extends java.lang.Object> Value<T> get(FieldName<T> fieldName) {
    parseFields();
    Field f = fields.get(fieldName);
    if (f == null) {
      return NullValue.NULL;
    } else {
      return f.getValue();
    }
  }

  @Override public boolean hasField(FieldName fieldName) {
    parseFields();
    return fields.containsKey(fieldName);
  }

  @Override public Iterator<Field> iterator() {
    parseFields();
    return fields.values().iterator();
  }

  /**
     * Parse the raw input line.
     */
  final void parseFields() {
    if (fields == null) {
      Map<FieldName, Value> parsedFieldMap = new SimpleJsonParser(data, jsonBlockOffset).parseObject();
      fields = new HashMap<>();
      for (Map.Entry<FieldName, Value> entry : parsedFieldMap.entrySet()) {
        fields.put(entry.getKey(), new ImmutableField(entry.getKey(), entry.getValue()));
      }
    }
  }

  /**
     * Extract the fields before the json block from a CDXJ line.
     * <p>
     * @param line the line containing the key
     * @return a CdxRecordKey with the parsed values
     */
  static final CdxRecordKey getKeyFromLine(final char[] line) {
    int indexOfJsonBlock = ArrayUtil.indexOf(line, 0, ' ', '{') + 1;
    if (indexOfJsonBlock == -1) {
      throw new IllegalArgumentException("The CDX record \'" + new String(line) + "\' cannot be parsed: JSON block is missing");
    }
    return new CdxjLineRecordKey(Arrays.copyOf(line, indexOfJsonBlock - 1));
  }

  @Override public char[] getUnparsed() {
    return data;
  }

  @Override public String toString() {
    return getKey().toString() + ' ' + String.copyValueOf(data, jsonBlockOffset, data.length - jsonBlockOffset).trim();
  }

  @Override public int hashCode() {
    int hash = 3;
    hash = 31 * hash + Arrays.hashCode(this.data);
    return hash;
  }

  @Override public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CdxjLine other = (CdxjLine) obj;
    if (!Arrays.equals(this.data, other.data)) {
      return false;
    }
    return true;
  }
}