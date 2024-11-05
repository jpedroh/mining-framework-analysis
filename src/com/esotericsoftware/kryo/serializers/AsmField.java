package com.esotericsoftware.kryo.serializers;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer.CachedField;
import com.esotericsoftware.kryo.util.DefaultGenericHandler.GenericType;
import com.esotericsoftware.reflectasm.FieldAccess;
import java.lang.reflect.Field;

class AsmField extends ReflectField {
  public AsmField(Field field, FieldSerializer serializer, GenericType genericType) {
    super(field, serializer, genericType);
  }

  public Object get(Object object) throws IllegalAccessException {
    return access.get(object, accessIndex);
  }

  public void set(Object object, Object value) throws IllegalAccessException {
    access.set(object, accessIndex, value);
  }

  public void copy(Object original, Object copy) {
    try {
      access.set(copy, accessIndex, fieldSerializer.kryo.copy(access.get(original, accessIndex)));
    } catch (KryoException ex) {
      ex.addTrace(this + " (" + fieldSerializer.type.getName() + ")");
      throw ex;
    } catch (Throwable t) {
      KryoException ex = new KryoException(t);
      ex.addTrace(this + " (" + fieldSerializer.type.getName() + ")");
      throw ex;
    }
  }

  final static class IntAsmField extends CachedField {
    public IntAsmField(Field field) {
      super(field);
    }

    public void write(Output output, Object object) {
      if (varEncoding) {
        output.writeVarInt(access.getInt(object, accessIndex), false);
      } else {
        output.writeInt(access.getInt(object, accessIndex));
      }
    }

    public void read(Input input, Object object) {
      if (varEncoding) {
        access.setInt(object, accessIndex, input.readVarInt(false));
      } else {
        access.setInt(object, accessIndex, input.readInt());
      }
    }

    public void copy(Object original, Object copy) {
      access.setInt(copy, accessIndex, access.getInt(original, accessIndex));
    }
  }

  final static class FloatAsmField extends CachedField {
    public FloatAsmField(Field field) {
      super(field);
    }

    public void write(Output output, Object object) {
      output.writeFloat(access.getFloat(object, accessIndex));
    }

    public void read(Input input, Object object) {
      access.setFloat(object, accessIndex, input.readFloat());
    }

    public void copy(Object original, Object copy) {
      access.setFloat(copy, accessIndex, access.getFloat(original, accessIndex));
    }
  }

  final static class ShortAsmField extends CachedField {
    public ShortAsmField(Field field) {
      super(field);
    }

    public void write(Output output, Object object) {
      output.writeShort(access.getShort(object, accessIndex));
    }

    public void read(Input input, Object object) {
      access.setShort(object, accessIndex, input.readShort());
    }

    public void copy(Object original, Object copy) {
      access.setShort(copy, accessIndex, access.getShort(original, accessIndex));
    }
  }

  final static class ByteAsmField extends CachedField {
    public ByteAsmField(Field field) {
      super(field);
    }

    public void write(Output output, Object object) {
      output.writeByte(access.getByte(object, accessIndex));
    }

    public void read(Input input, Object object) {
      access.setByte(object, accessIndex, input.readByte());
    }

    public void copy(Object original, Object copy) {
      access.setByte(copy, accessIndex, access.getByte(original, accessIndex));
    }
  }

  final static class BooleanAsmField extends CachedField {
    public BooleanAsmField(Field field) {
      super(field);
    }

    public void write(Output output, Object object) {
      output.writeBoolean(access.getBoolean(object, accessIndex));
    }

    public void read(Input input, Object object) {
      access.setBoolean(object, accessIndex, input.readBoolean());
    }

    public void copy(Object original, Object copy) {
      access.setBoolean(copy, accessIndex, access.getBoolean(original, accessIndex));
    }
  }

  final static class CharAsmField extends CachedField {
    public CharAsmField(Field field) {
      super(field);
    }

    public void write(Output output, Object object) {
      output.writeChar(access.getChar(object, accessIndex));
    }

    public void read(Input input, Object object) {
      access.setChar(object, accessIndex, input.readChar());
    }

    public void copy(Object original, Object copy) {
      access.setChar(copy, accessIndex, access.getChar(original, accessIndex));
    }
  }

  final static class LongAsmField extends CachedField {
    public LongAsmField(Field field) {
      super(field);
    }

    public void write(Output output, Object object) {
      if (varEncoding) {
        output.writeVarLong(access.getLong(object, accessIndex), false);
      } else {
        output.writeLong(access.getLong(object, accessIndex));
      }
    }

    public void read(Input input, Object object) {
      if (varEncoding) {
        access.setLong(object, accessIndex, input.readVarLong(false));
      } else {
        access.setLong(object, accessIndex, input.readLong());
      }
    }

    public void copy(Object original, Object copy) {
      access.setLong(copy, accessIndex, access.getLong(original, accessIndex));
    }
  }

  final static class DoubleAsmField extends CachedField {
    public DoubleAsmField(Field field) {
      super(field);
    }

    public void write(Output output, Object object) {
      output.writeDouble(access.getDouble(object, accessIndex));
    }

    public void read(Input input, Object object) {
      access.setDouble(object, accessIndex, input.readDouble());
    }

    public void copy(Object original, Object copy) {
      access.setDouble(copy, accessIndex, access.getDouble(original, accessIndex));
    }
  }

  final static class StringAsmField extends CachedField {
    public StringAsmField(Field field) {
      super(field);
    }

    public void write(Output output, Object object) {
      output.writeString(access.getString(object, accessIndex));
    }

    public void read(Input input, Object object) {
      access.set(object, accessIndex, input.readString());
    }

    public void copy(Object original, Object copy) {
      access.set(copy, accessIndex, access.getString(original, accessIndex));
    }
  }
}