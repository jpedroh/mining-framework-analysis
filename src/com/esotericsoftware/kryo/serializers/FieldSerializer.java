package com.esotericsoftware.kryo.serializers;
import static com.esotericsoftware.kryo.util.Util.*;
import static com.esotericsoftware.minlog.Log.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.SerializerFactory;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultGenericHandler;
import com.esotericsoftware.kryo.util.DefaultGenericHandler.GenericType;
import com.esotericsoftware.kryo.util.DefaultGenericHandler.GenericsHierarchy;
import com.esotericsoftware.kryo.util.GenericHandler;
import com.esotericsoftware.reflectasm.FieldAccess;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class FieldSerializer<T extends java.lang.Object> extends Serializer<T> {
  final Kryo kryo;

  final Class type;

  final FieldSerializerConfig config;

  final CachedFields cachedFields;

  private final GenericsHierarchy genericsHierarchy;

  public FieldSerializer(Kryo kryo, Class type) {
    this(kryo, type, new FieldSerializerConfig());
  }

  public FieldSerializer(Kryo kryo, Class type, FieldSerializerConfig config) {
    if (type == null) {
      throw new IllegalArgumentException("type cannot be null.");
    }
    if (type.isPrimitive()) {
      throw new IllegalArgumentException("type cannot be a primitive class: " + type);
    }
    if (config == null) {
      throw new IllegalArgumentException("config cannot be null.");
    }
    this.kryo = kryo;
    this.type = type;
    this.config = config;
    genericsHierarchy = new GenericsHierarchy(type);
    cachedFields = new CachedFields(this);
    cachedFields.rebuild();
  }

  protected void initializeCachedFields() {
  }

  public FieldSerializerConfig getFieldSerializerConfig() {
    return config;
  }

  public void updateFields() {
    if (TRACE) {
      trace("kryo", "Update fields: " + className(type));
    }
    cachedFields.rebuild();
  }

  public void write(Kryo kryo, Output output, T object) {
    int pop = pushTypeVariables();
    CachedField[] fields = cachedFields.fields;
    for (int i = 0, n = fields.length; i < n; i++) {
      if (TRACE) {
        log("Write", fields[i], output.position());
      }
      fields[i].write(output, object);
    }
    if (pop > 0) {
      popTypeVariables(pop);
    }
  }

  public T read(Kryo kryo, Input input, Class<? extends T> type) {
    int pop = pushTypeVariables();
    T object = create(kryo, input, type);
    kryo.reference(object);
    CachedField[] fields = cachedFields.fields;
    for (int i = 0, n = fields.length; i < n; i++) {
      if (TRACE) {
        log("Read", fields[i], input.position());
      }
      fields[i].read(input, object);
    }
    if (pop > 0) {
      popTypeVariables(pop);
    }
    return object;
  }

  protected int pushTypeVariables() {
    if (genericsHierarchy.isEmpty()) {
      return 0;
    }
    GenericType[] genericTypes = kryo.getGenerics().nextGenericTypes();
    if (genericTypes == null) {
      return 0;
    }
    int pop = kryo.getGenerics().pushTypeVariables(genericsHierarchy, genericTypes);
    if (TRACE && pop > 0) {
      trace("kryo", "Generics: " + kryo.getGenerics());
    }
    return pop;
  }

  protected void popTypeVariables(int pop) {
    GenericHandler generics = kryo.getGenerics();
    generics.popTypeVariables(pop);
    generics.popGenericType();
  }

  protected T create(Kryo kryo, Input input, Class<? extends T> type) {
    return kryo.newInstance(type);
  }

  protected void log(String prefix, CachedField cachedField, int position) {
    String fieldClassName;
    if (cachedField instanceof ReflectField) {
      ReflectField reflectField = (ReflectField) cachedField;
      Class fieldClass = reflectField.resolveFieldClass();
      if (fieldClass == null) {
        fieldClass = cachedField.field.getType();
      }
      fieldClassName = simpleName(fieldClass, reflectField.genericType);
    } else {
      if (cachedField.valueClass != null) {
        fieldClassName = cachedField.valueClass.getSimpleName();
      } else {
        fieldClassName = cachedField.field.getType().getSimpleName();
      }
    }
    trace("kryo", prefix + " field " + fieldClassName + ": " + cachedField.name + " (" + className(cachedField.field.getDeclaringClass()) + ')' + pos(position));
  }

  public CachedField getField(String fieldName) {
    for (CachedField cachedField : cachedFields.fields) {
      if (cachedField.name.equals(fieldName)) {
        return cachedField;
      }
    }
    throw new IllegalArgumentException("Field \"" + fieldName + "\" not found on class: " + type.getName());
  }

  public void removeField(String fieldName) {
    cachedFields.removeField(fieldName);
  }

  public void removeField(CachedField field) {
    cachedFields.removeField(field);
  }

  public CachedField[] getFields() {
    return cachedFields.fields;
  }

  public CachedField[] getCopyFields() {
    return cachedFields.copyFields;
  }

  public Class getType() {
    return type;
  }

  public Kryo getKryo() {
    return kryo;
  }

  protected T createCopy(Kryo kryo, T original) {
    return (T) kryo.newInstance(original.getClass());
  }

  public T copy(Kryo kryo, T original) {
    T copy = createCopy(kryo, original);
    kryo.reference(copy);
    for (int i = 0, n = cachedFields.copyFields.length; i < n; i++) {
      cachedFields.copyFields[i].copy(original, copy);
    }
    return copy;
  }

  static public abstract class CachedField {
    final Field field;

    String name;

    Class valueClass;

    Serializer serializer;

    boolean canBeNull, varEncoding = true, optimizePositive;

    FieldAccess access;

    int accessIndex = -1;

    long offset;

    int tag;

    public CachedField(Field field) {
      this.field = field;
    }

    public void setValueClass(Class valueClass) {
      this.valueClass = valueClass;
    }

    public Class getValueClass() {
      return valueClass;
    }

    public void setValueClass(Class valueClass, Serializer serializer) {
      this.valueClass = valueClass;
      this.serializer = serializer;
    }

    public void setSerializer(Serializer serializer) {
      this.serializer = serializer;
    }

    public Serializer getSerializer() {
      return this.serializer;
    }

    public void setCanBeNull(boolean canBeNull) {
      this.canBeNull = canBeNull;
    }

    public boolean getCanBeNull() {
      return canBeNull;
    }

    public void setVariableLengthEncoding(boolean varEncoding) {
      this.varEncoding = varEncoding;
    }

    public boolean getVariableLengthEncoding() {
      return varEncoding;
    }

    public void setOptimizePositive(boolean optimizePositive) {
      this.optimizePositive = optimizePositive;
    }

    public boolean getOptimizePositive() {
      return optimizePositive;
    }

    public String getName() {
      return name;
    }

    public Field getField() {
      return field;
    }

    public String toString() {
      return name;
    }

    abstract public void write(Output output, Object object);

    abstract public void read(Input input, Object object);

    abstract public void copy(Object original, Object copy);
  }

  @Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.FIELD }) public @interface Optional {
    public String value();
  }

  @Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.FIELD }) public @interface Bind {
    Class valueClass() default Object.class;

    Class<? extends Serializer> serializer() default Serializer.class;

    Class<? extends SerializerFactory> serializerFactory() default SerializerFactory.class;

    boolean canBeNull() default true;

    boolean variableLengthEncoding() default true;

    boolean optimizePositive() default false;
  }

  @Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.FIELD }) public @interface NotNull {
  }

  static public class FieldSerializerConfig implements Cloneable {
    boolean fieldsCanBeNull = true;

    boolean setFieldsAsAccessible = true;

    boolean ignoreSyntheticFields = true;

    boolean fixedFieldTypes;

    boolean copyTransient = true;

    boolean serializeTransient;

    boolean varEncoding = true;

    boolean extendedFieldNames;

    public FieldSerializerConfig clone() {
      try {
        return (FieldSerializerConfig) super.clone();
      } catch (CloneNotSupportedException ex) {
        throw new KryoException(ex);
      }
    }

    public void setFieldsCanBeNull(boolean fieldsCanBeNull) {
      this.fieldsCanBeNull = fieldsCanBeNull;
      if (TRACE) {
        trace("kryo", "FieldSerializerConfig fieldsCanBeNull: " + fieldsCanBeNull);
      }
    }

    public boolean getFieldsCanBeNull() {
      return fieldsCanBeNull;
    }

    public void setFieldsAsAccessible(boolean setFieldsAsAccessible) {
      this.setFieldsAsAccessible = setFieldsAsAccessible;
      if (TRACE) {
        trace("kryo", "FieldSerializerConfig setFieldsAsAccessible: " + setFieldsAsAccessible);
      }
    }

    public boolean getSetFieldsAsAccessible() {
      return setFieldsAsAccessible;
    }

    public void setIgnoreSyntheticFields(boolean ignoreSyntheticFields) {
      this.ignoreSyntheticFields = ignoreSyntheticFields;
      if (TRACE) {
        trace("kryo", "FieldSerializerConfig ignoreSyntheticFields: " + ignoreSyntheticFields);
      }
    }

    public boolean getIgnoreSyntheticFields() {
      return ignoreSyntheticFields;
    }

    public void setFixedFieldTypes(boolean fixedFieldTypes) {
      this.fixedFieldTypes = fixedFieldTypes;
      if (TRACE) {
        trace("kryo", "FieldSerializerConfig fixedFieldTypes: " + fixedFieldTypes);
      }
    }

    public boolean getFixedFieldTypes() {
      return fixedFieldTypes;
    }

    public void setCopyTransient(boolean copyTransient) {
      this.copyTransient = copyTransient;
      if (TRACE) {
        trace("kryo", "FieldSerializerConfig copyTransient: " + copyTransient);
      }
    }

    public boolean getCopyTransient() {
      return copyTransient;
    }

    public void setSerializeTransient(boolean serializeTransient) {
      this.serializeTransient = serializeTransient;
      if (TRACE) {
        trace("kryo", "FieldSerializerConfig serializeTransient: " + serializeTransient);
      }
    }

    public boolean getSerializeTransient() {
      return serializeTransient;
    }

    public void setVariableLengthEncoding(boolean varEncoding) {
      this.varEncoding = varEncoding;
      if (TRACE) {
        trace("kryo", "FieldSerializerConfig variable length encoding: " + varEncoding);
      }
    }

    public boolean getVariableLengthEncoding() {
      return varEncoding;
    }

    public void setExtendedFieldNames(boolean extendedFieldNames) {
      this.extendedFieldNames = extendedFieldNames;
      if (TRACE) {
        trace("kryo", "FieldSerializerConfig extendedFieldNames: " + extendedFieldNames);
      }
    }

    public boolean getExtendedFieldNames() {
      return extendedFieldNames;
    }
  }
}