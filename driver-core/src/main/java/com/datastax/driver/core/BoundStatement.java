package com.datastax.driver.core;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;
import com.datastax.driver.core.exceptions.InvalidTypeException;

public class BoundStatement extends Statement implements SettableData<BoundStatement>, GettableData {
  private static final ByteBuffer UNSET = ByteBuffer.allocate(0);

  final PreparedStatement statement;

  final DataWrapper wrapper;

  public BoundStatement(PreparedStatement statement) {
    this.statement = statement;
    this.wrapper = new DataWrapper(this, statement.getVariables().size());
    for (int i = 0; i < wrapper.values.length; i++) {
      wrapper.values[i] = UNSET;
    }
    if (statement.getConsistencyLevel() != null) {
      this.setConsistencyLevel(statement.getConsistencyLevel());
    }
    if (statement.getSerialConsistencyLevel() != null) {
      this.setSerialConsistencyLevel(statement.getSerialConsistencyLevel());
    }
    if (statement.isTracing()) {
      this.enableTracing();
    }
    if (statement.getRetryPolicy() != null) {
      this.setRetryPolicy(statement.getRetryPolicy());
    }
  }

  public PreparedStatement preparedStatement() {
    return statement;
  }

  public boolean isSet(int i) {
    return wrapper.getValue(i) != UNSET;
  }

  public boolean isSet(String name) {
    return wrapper.getValue(wrapper.getIndexOf(name)) != UNSET;
  }

  public BoundStatement bind(Object... values) {
    if (values.length > statement.getVariables().size()) {
      throw new IllegalArgumentException(String.format("Prepared statement has only %d variables, %d values provided", statement.getVariables().size(), values.length));
    }
    for (int i = 0; i < values.length; i++) {
      Object toSet = values[i];
      if (toSet == null) {
        wrapper.values[i] = null;
        continue;
      }
      DataType columnType = statement.getVariables().getType(i);
      switch (columnType.getName()) {
        case LIST:
        if (!(toSet instanceof List)) {
          throw new InvalidTypeException(String.format("Invalid type for value %d, column is a list but %s provided", i, toSet.getClass()));
        }
        List<?> l = (List<?>) toSet;
        if (!l.isEmpty()) {
          Class<?> providedClass = l.get(0).getClass();
          Class<?> expectedClass = columnType.getTypeArguments().get(0).asJavaClass();
          if (!expectedClass.isAssignableFrom(providedClass)) {
            throw new InvalidTypeException(String.format("Invalid type for value %d of CQL type %s, expecting list of %s but provided list of %s", i, columnType, expectedClass, providedClass));
          }
        }
        break;
        case SET:
        if (!(toSet instanceof Set)) {
          throw new InvalidTypeException(String.format("Invalid type for value %d, column is a set but %s provided", i, toSet.getClass()));
        }
        Set<?> s = (Set<?>) toSet;
        if (!s.isEmpty()) {
          Class<?> providedClass = s.iterator().next().getClass();
          Class<?> expectedClass = columnType.getTypeArguments().get(0).getName().javaType;
          if (!expectedClass.isAssignableFrom(providedClass)) {
            throw new InvalidTypeException(String.format("Invalid type for value %d of CQL type %s, expecting set of %s but provided set of %s", i, columnType, expectedClass, providedClass));
          }
        }
        break;
        case MAP:
        if (!(toSet instanceof Map)) {
          throw new InvalidTypeException(String.format("Invalid type for value %d, column is a map but %s provided", i, toSet.getClass()));
        }
        Map<?, ?> m = (Map<?, ?>) toSet;
        if (!m.isEmpty()) {
          Map.Entry<?, ?> entry = m.entrySet().iterator().next();
          Class<?> providedKeysClass = entry.getKey().getClass();
          Class<?> providedValuesClass = entry.getValue().getClass();
          Class<?> expectedKeysClass = columnType.getTypeArguments().get(0).getName().javaType;
          Class<?> expectedValuesClass = columnType.getTypeArguments().get(1).getName().javaType;
          if (!expectedKeysClass.isAssignableFrom(providedKeysClass) || !expectedValuesClass.isAssignableFrom(providedValuesClass)) {
            throw new InvalidTypeException(String.format("Invalid type for value %d of CQL type %s, expecting map of %s->%s but provided set of %s->%s", i, columnType, expectedKeysClass, expectedValuesClass, providedKeysClass, providedValuesClass));
          }
        }
        break;
        default:
        Class<?> providedClass = toSet.getClass();
        Class<?> expectedClass = columnType.getName().javaType;
        if (!expectedClass.isAssignableFrom(providedClass)) {
          throw new InvalidTypeException(String.format("Invalid type for value %d of CQL type %s, expecting %s but %s provided", i, columnType, expectedClass, providedClass));
        }
        break;
      }
      wrapper.values[i] = columnType.codec(statement.getPreparedId().protocolVersion).serialize(toSet);
    }
    return this;
  }

  @Override public ByteBuffer getRoutingKey() {
    if (this.routingKey != null) {
      return this.routingKey;
    }
    if (statement.getRoutingKey() != null) {
      return statement.getRoutingKey();
    }
    int[] rkIndexes = statement.getPreparedId().routingKeyIndexes;
    if (rkIndexes != null) {
      if (rkIndexes.length == 1) {
        return wrapper.values[rkIndexes[0]];
      } else {
        ByteBuffer[] components = new ByteBuffer[rkIndexes.length];
        for (int i = 0; i < components.length; ++i) {
          ByteBuffer value = wrapper.values[rkIndexes[i]];
          if (value == null) {
            return null;
          }
          components[i] = value;
        }
        return SimpleStatement.compose(components);
      }
    }
    return null;
  }

  @Override public String getKeyspace() {
    return statement.getPreparedId().metadata.size() == 0 ? null : statement.getPreparedId().metadata.getKeyspace(0);
  }

  public BoundStatement setBool(int i, boolean v) {
    return wrapper.setBool(i, v);
  }

  public BoundStatement setBool(String name, boolean v) {
    return wrapper.setBool(name, v);
  }

  public BoundStatement setInt(int i, int v) {
    return wrapper.setInt(i, v);
  }

  public BoundStatement setInt(String name, int v) {
    return wrapper.setInt(name, v);
  }

  public BoundStatement setLong(int i, long v) {
    return wrapper.setLong(i, v);
  }

  public BoundStatement setLong(String name, long v) {
    return wrapper.setLong(name, v);
  }

  public BoundStatement setDate(int i, Date v) {
    return wrapper.setDate(i, v);
  }

  public BoundStatement setDate(String name, Date v) {
    return wrapper.setDate(name, v);
  }

  public BoundStatement setFloat(int i, float v) {
    return wrapper.setFloat(i, v);
  }

  public BoundStatement setFloat(String name, float v) {
    return wrapper.setFloat(name, v);
  }

  public BoundStatement setDouble(int i, double v) {
    return wrapper.setDouble(i, v);
  }

  public BoundStatement setDouble(String name, double v) {
    return wrapper.setDouble(name, v);
  }

  public BoundStatement setString(int i, String v) {
    return wrapper.setString(i, v);
  }

  public BoundStatement setString(String name, String v) {
    return wrapper.setString(name, v);
  }

  public BoundStatement setBytes(int i, ByteBuffer v) {
    return wrapper.setBytes(i, v);
  }

  public BoundStatement setBytes(String name, ByteBuffer v) {
    return wrapper.setBytes(name, v);
  }

  public BoundStatement setBytesUnsafe(int i, ByteBuffer v) {
    return wrapper.setBytesUnsafe(i, v);
  }

  public BoundStatement setBytesUnsafe(String name, ByteBuffer v) {
    return wrapper.setBytesUnsafe(name, v);
  }

  public BoundStatement setVarint(int i, BigInteger v) {
    return wrapper.setVarint(i, v);
  }

  public BoundStatement setVarint(String name, BigInteger v) {
    return wrapper.setVarint(name, v);
  }

  public BoundStatement setDecimal(int i, BigDecimal v) {
    return wrapper.setDecimal(i, v);
  }

  public BoundStatement setDecimal(String name, BigDecimal v) {
    return wrapper.setDecimal(name, v);
  }

  public BoundStatement setUUID(int i, UUID v) {
    return wrapper.setUUID(i, v);
  }

  public BoundStatement setUUID(String name, UUID v) {
    return wrapper.setUUID(name, v);
  }

  public BoundStatement setInet(int i, InetAddress v) {
    return wrapper.setInet(i, v);
  }

  public BoundStatement setInet(String name, InetAddress v) {
    return wrapper.setInet(name, v);
  }

  public <T extends java.lang.Object> BoundStatement setList(int i, List<T> v) {
    return wrapper.setList(i, v);
  }

  public <T extends java.lang.Object> BoundStatement setList(String name, List<T> v) {
    return wrapper.setList(name, v);
  }

  public <K extends java.lang.Object, V extends java.lang.Object> BoundStatement setMap(int i, Map<K, V> v) {
    return wrapper.setMap(i, v);
  }

  public <K extends java.lang.Object, V extends java.lang.Object> BoundStatement setMap(String name, Map<K, V> v) {
    return wrapper.setMap(name, v);
  }

  public <T extends java.lang.Object> BoundStatement setSet(int i, Set<T> v) {
    return wrapper.setSet(i, v);
  }

  public <T extends java.lang.Object> BoundStatement setSet(String name, Set<T> v) {
    return wrapper.setSet(name, v);
  }

  public BoundStatement setUDTValue(int i, UDTValue v) {
    return wrapper.setUDTValue(i, v);
  }

  public BoundStatement setUDTValue(String name, UDTValue v) {
    return wrapper.setUDTValue(name, v);
  }

  public BoundStatement setTupleValue(int i, TupleValue v) {
    return wrapper.setTupleValue(i, v);
  }

  public BoundStatement setTupleValue(String name, TupleValue v) {
    return wrapper.setTupleValue(name, v);
  }

  public BoundStatement setToNull(int i) {
    return wrapper.setToNull(i);
  }

  public BoundStatement setToNull(String name) {
    return wrapper.setToNull(name);
  }

  public boolean isNull(int i) {
    return wrapper.isNull(i);
  }

  public boolean isNull(String name) {
    return wrapper.isNull(name);
  }

  public boolean getBool(int i) {
    return wrapper.getBool(i);
  }

  public boolean getBool(String name) {
    return wrapper.getBool(name);
  }

  public int getInt(int i) {
    return wrapper.getInt(i);
  }

  public int getInt(String name) {
    return wrapper.getInt(name);
  }

  public long getLong(int i) {
    return wrapper.getLong(i);
  }

  public long getLong(String name) {
    return wrapper.getLong(name);
  }

  public Date getDate(int i) {
    return wrapper.getDate(i);
  }

  public Date getDate(String name) {
    return wrapper.getDate(name);
  }

  public float getFloat(int i) {
    return wrapper.getFloat(i);
  }

  public float getFloat(String name) {
    return wrapper.getFloat(name);
  }

  public double getDouble(int i) {
    return wrapper.getDouble(i);
  }

  public double getDouble(String name) {
    return wrapper.getDouble(name);
  }

  public ByteBuffer getBytesUnsafe(int i) {
    return wrapper.getBytesUnsafe(i);
  }

  public ByteBuffer getBytesUnsafe(String name) {
    return wrapper.getBytesUnsafe(name);
  }

  public ByteBuffer getBytes(int i) {
    return wrapper.getBytes(i);
  }

  public ByteBuffer getBytes(String name) {
    return wrapper.getBytes(name);
  }

  public String getString(int i) {
    return wrapper.getString(i);
  }

  public String getString(String name) {
    return wrapper.getString(name);
  }

  public BigInteger getVarint(int i) {
    return wrapper.getVarint(i);
  }

  public BigInteger getVarint(String name) {
    return wrapper.getVarint(name);
  }

  public BigDecimal getDecimal(int i) {
    return wrapper.getDecimal(i);
  }

  public BigDecimal getDecimal(String name) {
    return wrapper.getDecimal(name);
  }

  public UUID getUUID(int i) {
    return wrapper.getUUID(i);
  }

  public UUID getUUID(String name) {
    return wrapper.getUUID(name);
  }

  public InetAddress getInet(int i) {
    return wrapper.getInet(i);
  }

  public InetAddress getInet(String name) {
    return wrapper.getInet(name);
  }

  public <T extends java.lang.Object> List<T> getList(int i, Class<T> elementsClass) {
    return wrapper.getList(i, elementsClass);
  }

  public <T extends java.lang.Object> List<T> getList(String name, Class<T> elementsClass) {
    return wrapper.getList(name, elementsClass);
  }

  public <T extends java.lang.Object> Set<T> getSet(int i, Class<T> elementsClass) {
    return wrapper.getSet(i, elementsClass);
  }

  public <T extends java.lang.Object> Set<T> getSet(String name, Class<T> elementsClass) {
    return wrapper.getSet(name, elementsClass);
  }

  public <K extends java.lang.Object, V extends java.lang.Object> Map<K, V> getMap(int i, Class<K> keysClass, Class<V> valuesClass) {
    return wrapper.getMap(i, keysClass, valuesClass);
  }

  public <K extends java.lang.Object, V extends java.lang.Object> Map<K, V> getMap(String name, Class<K> keysClass, Class<V> valuesClass) {
    return wrapper.getMap(name, keysClass, valuesClass);
  }

  public UDTValue getUDTValue(int i) {
    return wrapper.getUDTValue(i);
  }

  public UDTValue getUDTValue(String name) {
    return wrapper.getUDTValue(name);
  }

  public TupleValue getTupleValue(int i) {
    return wrapper.getTupleValue(i);
  }

  public TupleValue getTupleValue(String name) {
    return wrapper.getTupleValue(name);
  }

  static class DataWrapper extends AbstractData<BoundStatement> {
    DataWrapper(BoundStatement wrapped, int size) {
      super(wrapped.statement.getPreparedId().protocolVersion, wrapped, size);
    }

    protected int[] getAllIndexesOf(String name) {
      return wrapped.statement.getVariables().getAllIdx(name);
    }

    protected DataType getType(int i) {
      return wrapped.statement.getVariables().getType(i);
    }

    protected String getName(int i) {
      return wrapped.statement.getVariables().getName(i);
    }
  }

  void ensureAllSet() {
    int index = 0;
    for (ByteBuffer value : wrapper.values) {
      if (value == BoundStatement.UNSET) {
        throw new IllegalStateException("Unset value at index " + index + ". " + "If you want this value to be null, please set it to null explicitly.");
      }
      index += 1;
    }
  }

  private ByteBuffer routingKey;

  public BoundStatement setRoutingKey(ByteBuffer routingKey) {
    this.routingKey = routingKey;
    return this;
  }
}