package com.esotericsoftware.kryo;
import static com.esotericsoftware.minlog.Log.*;
import static org.junit.Assert.*;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.unsafe.UnsafeByteBufferInput;
import com.esotericsoftware.kryo.unsafe.UnsafeByteBufferOutput;
import com.esotericsoftware.kryo.unsafe.UnsafeInput;
import com.esotericsoftware.kryo.unsafe.UnsafeOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.junit.Before;

abstract public class KryoTestCase {
  static private final boolean debug = false;

  protected Kryo kryo;

  protected Output output;

  protected Input input;

  protected Object object1, object2;

  protected boolean supportsCopy;

  static interface BufferFactory {
    public Output createOutput(OutputStream os);

    public Output createOutput(OutputStream os, int size);

    public Output createOutput(int size, int limit);

    public Input createInput(InputStream os, int size);

    public Input createInput(byte[] buffer);
  }

  @Before public void setUp() throws Exception {
    if (debug && WARN) {
      warn("*** DEBUG TEST ***");
    }
    kryo = new Kryo();
  }

  public <T extends java.lang.Object> T roundTrip(int length, T object1) {
    T object2 = roundTripWithBufferFactory(length, object1, new BufferFactory() {
      public Output createOutput(OutputStream os) {
        return new Output(os);
      }

      public Output createOutput(OutputStream os, int size) {
        return new Output(os, size);
      }

      public Output createOutput(int size, int limit) {
        return new Output(size, limit);
      }

      public Input createInput(InputStream os, int size) {
        return new Input(os, size);
      }

      public Input createInput(byte[] buffer) {
        return new Input(buffer);
      }
    });
    if (debug) {
      return object2;
    }
    roundTripWithBufferFactory(length, object1, new BufferFactory() {
      public Output createOutput(OutputStream os) {
        return new ByteBufferOutput(os);
      }

      public Output createOutput(OutputStream os, int size) {
        return new ByteBufferOutput(os, size);
      }

      public Output createOutput(int size, int limit) {
        return new ByteBufferOutput(size, limit);
      }

      public Input createInput(InputStream os, int size) {
        return new ByteBufferInput(os, size);
      }

      public Input createInput(byte[] buffer) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(buffer.length);
        byteBuffer.put(buffer).flip();
        return new ByteBufferInput(byteBuffer);
      }
    });
    roundTripWithBufferFactory(length, object1, new BufferFactory() {
      public Output createOutput(OutputStream os) {
        return new UnsafeOutput(os);
      }

      public Output createOutput(OutputStream os, int size) {
        return new UnsafeOutput(os, size);
      }

      public Output createOutput(int size, int limit) {
        return new UnsafeOutput(size, limit);
      }

      public Input createInput(InputStream os, int size) {
        return new UnsafeInput(os, size);
      }

      public Input createInput(byte[] buffer) {
        return new UnsafeInput(buffer);
      }
    });
    roundTripWithBufferFactory(length, object1, new BufferFactory() {
      public Output createOutput(OutputStream os) {
        return new UnsafeByteBufferOutput(os);
      }

      public Output createOutput(OutputStream os, int size) {
        return new UnsafeByteBufferOutput(os, size);
      }

      public Output createOutput(int size, int limit) {
        return new UnsafeByteBufferOutput(size, limit);
      }

      public Input createInput(InputStream os, int size) {
        return new UnsafeByteBufferInput(os, size);
      }

      public Input createInput(byte[] buffer) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(buffer.length);
        byteBuffer.put(buffer).flip();
        return new UnsafeByteBufferInput(byteBuffer);
      }
    });
    return object2;
  }

  public <T extends java.lang.Object> T roundTripWithBufferFactory(int length, T object1, BufferFactory sf) {
    boolean checkLength = length != Integer.MIN_VALUE;
    this.object1 = object1;
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    output = sf.createOutput(outStream, 4096);
    kryo.writeClassAndObject(output, object1);
    output.flush();
    if (debug) {
      System.out.println();
    }
    input = sf.createInput(new ByteArrayInputStream(outStream.toByteArray()), 4096);
    object2 = kryo.readClassAndObject(input);
    doAssertEquals(object1, object2);
    if (checkLength) {
    }
    if (debug) {
      return (T) object2;
    }
    outStream = new ByteArrayOutputStream();
    output = sf.createOutput(outStream, 10);
    kryo.writeClassAndObject(output, object1);
    output.flush();
    input = sf.createInput(new ByteArrayInputStream(outStream.toByteArray()), 10);
    object2 = kryo.readClassAndObject(input);
    doAssertEquals(object1, object2);
    if (object1 != null) {
      Serializer serializer = kryo.getRegistration(object1.getClass()).getSerializer();
      output.reset();
      outStream.reset();
      kryo.writeObjectOrNull(output, null, serializer);
      output.flush();
      input = sf.createInput(new ByteArrayInputStream(outStream.toByteArray()), 10);
      assertNull(kryo.readObjectOrNull(input, object1.getClass(), serializer));
      input = sf.createInput(new ByteArrayInputStream(outStream.toByteArray()), 10);
      assertNull(kryo.readObjectOrNull(input, object1.getClass()));
    }
    output = sf.createOutput(length * 2, -1);
    kryo.writeClassAndObject(output, object1);
    output.flush();
    input = sf.createInput(output.toBytes());
    object2 = kryo.readClassAndObject(input);
    doAssertEquals(object1, object2);
    if (checkLength) {
      assertEquals("Incorrect length.", length, output.total());
      assertEquals("Incorrect number of bytes read.", length, input.total());
    }
    input.reset();
    if (supportsCopy) {
      T copy = kryo.copy(object1);
      doAssertEquals(object1, copy);
      copy = kryo.copyShallow(object1);
      doAssertEquals(object1, copy);
    }
    return (T) object2;
  }

  protected void doAssertEquals(Object object1, Object object2) {
    assertEquals(arrayToList(object1), arrayToList(object2));
  }

  static public Object arrayToList(Object array) {
    if (array == null || !array.getClass().isArray()) {
      return array;
    }
    ArrayList list = new ArrayList(Array.getLength(array));
    for (int i = 0, n = Array.getLength(array); i < n; i++) {
      list.add(arrayToList(Array.get(array, i)));
    }
    return list;
  }

  static public ArrayList list(Object... items) {
    ArrayList list = new ArrayList();
    for (Object item : items) {
      list.add(item);
    }
    return list;
  }
}