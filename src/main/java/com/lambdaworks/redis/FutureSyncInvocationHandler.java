package com.lambdaworks.redis;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.AbstractInvocationHandler;
import com.lambdaworks.redis.protocol.Command;
import com.lambdaworks.redis.api.StatefulConnection;
import com.lambdaworks.redis.api.StatefulRedisConnection;

/**
 * Invocation-handler to synchronize API calls which use Futures as backend. This class leverages the need to implement a full
 * sync class which just delegates every request.
 * 
 * @param <K> Key type.
 * @param <V> Value type.
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 3.0
 */
class FutureSyncInvocationHandler<K extends java.lang.Object, V extends java.lang.Object> extends AbstractInvocationHandler {
  private final StatefulConnection<?, ?> connection;

  private final Object asyncApi;

  private LoadingCache<Method, Method> methodCache;

  public FutureSyncInvocationHandler(final StatefulConnection<?, ?> connection, Object asyncApi) {
    this.connection = connection;
    this.asyncApi = asyncApi;
    methodCache = CacheBuilder.newBuilder().build(new CacheLoader<Method, Method>() {
      @Override public Method load(Method key) throws Exception {
        return 
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/FutureSyncInvocationHandler.java/left.java
        connection
=======
        asyncApi
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/FutureSyncInvocationHandler.java/right.java
        .getClass().getMethod(key.getName(), key.getParameterTypes());
      }
    });
  }

  /**
     * 
     * @see com.google.common.reflect.AbstractInvocationHandler#handleInvocation(java.lang.Object, java.lang.reflect.Method,
     *      java.lang.Object[])
     */
  @Override @SuppressWarnings(value = { "unchecked" }) protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      Method targetMethod = methodCache.get(method);
      Object result = targetMethod.invoke(asyncApi, args);
      if (result instanceof RedisFuture) {
        RedisFuture<?> 
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/FutureSyncInvocationHandler.java/left.java
        redisCommand = (RedisCommand<?, ?, ?>) result
=======
        command = (RedisFuture<?>) result
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/FutureSyncInvocationHandler.java/right.java
        ;
        if (!method.getName().equals("exec") && !method.getName().equals("multi")) {
          if (connection instanceof StatefulRedisConnection && ((StatefulRedisConnection) connection).isMulti()) {
            return null;
          }
        }

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/FutureSyncInvocationHandler.java/left.java
        Object awaitedResult = LettuceFutures.awaitOrCancel(redisCommand, timeout, unit);
=======
        LettuceFutures.awaitOrCancel(command, connection.getTimeout(), connection.getTimeoutUnit());
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/FutureSyncInvocationHandler.java/right.java

        if (redisCommand instanceof Command) {
          Command<?, ?, ?> command = (Command<?, ?, ?>) redisCommand;
          if (command.getException() != null) {
            throw new RedisException(command.getException());
          }
        }
        if (redisCommand instanceof Future<?>) {
          if (redisCommand.isDone()) {
            try {
              redisCommand.get();
            } catch (InterruptedException e) {
              throw e;
            } catch (ExecutionException e) {
              throw new RedisException(e.getCause());
            }
          }
        }
        return 
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/FutureSyncInvocationHandler.java/left.java
        awaitedResult
=======
        command.get()
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/FutureSyncInvocationHandler.java/right.java
        ;
      }

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/FutureSyncInvocationHandler.java/left.java
      if (result instanceof RedisClusterAsyncConnection) {
        return AbstractRedisClient.syncHandler((RedisChannelHandler<?, ?>) result, RedisConnection.class, RedisClusterConnection.class);
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.

      return result;
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }
}