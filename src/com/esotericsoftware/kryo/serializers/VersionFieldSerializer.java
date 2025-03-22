package com.esotericsoftware.kryo.serializers;
import static com.esotericsoftware.minlog.Log.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/** Serializes objects using direct field assignment, with versioning backward compatibility. Fields can be added without
 * invalidating previously serialized bytes. Note that removing, renaming or changing the type of a field is not supported. In
 * addition, forward compatibility is not supported.
 * <p>
 * There is a little additional overhead compared to {@link FieldSerializer}. A version varint is written before each object. When
 * deserializing, the input version will be examined to decide what fields are not in the input.
 * @author Tianyi HE <hty0807@gmail.com> */
public class VersionFieldSerializer<T extends java.lang.Object> extends FieldSerializer<T> {
  private int typeVersion = 0;

  private int[] fieldVersion;

  private boolean compatible = true;

  public VersionFieldSerializer(Kryo kryo, Class type) {
    super(kryo, type);
    initializeCachedFields();
  }

  public VersionFieldSerializer(Kryo kryo, Class type, boolean compatible) {
    this(kryo, type);
    this.compatible = compatible;
  }

  @Override protected void initializeCachedFields() {
    CachedField[] fields = getFields();
    fieldVersion = new int[fields.length];
    for (int i = 0, n = fields.length; i < n; i++) {
      Field field = fields[i].getField();
      Since since = field.getAnnotation(Since.class);
      if (since != null) {
        fieldVersion[i] = since.value();
        typeVersion = Math.max(fieldVersion[i], typeVersion);
      } else {
        fieldVersion[i] = 0;
      }
    }
    this.removedFields.clear();
    if (DEBUG) {
      debug("Version for type " + getType().getName() + " is " + typeVersion);
    }
  }

  @Override public void removeField(String fieldName) {
    super.removeField(fieldName);
    initializeCachedFields();
  }

  @Override public void removeField(CachedField field) {
    super.removeField(field);
    initializeCachedFields();
  }

  @Override public void write(Kryo kryo, Output output, T object) {
    CachedField[] fields = getFields();
    output.writeVarInt(typeVersion, true);
    for (int i = 0, n = fields.length; i < n; i++) {
      fields[i].write(output, object);
    }
  }

  @Override public T read(Kryo kryo, Input input, Class<T> type) {
    T object = create(kryo, input, type);
    kryo.reference(object);
    int version = input.readVarInt(true);
    if (!compatible && version != typeVersion) {
      throw new KryoException("Version not compatible: " + version + " <-> " + typeVersion);
    }
    CachedField[] fields = getFields();
    for (int i = 0, n = fields.length; i < n; i++) {
      if (fieldVersion[i] > version) {
        if (DEBUG) {
          debug("Skip field " + fields[i].getField().getName());
        }
        continue;
      }
      fields[i].read(input, object);
    }
    return object;
  }

  @Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.FIELD }) public @interface Since {
    int value() default 0;
  }
}