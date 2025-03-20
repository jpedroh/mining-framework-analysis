package org.apache.commons.dbcp2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import org.apache.commons.pool2.KeyedObjectPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TestSuite for BasicDataSource with abandoned connection trace enabled
 */
public class TestAbandonedBasicDataSource extends TestBasicDataSource {
  private StringWriter sw;

  /**
     * Verifies that con.lastUsed has been updated and then resets it to 0
     */
  private void assertAndReset(final DelegatingConnection<?> con) {
    assertTrue(con.getLastUsedInstant().compareTo(Instant.EPOCH) > 0);
    con.setLastUsed(Instant.EPOCH);
  }

  /**
     * Verifies that PreparedStatement executeXxx methods update lastUsed on the parent connection
     */
  private void checkLastUsedPreparedStatement(final PreparedStatement ps, final DelegatingConnection<?> conn) throws Exception {
    ps.execute();
    assertAndReset(conn);
    try (ResultSet rs = ps.executeQuery()) {
      Assertions.assertNotNull(rs);
    }
    assertAndReset(conn);
    ps.executeUpdate();
    assertAndReset(conn);
  }

  /**
     * Verifies that Statement executeXxx methods update lastUsed on the parent connection
     */
  private void checkLastUsedStatement(final Statement st, final DelegatingConnection<?> conn) throws Exception {
    st.execute("");
    assertAndReset(conn);
    st.execute("", new int[] {  });
    assertAndReset(conn);
    st.execute("", 0);
    assertAndReset(conn);
    st.executeBatch();
    assertAndReset(conn);
    st.executeLargeBatch();
    assertAndReset(conn);
    try (ResultSet rs = st.executeQuery("")) {
      Assertions.assertNotNull(rs);
    }
    assertAndReset(conn);
    st.executeUpdate("");
    assertAndReset(conn);
    st.executeUpdate("", new int[] {  });
    assertAndReset(conn);
    st.executeLargeUpdate("", new int[] {  });
    assertAndReset(conn);
    st.executeUpdate("", 0);
    assertAndReset(conn);
    st.executeLargeUpdate("", 0);
    assertAndReset(conn);
    st.executeUpdate("", new String[] {  });
    assertAndReset(conn);
    st.executeLargeUpdate("", new String[] {  });
    assertAndReset(conn);
  }

  private void createStatement(final Connection conn) throws Exception {
    final PreparedStatement ps = conn.prepareStatement("");
    Assertions.assertNotNull(ps);
  }

  @Override @BeforeEach public void setUp() throws Exception {
    super.setUp();
    ds.setLogAbandoned(true);
    ds.setRemoveAbandonedOnBorrow(true);
    ds.setRemoveAbandonedOnMaintenance(true);
    ds.setRemoveAbandonedTimeout(Duration.ofSeconds(10));
    sw = new StringWriter();
    ds.setAbandonedLogWriter(new PrintWriter(sw));
  }

  @Test public void testAbandoned() throws Exception {
    ds.setRemoveAbandonedTimeout(Duration.ZERO);
    ds.setMaxTotal(1);
    for (int i = 0; i < 3; i++) {
      assertNotNull(ds.getConnection());
    }
  }

  @Test public void testAbandonedClose() throws Exception {
    ds.setRemoveAbandonedTimeout(Duration.ZERO);
    ds.setMaxTotal(1);
    ds.setAccessToUnderlyingConnectionAllowed(true);
    try (Connection conn1 = getConnection()) {
      assertNotNull(conn1);
      assertEquals(1, ds.getNumActive());
      try (Connection conn2 = getConnection()) {
        assertNotNull(conn2);
        assertEquals(1, ds.getNumActive());
        assertTrue(((DelegatingConnection<?>) conn1).getInnermostDelegate().isClosed());
        final TesterConnection tCon = (TesterConnection) ((DelegatingConnection<?>) conn1).getInnermostDelegate();
        assertTrue(tCon.isAborted());
      }
      assertEquals(0, ds.getNumActive());
    }
    assertEquals(0, ds.getNumActive());
    final String string = sw.toString();
    assertTrue(string.contains("testAbandonedClose"), string);
  }

  @Test public void testAbandonedCloseWithExceptions() throws Exception {
    ds.setRemoveAbandonedTimeout(Duration.ZERO);
    ds.setMaxTotal(1);
    ds.setAccessToUnderlyingConnectionAllowed(true);
    final Connection conn1 = getConnection();
    assertNotNull(conn1);
    assertEquals(1, ds.getNumActive());
    final Connection conn2 = getConnection();
    assertNotNull(conn2);
    assertEquals(1, ds.getNumActive());
    final TesterConnection tconn1 = (TesterConnection) ((DelegatingConnection<?>) conn1).getInnermostDelegate();
    tconn1.setFailure(new IOException("network error"));
    final TesterConnection tconn2 = (TesterConnection) ((DelegatingConnection<?>) conn2).getInnermostDelegate();
    tconn2.setFailure(new IOException("network error"));
    try {
      conn2.close();
    } catch (final SQLException ex) {
    }
    assertEquals(0, ds.getNumActive());
    try {
      conn1.close();
    } catch (final SQLException ex) {
    }
    assertEquals(0, ds.getNumActive());
    final String string = sw.toString();
    assertTrue(string.contains("testAbandonedCloseWithExceptions"), string);
  }

  /**
     * DBCP-180 - verify that a GC can clean up an unused Statement when it is
     * no longer referenced even when it is tracked via the AbandonedTrace
     * mechanism.
     */
  @Test public void testGarbageCollectorCleanUp01() throws Exception {
    try (DelegatingConnection<?> conn = (DelegatingConnection<?>) ds.getConnection()) {
      Assertions.assertEquals(0, conn.getTrace().size());
      createStatement(conn);
      Assertions.assertEquals(1, conn.getTrace().size());
      System.gc();
      Assertions.assertEquals(0, conn.getTrace().size());
    }
  }

  /**
     * DBCP-180 - things get more interesting with statement pooling.
     */
  @Test public void testGarbageCollectorCleanUp02() throws Exception {
    ds.setPoolPreparedStatements(true);
    ds.setAccessToUnderlyingConnectionAllowed(true);
    final DelegatingConnection<?> conn = (DelegatingConnection<?>) ds.getConnection();
    final PoolableConnection poolableConn = (PoolableConnection) conn.getDelegate();
    final PoolingConnection poolingConn = (PoolingConnection) poolableConn.getDelegate();
    final KeyedObjectPool<PStmtKey, DelegatingPreparedStatement> gkop = poolingConn.getStatementPool();
    Assertions.assertEquals(0, conn.getTrace().size());
    Assertions.assertEquals(0, gkop.getNumActive());
    createStatement(conn);
    Assertions.assertEquals(1, conn.getTrace().size());
    Assertions.assertEquals(1, gkop.getNumActive());
    System.gc();
    int count = 0;
    while (count < 50 && gkop.getNumActive() > 0) {
      Thread.sleep(100);
      count++;
    }
    Assertions.assertEquals(0, gkop.getNumActive());
    Assertions.assertEquals(0, conn.getTrace().size());
  }

  /**
     * Verify that lastUsed property is updated when a connection
     * creates or prepares a statement
     */
  @Test public void testLastUsed() throws Exception {
    ds.setRemoveAbandonedTimeout(Duration.ofSeconds(1));
    ds.setMaxTotal(2);
    try (Connection conn1 = ds.getConnection()) {
      Thread.sleep(500);
      try (Statement s = conn1.createStatement()) {
      }
      Thread.sleep(800);
      final Connection conn2 = ds.getConnection();
      try (Statement s = conn1.createStatement()) {
      }
      conn2.close();
      Thread.sleep(500);
      try (PreparedStatement ps = conn1.prepareStatement("SELECT 1 FROM DUAL")) {
      }
      Thread.sleep(800);
      try (Connection c = ds.getConnection()) {
      }
      try (Statement s = conn1.createStatement()) {
      }
    }
  }

  /**
     * DBCP-343 - verify that using a DelegatingStatement updates
     * the lastUsed on the parent connection
     */
  @Test public void testLastUsedLargePreparedStatementUse() throws Exception {
    ds.setRemoveAbandonedTimeout(Duration.ofSeconds(1));
    ds.setMaxTotal(2);
    try (Connection conn1 = ds.getConnection(); Statement st = conn1.createStatement()) {
      final String querySQL = "SELECT 1 FROM DUAL";
      Thread.sleep(500);
      try (ResultSet rs = st.executeQuery(querySQL)) {
        Assertions.assertNotNull(rs);
      }
      Thread.sleep(800);
      try (Connection conn2 = ds.getConnection()) {
        try (ResultSet rs = st.executeQuery(querySQL)) {
          Assertions.assertNotNull(rs);
        }
      }
      Thread.sleep(500);
      st.executeLargeUpdate("");
      Thread.sleep(800);
      try (Connection c = ds.getConnection()) {
      }
      try (Statement s = conn1.createStatement()) {
      }
    }
  }

  /**
     * Verify that lastUsed property is updated when a connection
     * prepares a callable statement.
     */
  @Test public void testLastUsedPrepareCall() throws Exception {
    ds.setRemoveAbandonedTimeout(Duration.ofSeconds(1));
    ds.setMaxTotal(2);
    try (Connection conn1 = ds.getConnection()) {
      Thread.sleep(500);
      try (CallableStatement cs = conn1.prepareCall("{call home}")) {
      }
      Thread.sleep(800);
      final Connection conn2 = ds.getConnection();
      try (CallableStatement cs = conn1.prepareCall("{call home}")) {
      }
      conn2.close();
      Thread.sleep(500);
      try (CallableStatement cs = conn1.prepareCall("{call home}")) {
      }
      Thread.sleep(800);
      try (Connection c = ds.getConnection()) {
      }
      try (Statement s = conn1.createStatement()) {
      }
    }
  }

  /**
     * DBCP-343 - verify that using a DelegatingStatement updates
     * the lastUsed on the parent connection
     */
  @Test public void testLastUsedPreparedStatementUse() throws Exception {
    ds.setRemoveAbandonedTimeout(Duration.ofSeconds(1));
    ds.setMaxTotal(2);
    try (Connection conn1 = ds.getConnection(); Statement st = conn1.createStatement()) {
      final String querySQL = "SELECT 1 FROM DUAL";
      Thread.sleep(500);
      Assertions.assertNotNull(st.executeQuery(querySQL));
      Thread.sleep(800);
      final Connection conn2 = ds.getConnection();
      Assertions.assertNotNull(st.executeQuery(querySQL));
      conn2.close();
      Thread.sleep(500);
      st.executeUpdate("");
      Thread.sleep(800);
      try (Connection c = ds.getConnection()) {
      }
      try (Statement s = conn1.createStatement()) {
      }
    }
  }

  /**
     * DBCP-343 - verify additional operations reset lastUsed on
     * the parent connection
     */
  @Test public void testLastUsedUpdate() throws Exception {
    try (DelegatingConnection<?> conn = (DelegatingConnection<?>) ds.getConnection(); PreparedStatement ps = conn.prepareStatement(""); CallableStatement cs = conn.prepareCall(""); Statement st = conn.prepareStatement("")) {
      checkLastUsedStatement(ps, conn);
      checkLastUsedPreparedStatement(ps, conn);
      checkLastUsedStatement(cs, conn);
      checkLastUsedPreparedStatement(cs, conn);
      checkLastUsedStatement(st, conn);
    }
  }
}