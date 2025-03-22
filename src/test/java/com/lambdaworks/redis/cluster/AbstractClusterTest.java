package com.lambdaworks.redis.cluster;
import com.lambdaworks.TestClientResources;
import com.lambdaworks.redis.resource.ClientResources;
import org.junit.AfterClass;
import com.lambdaworks.redis.FastShutdown;
import org.junit.BeforeClass;
import org.junit.Rule;
import com.google.common.collect.ImmutableList;
import com.lambdaworks.redis.AbstractTest;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.TestSettings;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class AbstractClusterTest extends AbstractTest {
  public static final String host = TestSettings.hostAddr();

  public static final int port1 = 7379;

  public static final int SLOT_A = SlotHash.getSlot("a".getBytes());

  public static final int port2 = port1 + 1;

  public static final int SLOT_B = SlotHash.getSlot("b".getBytes());

  public static final int port3 = port1 + 2;

  public static final int port4 = port1 + 3;

  protected static RedisClusterClient clusterClient;


<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/AbstractClusterTest.java/left.java
  protected static ClientResources resources = TestClientResources.create();
=======
  public static final int port5 = port1 + 4;
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/test/java/com/lambdaworks/redis/cluster/AbstractClusterTest.java/right.java


  public static final String KEY_A = "a";

  public static final int port6 = port1 + 5;

  public static final String KEY_B = "b";

  public static final int port7 = port1 + 6;

  @Rule public ClusterRule clusterRule = new ClusterRule(clusterClient, port1, port2, port3, port4);

  @BeforeClass public static void setupClusterClient() throws Exception {
    clusterClient = RedisClusterClient.create(resources, ImmutableList.of(RedisURI.Builder.redis(host, port1).build()));
  }

  @AfterClass public static void shutdownClusterClient() {
    FastShutdown.shutdown(clusterClient);
  }
}