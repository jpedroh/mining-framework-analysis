package com.lambdaworks.redis.protocol;
import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.max;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Map;
import com.lambdaworks.redis.codec.RedisCodec;

/**
 * Redis command argument encoder.
 * 
 * @param <K> Key type.
 * @param <V> Value type.
 * @author Will Glozer
 */
public class CommandArgs<K extends java.lang.Object, V extends java.lang.Object> {
  private static final byte[] CRLF = "\r\n".getBytes(LettuceCharsets.ASCII);

  private final RedisCodec<K, V> codec;

  private ByteBuffer buffer;

  private int count;

  private 
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/left.java
  K
=======
  ByteBuffer
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/right.java
   
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/left.java
  firstKey
=======
  firstEncodedKey
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/right.java
  ;

  private Long 
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/left.java
  keywords = new ArrayList<ProtocolKeyword>(8)
=======
  firstInteger
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/right.java
  ;


<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/left.java
  private byte[] encodedFirstKey;
=======
  private String firstString;
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/right.java


  public CommandArgs(RedisCodec<K, V> codec) {
    checkArgument(codec != null, "RedisCodec must not be null");
    this.codec = codec;
    this.buffer = ByteBuffer.allocate(32);
  }

  ByteBuffer buffer() {
    buffer.flip();
    return buffer;
  }

  public int count() {
    return count;
  }

  public CommandArgs<K, V> addKey(K key) {
    if (firstKey == null) {
      firstKey = key;
    }
    byte[] b = codec.encodeKey(key);
    if (
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/left.java
    encodedFirstKey
=======
    firstEncodedKey
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/right.java
     == null) {

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/left.java
      encodedFirstKey = b
=======
      firstEncodedKey = codec.encodeKey(key)
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/right.java
      ;
      return write(firstEncodedKey.duplicate());
    }
    return write(b);
  }

  public CommandArgs<K, V> addKeys(Iterable<K> keys) {
    for (K key : keys) {
      addKey(key);
    }
    return this;
  }

  public CommandArgs<K, V> addKeys(K... keys) {
    for (K key : keys) {
      addKey(key);
    }
    return this;
  }

  public CommandArgs<K, V> addValue(V value) {
    return write(codec.encodeValue(value));
  }

  public CommandArgs<K, V> addValues(V... values) {
    for (V value : values) {
      addValue(value);
    }
    return this;
  }

  public CommandArgs<K, V> add(Map<K, V> map) {
    if (map.size() > 2) {
      realloc(buffer.capacity() + 16 * map.size());
    }
    for (Map.Entry<K, V> entry : map.entrySet()) {
      if (firstEncodedKey == null) {
        firstEncodedKey = codec.encodeKey(entry.getKey());
        write(firstEncodedKey.duplicate());
      } else {
        write(codec.encodeKey(entry.getKey()));
      }
      write(codec.encodeValue(entry.getValue()));
    }
    return this;
  }

  public CommandArgs<K, V> add(String s) {
    if (firstString == null) {
      firstString = s;
    }
    return write(s);
  }

  public CommandArgs<K, V> add(long n) {
    if (firstInteger == null) {
      firstInteger = n;
    }
    return write(Long.toString(n));
  }

  public CommandArgs<K, V> add(double n) {
    return write(Double.toString(n));
  }

  public CommandArgs<K, V> add(byte[] value) {
    return write(value);
  }

  public CommandArgs<K, V> add(CommandKeyword keyword) {
    return write(keyword.bytes);
  }

  public CommandArgs<K, V> add(CommandType type) {
    return write(type.bytes);
  }

  public CommandArgs<K, V> add(ProtocolKeyword keyword) {
    return write(keyword.getBytes());
  }

  private CommandArgs<K, V> write(ByteBuffer arg) {
    buffer.mark();
    if (buffer.remaining() < arg.remaining()) {
      int estimate = buffer.remaining() + arg.remaining() + 10;
      realloc(max(buffer.capacity() * 2, estimate));
    }
    while (true) {
      try {
        ByteBuffer toWrite = arg.duplicate();
        buffer.put((byte) '$');
        write(toWrite.remaining());
        buffer.put(CRLF);
        buffer.put(toWrite);
        buffer.put(CRLF);
        break;
      } catch (BufferOverflowException e) {
        buffer.reset();
        realloc(buffer.capacity() * 2);
      }
    }
    count++;
    return this;
  }

  private CommandArgs<K, V> write(byte[] arg) {
    buffer.mark();
    if (buffer.remaining() < arg.length) {
      int estimate = buffer.remaining() + arg.length + 10;
      realloc(max(buffer.capacity() * 2, estimate));
    }
    while (true) {
      try {
        buffer.put((byte) '$');
        write(arg.length);
        buffer.put(CRLF);
        buffer.put(arg);
        buffer.put(CRLF);
        break;
      } catch (BufferOverflowException e) {
        buffer.reset();
        realloc(buffer.capacity() * 2);
      }
    }
    count++;
    return this;
  }

  private CommandArgs<K, V> write(String arg) {
    int length = arg.length();
    buffer.mark();
    if (buffer.remaining() < length) {
      int estimate = buffer.remaining() + length + 10;
      realloc(max(buffer.capacity() * 2, estimate));
    }
    while (true) {
      try {
        buffer.put((byte) '$');
        write(length);
        buffer.put(CRLF);
        for (int i = 0; i < length; i++) {
          buffer.put((byte) arg.charAt(i));
        }
        buffer.put(CRLF);
        break;
      } catch (BufferOverflowException e) {
        buffer.reset();
        realloc(buffer.capacity() * 2);
      }
    }
    count++;
    return this;
  }

  private void write(int value) {
    if (value < 10) {
      buffer.put((byte) ('0' + value));
      return;
    }
    String asString = Integer.toString(value);
    for (int i = 0; i < asString.length(); i++) {
      buffer.put((byte) asString.charAt(i));
    }
  }

  private void realloc(int size) {
    ByteBuffer newBuffer = ByteBuffer.allocate(size);
    this.buffer.flip();
    newBuffer.put(this.buffer);
    newBuffer.mark();
    this.buffer = newBuffer;
  }


<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/left.java
  public byte[] getEncodedKey() {
    return encodedFirstKey;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  public ByteBuffer getFirstEncodedKey() {
    if (firstEncodedKey != null) {
      return firstEncodedKey.duplicate();
    }
    return null;
  }

  @Override public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName());

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandArgs.java/left.java
    sb.append(" [firstKey=").append(firstKey);
=======
>>>>>>> Unknown file: This is a bug in JDime.

    sb.append(" [buffer=").append(new String(buffer.array()));
    sb.append(']');
    return sb.toString();
  }

  public Long getFirstInteger() {
    return firstInteger;
  }

  public String getFirstString() {
    return firstString;
  }
}