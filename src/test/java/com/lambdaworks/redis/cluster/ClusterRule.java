package com.lambdaworks.redis.cluster;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/left.java
import java.util.concurrent.TimeUnit;
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/base.java
=======
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/right.java

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/left.java
import com.lambdaworks.redis.RedisAsyncConnection;
import com.lambdaworks.redis.RedisAsyncConnectionImpl;
import com.lambdaworks.redis.TestSettings;
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/base.java
import com.lambdaworks.redis.RedisAsyncConnection;
import com.lambdaworks.redis.RedisAsyncConnectionImpl;
=======
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.cluster.api.async.RedisClusterAsyncCommands;
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/right.java
import com.lambdaworks.redis.cluster.models.partitions.ClusterPartitionParser;
import com.lambdaworks.redis.cluster.models.partitions.Partitions;
import com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class ClusterRule implements TestRule {

    private RedisClusterClient clusterClient;
    private int[] ports;
    private Map<Integer, RedisAsyncCommands<String, String>> connectionCache = Maps.newHashMap();

    public ClusterRule(RedisClusterClient clusterClient, int... ports) {
        this.clusterClient = clusterClient;
        this.ports = ports;

        for (int port : ports) {
            RedisAsyncCommands<String, String> connection = clusterClient.connectToNode(
                    new InetSocketAddress("localhost", port)).async();
            connectionCache.put(port, connection);
        }
    }

    @Override
    public Statement apply(final Statement base, Description description) {

        final Statement beforeCluster = new Statement() {
            @Override
            public void evaluate() throws Throwable {
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/left.java
                List<Future<?>> futures = Lists.newArrayList();

                for (RedisAsyncConnection<?, ?> connection : connectionCache.values()) {
                    futures.add(connection.flushall());
                }

                await(futures);
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/base.java
                List<Future> futures = Lists.newArrayList();

                for (RedisAsyncConnection<?, ?> connection : connectionCache.values()) {
                    futures.add(connection.flushall());
                }

                for (Future future : futures) {
                    future.get();
                }
=======
                flushdb();
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/right.java
            }
        };

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                beforeCluster.evaluate();

                base.evaluate();

            }
        };
    }

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/left.java
    private void await(List<Future<?>> futures) throws InterruptedException, java.util.concurrent.ExecutionException,
            java.util.concurrent.TimeoutException {
        for (Future<?> future : futures) {
            future.get(10, TimeUnit.SECONDS);
        }
    }

||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/base.java
=======
    /**
     * 
     * @return true if the cluster state is {@code ok} and there are no failing nodes
     */
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/right.java
    public boolean isStable() {

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/left.java
        for (int port : ports) {
            RedisAsyncConnectionImpl<String, String> connection = clusterClient.connectAsyncImpl(new InetSocketAddress(
                    TestSettings.host(), port));
            try {
                String info = connection.clusterInfo().get();
                if (info != null && info.contains("cluster_state:ok")) {
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/base.java
        RedisAsyncConnectionImpl<String, String> connection = clusterClient.connectAsyncImpl(new InetSocketAddress("localhost",
                ports[0]));
        try {
            String info = connection.clusterInfo().get();
            if (info != null && info.contains("cluster_state:ok")) {
=======
        for (RedisAsyncCommands<String, String> commands : connectionCache.values()) {
            try {
                RedisCommands<String, String> sync = commands.getStatefulConnection().sync();
                String info = sync.clusterInfo();
                if (info != null && info.contains("cluster_state:ok")) {
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/right.java

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/left.java
                    String s = connection.clusterNodes().get();
                    Partitions parse = ClusterPartitionParser.parse(s);
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/base.java
                String s = connection.clusterNodes().get();
                Partitions parse = ClusterPartitionParser.parse(s);
=======
                    String s = sync.clusterNodes();
                    Partitions parse = ClusterPartitionParser.parse(s);
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/right.java

                    for (RedisClusterNode redisClusterNode : parse) {
                        if (redisClusterNode.getFlags().contains(RedisClusterNode.NodeFlag.FAIL)
                                || redisClusterNode.getFlags().contains(RedisClusterNode.NodeFlag.EVENTUAL_FAIL)
                                || redisClusterNode.getFlags().contains(RedisClusterNode.NodeFlag.HANDSHAKE)) {
                            return false;
                        }
                    }
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/left.java
                } else {
                    return false;
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/base.java
=======

                } else {
                    return false;
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/right.java
                }
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/left.java
            } catch (Exception e) {
                // nothing to do
            } finally {
                connection.close();
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/base.java

                return true;

=======
            } catch (Exception e) {
                // nothing to do
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/right.java
            }
        }

        return true;
    }

    /**
     * Flush data on all nodes, ignore failures.
     */
    public void flushdb() {
        onAllConnections(c -> c.flushdb(), true);
    }

    /**
     * Cluster reset on all nodes.
     */
    public void clusterReset() {
        onAllConnections(c -> c.clusterReset(true));
        onAllConnections(RedisClusterAsyncCommands::clusterFlushslots);
    }

    /**
     * Meet on all nodes.
     * 
     * @param host
     * @param port
     */
    public void meet(String host, int port) {
        onAllConnections(c -> c.clusterMeet(host, port));
    }

    public RedisClusterClient getClusterClient() {
        return clusterClient;
    }

    @SuppressWarnings("rawtypes")
    private <T> void onAllConnections(Function<RedisClusterAsyncCommands<?, ?>, Future<T>> function) {
        onAllConnections(function, false);
    }

    @SuppressWarnings("rawtypes")
    private <T> void onAllConnections(Function<RedisClusterAsyncCommands<?, ?>, Future<T>> function,
            boolean ignoreExecutionException) {

        List<Future<?>> futures = Lists.newArrayList();
        for (RedisClusterAsyncCommands<?, ?> connection : connectionCache.values()) {
            futures.add(function.apply(connection));
        }

        try {
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/left.java
            for (RedisAsyncConnection<?, ?> connection : connectionCache.values()) {
                connection.flushdb().get(10, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/base.java
            for (RedisAsyncConnection<?, ?> connection : connectionCache.values()) {
                connection.flushdb().get();
            }
        } catch (Exception e) {
=======
            await((List) futures, ignoreExecutionException);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/right.java
            throw new IllegalStateException(e);
        }
    }


<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/left.java
            for (RedisAsyncConnectionImpl<?, ?> connection : connectionCache.values()) {
                connection.clusterReset(false).get(10, TimeUnit.SECONDS);
                connection.clusterReset(true).get(10, TimeUnit.SECONDS);
                connection.clusterFlushslots().get(10, TimeUnit.SECONDS);
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/base.java
            for (RedisAsyncConnectionImpl<?, ?> connection : connectionCache.values()) {
                connection.clusterReset(false).get();
                connection.clusterReset(true).get();
                connection.clusterFlushslots().get();
=======
    private void await(List<Future<?>> futures, boolean ignoreExecutionException) throws InterruptedException,
            java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException {
        for (Future<?> future : futures) {
            try {
                future.get(10, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                if (!ignoreExecutionException) {
                    throw e;
                }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/ClusterRule.java/right.java
            }
        }
    }

    public void meet(String host, int port) {

        List<Future<?>> futures = Lists.newArrayList();
        for (RedisAsyncConnectionImpl<?, ?> redisAsyncConnection : connectionCache.values()) {
            futures.add(redisAsyncConnection.clusterMeet(host, port));
        }

        for (Future<?> future : futures) {
            try {
                future.get(10, TimeUnit.SECONDS);
            } catch (Exception ignore) {
            }
        }

    }

    public RedisClusterClient getClusterClient() {
        return clusterClient;
    }
}
