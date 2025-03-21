package com.illposed.osc.utility;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Collection;

/**
 * OSCJavaToByteArrayConverter is a helper class that translates
 * from Java types to their byte stream representations according to
 * the OSC spec.
 *
 * The implementation is based on
 * <a href=" http://www.emergent.de/">Markus Gaelli</a> and
 * Iannis Zannos' OSC implementation in Squeak.
 *
 * This version includes bug fixes and improvements from
 * Martin Kaltenbrunner and Alex Potsides.
 *
 * @author Chandrasekhar Ramakrishnan
 * @author Martin Kaltenbrunner
 * @author Alex Potsides
 */
public class OSCJavaToByteArrayConverter {
  private ByteArrayOutputStream stream = new ByteArrayOutputStream();

  private byte[] intBytes = new byte[4];

  private byte[] longintBytes = new byte[8];

  public OSCJavaToByteArrayConverter() {
  }

  /**
	 * Line up the Big end of the bytes to a 4 byte boundary.
	 * @return byte[]
	 * @param bytes byte[]
	 */
  private byte[] alignBigEndToFourByteBoundry(byte[] bytes) {
    int mod = bytes.length % 4;
    if (mod == 0) {
      return bytes;
    }
    int pad = 4 - mod;
    byte[] newBytes = new byte[pad + bytes.length];
    System.arraycopy(bytes, 0, newBytes, pad, bytes.length);
    return newBytes;
  }

  /**
	 * Pad the stream to have a size divisible by 4.
	 */
  public void appendNullCharToAlignStream() {
    int mod = stream.size() % 4;
    int pad = 4 - mod;
    for (int i = 0; i < pad; i++) {
      stream.write(0);
    }
  }

  /**
	 * Convert the contents of the output stream to a byte array.
	 * @return the byte array containing the byte stream
	 */
  public byte[] toByteArray() {
    return stream.toByteArray();
  }

  /**
	 * Write bytes into the byte stream.
	 * @param bytes  bytes to be written
	 */
  public void write(byte[] bytes) {
    writeUnderHandler(bytes);
  }

  /**
	 * Write an integer into the byte stream.
	 * @param i the integer to be written
	 */
  public void write(int i) {
    writeInteger32ToByteArray(i);
  }

  /**
	 * Write a float into the byte stream.
	 * @param f floating point number to be written
	 */
  public void write(Float f) {
    writeInteger32ToByteArray(Float.floatToIntBits(f.floatValue()));
  }

  /**
	 * @param i the integer to be written
	 */
  public void write(Integer i) {
    writeInteger32ToByteArray(i.intValue());
  }

  /**
	 * @param i the integer to be written
	 */
  public void write(BigInteger i) {
    writeInteger64ToByteArray(i.longValue());
  }

  /**
	 * Write a string into the byte stream.
	 * @param aString the string to be written
	 */
  public void write(String aString) {
    byte[] stringBytes = aString.getBytes();
    int mod = aString.length() % 4;
    int pad = 4 - mod;
    byte[] newBytes = new byte[pad + stringBytes.length];
    System.arraycopy(stringBytes, 0, newBytes, 0, stringBytes.length);
    try {
      stream.write(newBytes);
    } catch (IOException e) {
      throw new RuntimeException("You\'re screwed:" + " IOException writing to a ByteArrayOutputStream", e);
    }
  }

  /**
	 * Write a char into the byte stream.
	 * @param c the character to be written
	 */
  public void write(char c) {
    stream.write(c);
  }

  /**
	 * Write an object into the byte stream.
	 * @param anObject one of Float, String, Integer, BigInteger, or array of
	 *   these.
	 */
  public void write(Object anObject) {
    if (null == anObject) {
    } else {
      if (anObject instanceof Object[]) {
        Object[] theArray = (Object[]) anObject;
        for (int i = 0; i < theArray.length; ++i) {
          write(theArray[i]);
        }
      } else {
        if (anObject instanceof Float) {
          write((Float) anObject);
        } else {
          if (anObject instanceof String) {
            write((String) anObject);
          } else {
            if (anObject instanceof Integer) {
              write((Integer) anObject);
            } else {
              if (anObject instanceof BigInteger) {
                write((BigInteger) anObject);
              }
            }
          }
        }
      }
    }

<<<<<<< /usr/src/app/output/hoijui/javaosc/922809cde5aeaaa89a94a8aa7b4b3e03b3f993e5/modules/core/src/main/java/com/illposed/osc/utility/OSCJavaToByteArrayConverter.java/left.java
    if (anObject instanceof Collection) {
      Collection<Object> theArray = (Collection<Object>) anObject;
      for (Object entry : theArray) {
        write(entry);
      }
      return;
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.
  }

  /**
	 * Write the type tag for the type represented by the class
	 * @param c Class of a Java object in the arguments
	 */
  public void writeType(Class c) {
    if (Integer.class.equals(c)) {
      stream.write('i');
    } else {
      if (java.math.BigInteger.class.equals(c)) {
        stream.write('h');
      } else {
        if (Float.class.equals(c)) {
          stream.write('f');
        } else {
          if (Double.class.equals(c)) {
            stream.write('d');
          } else {
            if (String.class.equals(c)) {
              stream.write('s');
            } else {
              if (Character.class.equals(c)) {
                stream.write('c');
              }
            }
          }
        }
      }
    }
  }

  /**
	 * Write the types for an array element in the arguments.
	 * @param array array of base Objects
	 */
  public void writeTypesArray(Collection<Object> array) {
    for (Object element : array) {
      if (element == null) {
        continue;
      }
      if (Boolean.TRUE.equals(element)) {
        stream.write('T');
        continue;
      }
      if (Boolean.FALSE.equals(element)) {
        stream.write('F');
        continue;
      }
      writeType(element.getClass());
    }
  }

  /**
	 * Write types for the arguments.
	 * @param types  the arguments to an OSCMessage
	 */
  public void writeTypes(Collection<Object> types) {
    for (Object type : types) {
      if (null == type) {
        continue;
      }
      if (type instanceof Collection) {
        stream.write('[');
        writeTypesArray((Collection<Object>) type);
        stream.write(']');
        continue;
      }
      if (Boolean.TRUE.equals(type)) {
        stream.write('T');
        continue;
      }
      if (Boolean.FALSE.equals(type)) {
        stream.write('F');
        continue;
      }
      writeType(type.getClass());
    }
    appendNullCharToAlignStream();
  }

  /**
	 * Write bytes to the stream, catching IOExceptions and converting them to
	 * RuntimeExceptions.
	 * @param bytes byte[]
	 */
  private void writeUnderHandler(byte[] bytes) {
    try {
      stream.write(alignBigEndToFourByteBoundry(bytes));
    } catch (IOException e) {
      throw new RuntimeException("You\'re screwed:" + " IOException writing to a ByteArrayOutputStream");
    }
  }

  /**
	 * Write a 32 bit integer to the byte array without allocating memory.
	 * @param value a 32 bit integer.
	 */
  private void writeInteger32ToByteArray(int value) {
    intBytes[3] = (byte) value;
    value >>>= 8;
    intBytes[2] = (byte) value;
    value >>>= 8;
    intBytes[1] = (byte) value;
    value >>>= 8;
    intBytes[0] = (byte) value;
    try {
      stream.write(intBytes);
    } catch (IOException ex) {
      throw new RuntimeException("You\'re screwed:" + " IOException writing to a ByteArrayOutputStream", ex);
    }
  }

  /**
	 * Write a 64 bit integer to the byte array without allocating memory.
	 * @param value a 64 bit integer.
	 */
  private void writeInteger64ToByteArray(long value) {
    longintBytes[7] = (byte) value;
    value >>>= 8;
    longintBytes[6] = (byte) value;
    value >>>= 8;
    longintBytes[5] = (byte) value;
    value >>>= 8;
    longintBytes[4] = (byte) value;
    value >>>= 8;
    longintBytes[3] = (byte) value;
    value >>>= 8;
    longintBytes[2] = (byte) value;
    value >>>= 8;
    longintBytes[1] = (byte) value;
    value >>>= 8;
    longintBytes[0] = (byte) value;
    try {
      stream.write(longintBytes);
    } catch (IOException ex) {
      throw new RuntimeException("You\'re screwed:" + " IOException writing to a ByteArrayOutputStream", ex);
    }
  }
}