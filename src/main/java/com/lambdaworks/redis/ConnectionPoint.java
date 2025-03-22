package com.lambdaworks.redis;

/**
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/ConnectionPoint.java/left.java
 * Interface for a connection point described with a host and port or socket.
 * 
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/ConnectionPoint.java/base.java
=======
 * Interface for a connection point described with a host and port or socket.
 *
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/ConnectionPoint.java/right.java
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public interface ConnectionPoint {

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/ConnectionPoint.java/left.java
    /**
     * Returns the host that should represent the hostname or IPv4/IPv6 literal.
     * 
     * @return the hostname/IP address
     */
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/ConnectionPoint.java/base.java
=======
    /**
     * Returns the host that should represent the hostname or IPv4/IPv6 literal.
     *
     * @return the hostname/IP address
     */
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/ConnectionPoint.java/right.java

    String getHost();

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/ConnectionPoint.java/left.java
    /**
     * Get the current port number.
     * 
     * @return the port number
     */
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/ConnectionPoint.java/base.java
=======
    /**
     * Get the current port number.
     *
     * @return the port number
     */
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/ConnectionPoint.java/right.java

    int getPort();

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/ConnectionPoint.java/left.java
    /**
     * Get the socket path.
     * 
     * @return path to a Unix Domain Socket
     */
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/ConnectionPoint.java/base.java
=======
    /**
     * Get the socket path.
     *
     * @return path to a Unix Domain Socket
     */
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/ConnectionPoint.java/right.java

    String getSocket();
}
