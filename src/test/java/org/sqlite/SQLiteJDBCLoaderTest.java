package org.sqlite;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import java.nio.file.Path;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SQLiteJDBCLoaderTest {
  private Connection connection = null;

  @BeforeEach public void setUp() throws Exception {
    connection = null;
    connection = DriverManager.getConnection("jdbc:sqlite::memory:");
  }

  @AfterEach public void tearDown() throws Exception {
    if (connection != null) {
      connection.close();
    }
  }

  @Test public void query() {
    assertThatNoException().isThrownBy(() -> {
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);
      statement.executeUpdate("create table person ( id integer, name string)");
      statement.executeUpdate("insert into person values(1, \'leo\')");
      statement.executeUpdate("insert into person values(2, \'yui\')");
      ResultSet rs = statement.executeQuery("select * from person order by id");
      while (rs.next()) {
        rs.getInt(1);
        rs.getString(2);
      }
    });
  }

  @Test public void function() throws SQLException {
    Function.create(connection, "total", new Function() {
      @Override protected void xFunc() throws SQLException {
        int sum = 0;
        for (int i = 0; i < args(); i++) {
          sum += value_int(i);
        }
        result(sum);
      }
    });
    ResultSet rs = connection.createStatement().executeQuery("select total(1, 2, 3, 4, 5)");
    assertThat(rs.next()).isTrue();
    assertThat(rs.getInt(1)).isEqualTo(1 + 2 + 3 + 4 + 5);
  }

  @Test public void version() {
    assertThat(SQLiteJDBCLoader.getVersion()).isNotEqualTo("unknown");
  }

  @Test public void test(@TempDir Path tmpDir) throws Throwable {
    final AtomicInteger completedThreads = new AtomicInteger(0);
    ExecutorService pool = Executors.newFixedThreadPool(32);
    for (int i = 0; i < 32; i++) {
      final String connStr = "jdbc:sqlite:" + tmpDir.resolve("sample-" + i + ".db");
      final int sleepMillis = i;
      pool.execute(() -> {
        try {
          Thread.sleep(sleepMillis * 10);
        } catch (InterruptedException ignored) {
        }
        assertThatNoException().isThrownBy(() -> {
          Connection conn = DriverManager.getConnection(connStr);
          conn.close();
        });
        completedThreads.incrementAndGet();
      });
    }
    pool.shutdown();
    pool.awaitTermination(3, TimeUnit.SECONDS);
    assertThat(completedThreads.get()).isEqualTo(32);
  }
}