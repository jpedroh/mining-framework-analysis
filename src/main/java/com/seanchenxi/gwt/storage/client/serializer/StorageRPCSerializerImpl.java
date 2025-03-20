package com.seanchenxi.gwt.storage.client.serializer;
import com.google.gwt.core.client.GWT;
import java.util.HashMap;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamReader;
import com.google.gwt.user.client.rpc.impl.Serializer;

/**
 * Default implementation of {@link StorageSerializer}
 *
 * Use GWT RPC way to realize object's serialization or deserialization.
 *
 */
final class StorageRPCSerializerImpl implements StorageSerializer {
  private static final Serializer TYPE_SERIALIZER;

  private static final HashMap<Class<?>, StorageValueType> TYPE_MAP;

  static {
    TYPE_SERIALIZER = GWT.create(StorageTypeSerializer.class);
    TYPE_MAP = new HashMap<Class<?>, StorageValueType>();
    TYPE_MAP.put(boolean[].class, StorageValueType.BOOLEAN_VECTOR);
    TYPE_MAP.put(byte[].class, StorageValueType.BYTE_VECTOR);
    TYPE_MAP.put(char[].class, StorageValueType.CHAR_VECTOR);
    TYPE_MAP.put(double[].class, StorageValueType.DOUBLE_VECTOR);
    TYPE_MAP.put(float[].class, StorageValueType.FLOAT_VECTOR);
    TYPE_MAP.put(int[].class, StorageValueType.INT_VECTOR);
    TYPE_MAP.put(long[].class, StorageValueType.LONG_VECTOR);
    TYPE_MAP.put(short[].class, StorageValueType.SHORT_VECTOR);
    TYPE_MAP.put(String[].class, StorageValueType.STRING_VECTOR);
    TYPE_MAP.put(boolean.class, StorageValueType.BOOLEAN);
    TYPE_MAP.put(byte.class, StorageValueType.BYTE);
    TYPE_MAP.put(char.class, StorageValueType.CHAR);
    TYPE_MAP.put(double.class, StorageValueType.DOUBLE);
    TYPE_MAP.put(float.class, StorageValueType.FLOAT);
    TYPE_MAP.put(int.class, StorageValueType.INT);
    TYPE_MAP.put(long.class, StorageValueType.LONG);
    TYPE_MAP.put(short.class, StorageValueType.SHORT);
    TYPE_MAP.put(String.class, StorageValueType.STRING);
  }

  @Override @SuppressWarnings(value = { "unchecked" }) public <T extends java.lang.Object> T deserialize(Class<? super T> clazz, String serializedString) throws SerializationException {
    if (serializedString == null) {
      return null;
    } else {
      if (String.class.equals(clazz)) {
        return (T) serializedString;
      }
    }
    ClientSerializationStreamReader reader = new ClientSerializationStreamReader(TYPE_SERIALIZER);
    reader.prepareToRead(serializedString);
    Object obj = findType(clazz).read(reader);
    return obj != null ? (T) obj : null;
  }

  @Override public <T extends java.lang.Object> String serialize(Class<? super T> clazz, T instance) throws SerializationException {
    if (instance == null) {
      return null;
    } else {
      if (String.class.equals(clazz)) {
        return (String) instance;
      }
    }
    StorageSerializationStreamWriter writer = new StorageSerializationStreamWriter(TYPE_SERIALIZER);
    writer.prepareToWrite();
    if (clazz.isArray()) {
      writer.writeString(TYPE_SERIALIZER.getSerializationSignature(clazz));
    }
    findType(clazz).write(writer, instance);
    return writer.toString();
  }

  private StorageValueType findType(Class<?> clazz) {
    StorageValueType type = TYPE_MAP.get(clazz);
    if (type == null) {
      type = clazz.isArray() ? StorageValueType.OBJECT_VECTOR : StorageValueType.OBJECT;
    }
    return type;
  }
}