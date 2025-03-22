package com.esotericsoftware.kryo.serializers;
import static com.esotericsoftware.kryo.Kryo.*;
import static com.esotericsoftware.minlog.Log.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;

/** Serializes objects using direct field assignment, providing backward compatibility with minimal overhead. This means fields
 * can be added without invalidating previously serialized bytes. Removing, renaming, or changing the type of a field is not
 * supported.
 * <p>
 * When a field is added, it must have the {@link Since} annotation to indicate the version it was added in order to be compatible
 * with previously serialized bytes. The annotation value must never change.
 * <p>
 * Compared to {@link FieldSerializer}, VersionFieldSerializer writes a single additional varint and requires annotations for
 * added fields, but provides backward compatibility so fields can be added. {@link TaggedFieldSerializer} provides more
 * flexibility for classes to evolve in exchange for a slightly larger serialized size.
 * @author Nathan Sweet */
public class VersionFieldSerializer<T extends java.lang.Object> extends FieldSerializer<T> {
  private final VersionFieldSerializerConfig config;

  private int typeVersion;

  private int[] fieldVersion;

  public VersionFieldSerializer(Kryo kryo, Class type) {
    this(kryo, type, new VersionFieldSerializerConfig());
  }

  public VersionFieldSerializer(Kryo kryo, Class type, VersionFieldSerializerConfig config) {
    super(kryo, type, config);
    this.config = config;
    setAcceptsNull(true);
    initializeCachedFields();
  }

  protected void initializeCachedFields() {
    CachedField[] fields = cachedFields.fields;
    fieldVersion = new int[fields.length];
    for (int i = 0, n = fields.length; i < n; i++) {
      Field field = fields[i].field;
      Since since = field.getAnnotation(Since.class);
      if (since != null) {
        fieldVersion[i] = since.value();
        typeVersion = Math.max(fieldVersion[i], typeVersion);
      } else {
        fieldVersion[i] = 0;
      }
    }
    if (DEBUG) {
      debug("Version for type " + getType().getName() + ": " + typeVersion);
    }
  }

  public void removeField(String fieldName) {
    super.removeField(fieldName);
    initializeCachedFields();
  }

  public void removeField(CachedField field) {
    super.removeField(field);
    initializeCachedFields();
  }

  public void write(Kryo kryo, Output output, T object) {
    if (object == null) {
      output.writeByte(NULL);
      return;
    }
    int pop = pushTypeVariables();
    CachedField[] fields = cachedFields.fields;
    output.writeVarInt(typeVersion + 1, true);
    for (int i = 0, n = fields.length; i < n; i++) {
      if (TRACE) {
        log("Write", fields[i], output.position());
      }
      fields[i].write(output, object);
    }
    popTypeVariables(pop);
  }

  public T read(Kryo kryo, Input input, Class<? extends T> type) {
    int version = input.readVarInt(true);
    if (version == NULL) {
      return null;
    }
    version--;
    if (!config.compatible && version != typeVersion) {
      throw new KryoException("Version is not compatible: " + version + " != " + typeVersion);
    }
    int pop = pushTypeVariables();
    T object = null;
    final boolean isRecord = type.isRecord();
    if (!isRecord) {
      object = create(kryo, input, type);
      kryo.reference(object);
    }
    CachedField[] fields = cachedFields.fields;
    Object[] values = null;
    for (int i = 0, n = fields.length; i < n; i++) {
      if (fieldVersion[i] > version) {
        if (DEBUG) {
          debug("Skip field: " + fields[i].field.getName());
        }
        continue;
      }
      if (TRACE) {
        log("Read", fields[i], input.position());
      }
      final CachedField field = fields[i];
      if (object != null) {
        field.read(input, object);
      } else {
        if (values == null) {
          values = new Object[fields.length];
        }
        values[field.index] = field.read(input);
      }
    }
    if (isRecord) {
      object = invokeCanonicalConstructor(type, fields, values);
    }
    popTypeVariables(pop);
    return object;
  }

  public VersionFieldSerializerConfig getVersionFieldSerializerConfig() {
    return config;
  }

  @Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.FIELD }) public @interface Since {
    int value() default 0;
  }

  public static class VersionFieldSerializerConfig extends FieldSerializerConfig {
    boolean compatible = true;

    public VersionFieldSerializerConfig clone() {
      return (VersionFieldSerializerConfig) super.clone();
    }

    /** When false, an exception is thrown when reading an object with a different version. The version of an object is the
		 * maximum version of any field. Default is true. */
    public void setCompatible(boolean compatible) {
      this.compatible = compatible;
      if (TRACE) {
        trace("kryo", "VersionFieldSerializerConfig setCompatible: " + compatible);
      }
    }

    public boolean getCompatible() {
      return compatible;
    }
  }
}