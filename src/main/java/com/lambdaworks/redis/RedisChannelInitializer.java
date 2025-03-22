package com.lambdaworks.redis;

import java.util.concurrent.Future;

import io.netty.channel.ChannelHandler;

/**
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/RedisChannelInitializer.java/left.java
 * Channel initializer to set up the transport before a Redis connection can be used. This is part of the internal API.
 * This class is part of the internal API.
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/RedisChannelInitializer.java/base.java
=======
 * Channel initializer to set up the transport before a Redis connection can be used. This is part of the internal API. This
 * class is part of the internal API.
 * 
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/RedisChannelInitializer.java/right.java
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public interface RedisChannelInitializer extends ChannelHandler {

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/RedisChannelInitializer.java/left.java
    /**
     * 
     * @return future to synchronize channel initialization. Returns a new future for every reconnect.
     */
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/RedisChannelInitializer.java/base.java
=======
    /**
     *
     * @return future to synchronize channel initialization. Returns a new future for every reconnect.
     */
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/RedisChannelInitializer.java/right.java

    Future<Boolean> channelInitialized();
}
