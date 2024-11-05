package com.esotericsoftware.kryo.serializers;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.SerializerFactory;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultGenericHandler.GenericType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MapSerializer<T extends Map> extends Serializer<T> {
  private Class keyClass, valueClass;

  private Serializer keySerializer, valueSerializer;

  private boolean keysCanBeNull = true, valuesCanBeNull = true;

  public MapSerializer() {
    setAcceptsNull(true);
  }

  public void setKeysCanBeNull(boolean keysCanBeNull) {
    this.keysCanBeNull = keysCanBeNull;
  }

  public void setKeyClass(Class keyClass) {
    this.keyClass = keyClass;
  }

  public Class getKeyClass() {
    return keyClass;
  }

  public void setKeyClass(Class keyClass, Serializer keySerializer) {
    this.keyClass = keyClass;
    this.keySerializer = keySerializer;
  }

  public void setKeySerializer(Serializer keySerializer) {
    this.keySerializer = keySerializer;
  }

  public Serializer getKeySerializer() {
    return this.keySerializer;
  }

  public void setValueClass(Class valueClass) {
    this.valueClass = valueClass;
  }

  public Class getValueClass() {
    return valueClass;
  }

  public void setValueClass(Class valueClass, Serializer valueSerializer) {
    this.valueClass = valueClass;
    this.valueSerializer = valueSerializer;
  }

  public void setValueSerializer(Serializer valueSerializer) {
    this.valueSerializer = valueSerializer;
  }

  public Serializer getValueSerializer() {
    return this.valueSerializer;
  }

  public void setValuesCanBeNull(boolean valuesCanBeNull) {
    this.valuesCanBeNull = valuesCanBeNull;
  }

  public void write(Kryo kryo, Output output, T map) {
    if (map == null) {
      output.writeByte(0);
      return;
    }
    int size = map.size();
    if (size == 0) {
      output.writeByte(1);
      writeHeader(kryo, output, map);
      return;
    }
    output.writeVarInt(size + 1, true);
    writeHeader(kryo, output, map);
    Serializer keySerializer = this.keySerializer, valueSerializer = this.valueSerializer;
    GenericType[] genericTypes = kryo.getGenerics().nextGenericTypes();
    if (genericTypes != null) {
      if (keySerializer == null) {
        Class keyType = genericTypes[0].resolve(kryo.getGenerics());
        if (keyType != null && kryo.isFinal(keyType)) {
          keySerializer = kryo.getSerializer(keyType);
        }
      }
      if (valueSerializer == null) {
        Class valueType = genericTypes[1].resolve(kryo.getGenerics());
        if (valueType != null && kryo.isFinal(valueType)) {
          valueSerializer = kryo.getSerializer(valueType);
        }
      }
    }
    for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
      Entry entry = (Entry) iter.next();
      if (genericTypes != null) {
        kryo.getGenerics().pushGenericType(genericTypes[0]);
      }
      if (keySerializer != null) {
        if (keysCanBeNull) {
          kryo.writeObjectOrNull(output, entry.getKey(), keySerializer);
        } else {
          kryo.writeObject(output, entry.getKey(), keySerializer);
        }
      } else {
        kryo.writeClassAndObject(output, entry.getKey());
      }
      if (genericTypes != null) {
        kryo.getGenerics().popGenericType();
      }
      if (valueSerializer != null) {
        if (valuesCanBeNull) {
          kryo.writeObjectOrNull(output, entry.getValue(), valueSerializer);
        } else {
          kryo.writeObject(output, entry.getValue(), valueSerializer);
        }
      } else {
        kryo.writeClassAndObject(output, entry.getValue());
      }
    }
    kryo.getGenerics().popGenericType();
  }

  protected void writeHeader(Kryo kryo, Output output, T map) {
  }

  protected T create(Kryo kryo, Input input, Class<? extends T> type, int size) {
    if (type == HashMap.class) {
      if (size < 3) {
        size++;
      } else {
        if (size < 1073741824) {
          size = (int) (size / 0.75f + 1);
        }
      }
      return (T) new HashMap(size);
    }
    return kryo.newInstance(type);
  }

  public T read(Kryo kryo, Input input, Class<? extends T> type) {
    int length = input.readVarInt(true);
    if (length == 0) {
      return null;
    }
    length--;
    T map = create(kryo, input, type, length);
    kryo.reference(map);
    if (length == 0) {
      return map;
    }
    Class keyClass = this.keyClass;
    Class valueClass = this.valueClass;
    Serializer keySerializer = this.keySerializer, valueSerializer = this.valueSerializer;
    GenericType[] genericTypes = kryo.getGenerics().nextGenericTypes();
    if (genericTypes != null) {
      if (keySerializer == null) {
        Class genericClass = genericTypes[0].resolve(kryo.getGenerics());
        if (genericClass != null && kryo.isFinal(genericClass)) {
          keySerializer = kryo.getSerializer(genericClass);
          keyClass = genericClass;
        }
      }
      if (valueSerializer == null) {
        Class genericClass = genericTypes[1].resolve(kryo.getGenerics());
        if (genericClass != null && kryo.isFinal(genericClass)) {
          valueSerializer = kryo.getSerializer(genericClass);
          valueClass = genericClass;
        }
      }
    }
    for (int i = 0; i < length; i++) {
      Object key;
      if (genericTypes != null) {
        kryo.getGenerics().pushGenericType(genericTypes[0]);
      }
      if (keySerializer != null) {
        if (keysCanBeNull) {
          key = kryo.readObjectOrNull(input, keyClass, keySerializer);
        } else {
          key = kryo.readObject(input, keyClass, keySerializer);
        }
      } else {
        key = kryo.readClassAndObject(input);
      }
      if (genericTypes != null) {
        kryo.getGenerics().popGenericType();
      }
      Object value;
      if (valueSerializer != null) {
        if (valuesCanBeNull) {
          value = kryo.readObjectOrNull(input, valueClass, valueSerializer);
        } else {
          value = kryo.readObject(input, valueClass, valueSerializer);
        }
      } else {
        value = kryo.readClassAndObject(input);
      }
      map.put(key, value);
    }
    kryo.getGenerics().popGenericType();
    return map;
  }

  protected T createCopy(Kryo kryo, T original) {
    return (T) kryo.newInstance(original.getClass());
  }

  public T copy(Kryo kryo, T original) {
    T copy = createCopy(kryo, original);
    for (Iterator iter = original.entrySet().iterator(); iter.hasNext(); ) {
      Entry entry = (Entry) iter.next();
      copy.put(kryo.copy(entry.getKey()), kryo.copy(entry.getValue()));
    }
    return copy;
  }

  @Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.FIELD }) public @interface BindMap {
    Class keyClass() default Object.class;

    Class<? extends Serializer> keySerializer() default Serializer.class;

    Class<? extends SerializerFactory> keySerializerFactory() default SerializerFactory.class;

    Class valueClass() default Object.class;

    Class<? extends Serializer> valueSerializer() default Serializer.class;

    Class<? extends SerializerFactory> valueSerializerFactory() default SerializerFactory.class;

    boolean keysCanBeNull() default true;

    boolean valuesCanBeNull() default true;
  }
}