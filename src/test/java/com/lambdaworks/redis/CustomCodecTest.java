// Copyright (C) 2011 - Will Glozer.  All rights reserved.

package com.lambdaworks.redis;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.lambdaworks.redis.protocol.SetArgs;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import org.junit.Test;

import com.lambdaworks.redis.codec.ByteArrayCodec;
import com.lambdaworks.redis.codec.CompressionCodec;
import com.lambdaworks.redis.codec.RedisCodec;
import rx.observers.TestSubscriber;

public class CustomCodecTest extends AbstractRedisClientTest {

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/CustomCodecTest.java/left.java
    @Test
    public void testJavaSerializer() throws Exception {
        RedisConnection<String, Object> connection = client.connect(new SerializedObjectCodec());

        List<String> list = list("one", "two");
        connection.set(key, list);

        assertThat(connection.get(key)).isEqualTo(list);
        assertThat(connection.set(key, list)).isEqualTo("OK");
        assertThat(connection.set(key, list, SetArgs.Builder.ex(1))).isEqualTo("OK");

        connection.close();
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/CustomCodecTest.java/base.java
=======
    @Test
    public void testJavaSerializer() throws Exception {
        StatefulRedisConnection<String, Object> redisConnection = client.connect(new SerializedObjectCodec());
        RedisCommands<String, Object> sync = redisConnection.sync();
        List<String> list = list("one", "two");
        sync.set(key, list);

        assertThat(sync.get(key)).isEqualTo(list);
        assertThat(sync.set(key, list)).isEqualTo("OK");
        assertThat(sync.set(key, list, SetArgs.Builder.ex(1))).isEqualTo("OK");

        redisConnection.close();
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/CustomCodecTest.java/right.java

    @Test
    public void testJavaSerializerRx() throws Exception {
        StatefulRedisConnection<String, Object> redisConnection = client.connect(new SerializedObjectCodec());
        List<String> list = list("one", "two");

        TestSubscriber<String> subscriber = TestSubscriber.create();

        redisConnection.reactive().set(key, list, SetArgs.Builder.ex(1)).subscribe(subscriber);
        subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS);
        subscriber.assertCompleted();
        subscriber.assertValue("OK");

        redisConnection.close();
    }

    @Test
    public void testDeflateCompressedJavaSerializer() throws Exception {
        RedisConnection<String, Object> connection = client.connect(CompressionCodec.valueCompressor(
                new SerializedObjectCodec(), CompressionCodec.CompressionType.DEFLATE));
        List<String> list = list("one", "two");
        connection.set(key, list);
        assertThat(connection.get(key)).isEqualTo(list);

        connection.close();
    }

    @Test
    public void testGzipompressedJavaSerializer() throws Exception {
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/CustomCodecTest.java/left.java
        RedisConnection<String, Object> connection = client.connect(CompressionCodec.valueCompressor(
                new SerializedObjectCodec(), CompressionCodec.CompressionType.GZIP));
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/CustomCodecTest.java/base.java
         ;
=======
        RedisCommands<String, Object> connection = client.connect(
                CompressionCodec.valueCompressor(new SerializedObjectCodec(), CompressionCodec.CompressionType.GZIP)).sync();
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/CustomCodecTest.java/right.java
        List<String> list = list("one", "two");
        connection.set(key, list);
        assertThat(connection.get(key)).isEqualTo(list);

        connection.close();
    }

    @Test
    public void testByteCodec() throws Exception {
        RedisConnection<byte[], byte[]> connection = client.connect(new ByteArrayCodec()).sync();
        String value = "üöäü+#";
        connection.set(key.getBytes(), value.getBytes());
        assertThat(connection.get(key.getBytes())).isEqualTo(value.getBytes());

        List<byte[]> keys = connection.keys(key.getBytes());
        assertThat(keys).contains(key.getBytes());
    }

    public class SerializedObjectCodec implements RedisCodec<String, Object> {
        private Charset charset = Charset.forName("UTF-8");

        @Override
        public String decodeKey(ByteBuffer bytes) {
            return charset.decode(bytes).toString();
        }

        @Override
        public Object decodeValue(ByteBuffer bytes) {
            try {
                byte[] array = new byte[bytes.remaining()];
                bytes.get(array);
                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(array));
                return is.readObject();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public ByteBuffer encodeKey(String key) {
            return charset.encode(key);
        }

        @Override
        public ByteBuffer encodeValue(Object value) {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(bytes);
                os.writeObject(value);
                return ByteBuffer.wrap(bytes.toByteArray());
            } catch (IOException e) {
                return null;
            }
        }
    }
}
