package com.datastax.driver.core;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;
import com.datastax.driver.core.exceptions.InvalidTypeException;

/**
 * Implementation of a Row backed by an ArrayList.
 */
class ArrayBackedRow extends AbstractGettableData implements Row {
  private final ColumnDefinitions metadata;

  private final List<ByteBuffer> data;

  private ArrayBackedRow(ColumnDefinitions metadata, int protocolVersion, List<ByteBuffer> data) {
    super(protocolVersion);
    this.metadata = metadata;
    this.data = data;
  }

  static Row fromData(ColumnDefinitions metadata, int protocolVersion, List<ByteBuffer> data) {
    if (data == null) {
      return null;
    }
    return new ArrayBackedRow(metadata, protocolVersion, data);
  }

  @Override public ColumnDefinitions getColumnDefinitions() {
    return metadata;
  }

  @Override protected DataType getType(int i) {
    return metadata.getType(i);
  }

  @Override protected String getName(int i) {
    return metadata.getName(i);
  }

  @Override protected ByteBuffer getValue(int i) {
    return data.get(i);
  }

  @Override protected int getIndexOf(String name) {
    return metadata.getFirstIdx(name);
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Row[");
    for (int i = 0; i < metadata.size(); i++) {
      if (i != 0) {
        sb.append(", ");
      }
      ByteBuffer bb = data.get(i);
      if (bb == null) {
        sb.append("NULL");
      } else {
        sb.append(metadata.getType(i).codec(protocolVersion).deserialize(bb).toString());
      }
    }
    sb.append(']');
    return sb.toString();
  }
}