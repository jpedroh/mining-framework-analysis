package org.crazycake.shiro;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisManager extends BaseRedisManager implements IRedisManager {
  private static final 
<<<<<<< /usr/src/app/output/alexxiyang/shiro-redis/0f0ecea024252dcaad14f1891f49bf96fd8e0e0b/src/main/java/org/crazycake/shiro/RedisManager.java/left.java
  String
=======
  JedisPool
>>>>>>> /usr/src/app/output/alexxiyang/shiro-redis/0f0ecea024252dcaad14f1891f49bf96fd8e0e0b/src/main/java/org/crazycake/shiro/RedisManager.java/right.java
   
<<<<<<< /usr/src/app/output/alexxiyang/shiro-redis/0f0ecea024252dcaad14f1891f49bf96fd8e0e0b/src/main/java/org/crazycake/shiro/RedisManager.java/left.java
  DEFAULT_HOST = "127.0.0.1:6379"
=======
  jedisPool
>>>>>>> /usr/src/app/output/alexxiyang/shiro-redis/0f0ecea024252dcaad14f1891f49bf96fd8e0e0b/src/main/java/org/crazycake/shiro/RedisManager.java/right.java
  ;

  private String host = DEFAULT_HOST;

  @Deprecated private int port = Protocol.DEFAULT_PORT;

  private int timeout = Protocol.DEFAULT_TIMEOUT;

  private String password;

  private int database = Protocol.DEFAULT_DATABASE;

  private void init() {
    synchronized (this) {
      if (jedisPool == null) {
        if (host.contains(":")) {
          String[] hostAndPort = host.split(":");
          jedisPool = new JedisPool(jedisPoolConfig, hostAndPort[0], Integer.parseInt(hostAndPort[1]), timeout, password, database);
        } else {
          jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
        }
      }
    }
  }

  @Override protected Jedis getJedis() {
    if (jedisPool == null) {
      init();
    }
    return jedisPool.getResource();
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getDatabase() {
    return database;
  }

  public void setDatabase(int database) {
    this.database = database;
  }

  public JedisPool getJedisPool() {
    return jedisPool;
  }

  public void setJedisPool(JedisPool jedisPool) {
    this.jedisPool = jedisPool;
  }
}