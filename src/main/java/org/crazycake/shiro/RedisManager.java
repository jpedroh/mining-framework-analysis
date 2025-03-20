package org.crazycake.shiro;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

<<<<<<< /usr/src/app/output/alexxiyang/shiro-redis/a23d2bb28bd5bceac5d4a089d950ded31b93dba3/src/main/java/org/crazycake/shiro/RedisManager.java/left.java
public class RedisManager extends BaseRedisManager implements IRedisManager{
||||||| /usr/src/app/output/alexxiyang/shiro-redis/a23d2bb28bd5bceac5d4a089d950ded31b93dba3/src/main/java/org/crazycake/shiro/RedisManager.java/base.java
public class RedisManager {
=======
public class RedisManager implements IRedisManager{
>>>>>>> /usr/src/app/output/alexxiyang/shiro-redis/a23d2bb28bd5bceac5d4a089d950ded31b93dba3/src/main/java/org/crazycake/shiro/RedisManager.java/right.java
	
	private String host = "127.0.0.1";

	private int port = Protocol.DEFAULT_PORT ;
	
	// timeout for jedis try to connect to redis server, not expire time! In milliseconds
	private int timeout = Protocol.DEFAULT_TIMEOUT;
	
	private String password;

	private int database = Protocol.DEFAULT_DATABASE;

	private void init() {
		synchronized (this) {
			if (jedisPool == null) {
				jedisPool = new JedisPool(new JedisPoolConfig(), host, port, timeout, password, database);
			}
		}
	}

	@Override
	protected void checkAndInit() {
		if (jedisPool == null) {
			init();
		}
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
}
