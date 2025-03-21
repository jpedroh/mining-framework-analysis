package com.datastax.driver.core;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.datastax.driver.core.policies.DelegatingLoadBalancingPolicy;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.Policies;
import com.datastax.driver.core.policies.ReconnectionPolicy;

public class ControlConnectionTest {
  @Test(groups = "short") public void should_prevent_simultaneous_reconnection_attempts() throws InterruptedException {
    CCMBridge ccm = null;
    Cluster cluster = null;
    QueryPlanCountingPolicy loadBalancingPolicy = new QueryPlanCountingPolicy(Policies.defaultLoadBalancingPolicy());
    AtomicInteger reconnectionAttempts = loadBalancingPolicy.counter;
    ReconnectionPolicy reconnectionPolicy = new ReconnectionPolicy() {
      @Override public ReconnectionSchedule newSchedule() {
        return new ReconnectionSchedule() {
          @Override public long nextDelayMs() {
            return 60 * 1000;
          }
        };
      }
    };
    try {
      ccm = CCMBridge.create("test", 2);
      cluster = Cluster.builder().addContactPoint(CCMBridge.ipOfNode(1)).withReconnectionPolicy(reconnectionPolicy).withLoadBalancingPolicy(loadBalancingPolicy).build();
      cluster.init();
      ccm.stop(1);
      TimeUnit.SECONDS.sleep(1);
      assertThat(reconnectionAttempts.get()).isEqualTo(1);
      ccm.stop(2);
      TimeUnit.SECONDS.sleep(1);
      assertThat(reconnectionAttempts.get()).isEqualTo(2);
    }  finally {
      if (cluster != null) {
        cluster.close();
      }
      if (ccm != null) {
        ccm.remove();
      }
    }
  }

  /**
     * Test for JAVA-509: UDT definitions were not properly parsed when using the default protocol version.
     *
     * This did not appear with other tests because the UDT needs to already exist when the driver initializes.
     * Therefore we use two different driver instances in this test.
     */
  @Test(groups = "short") public void should_parse_UDT_definitions_when_using_default_protocol_version() {
    TestUtils.versionCheck(2.1, 0, "This will only work with C* 2.1.0");
    CCMBridge ccm = null;
    Cluster cluster = null;
    try {
      ccm = CCMBridge.create("test", 1);
      cluster = Cluster.builder().addContactPoint(CCMBridge.ipOfNode(1)).build();
      Session session = cluster.connect();
      session.execute("create keyspace ks WITH replication = {\'class\': \'SimpleStrategy\', \'replication_factor\': 1}");
      session.execute("create type ks.foo (i int)");
      cluster.close();
      cluster = Cluster.builder().addContactPoint(CCMBridge.ipOfNode(1)).build();
      UserType fooType = cluster.getMetadata().getKeyspace("ks").getUserType("foo");
      assertThat(fooType.getFieldNames()).containsExactly("i");
    }  finally {
      if (cluster != null) {
        cluster.close();
      }
      if (ccm != null) {
        ccm.remove();
      }
    }
  }

  /**
     * Ensures that if the host that the Control Connection is connected to is removed/decommissioned that the
     * Control Connection is reestablished to another host.
     *
     * @since 2.0.9
     * @jira_ticket JAVA-597
     * @expected_result Control Connection is reestablished to another host.
     * @test_category control_connection
     */
  @Test(groups = "long") public void should_reestablish_if_control_node_decommissioned() throws InterruptedException {
    CCMBridge ccm = null;
    Cluster cluster = null;
    try {
      ccm = CCMBridge.create("test", 3);
      cluster = Cluster.builder().addContactPoint(CCMBridge.ipOfNode(1)).build();
      cluster.init();
      String controlHost = cluster.manager.controlConnection.connectedHost().getAddress().getHostAddress();
      assertThat(controlHost).isEqualTo(CCMBridge.ipOfNode(1));
      ccm.decommissionNode(1);
      Host newHost = cluster.manager.controlConnection.connectedHost();
      assertThat(newHost).isNotNull();
      assertThat(newHost.getAddress().getHostAddress()).isNotEqualTo(controlHost);
    }  finally {
      if (cluster != null) {
        cluster.close();
      }
      if (ccm != null) {
        ccm.remove();
      }
    }
  }

  static class QueryPlanCountingPolicy extends DelegatingLoadBalancingPolicy {
    final AtomicInteger counter = new AtomicInteger();

    public QueryPlanCountingPolicy(LoadBalancingPolicy delegate) {
      super(delegate);
    }

    public Iterator<Host> newQueryPlan(String loggedKeyspace, Statement statement) {
      counter.incrementAndGet();
      return super.newQueryPlan(loggedKeyspace, statement);
    }
  }
}