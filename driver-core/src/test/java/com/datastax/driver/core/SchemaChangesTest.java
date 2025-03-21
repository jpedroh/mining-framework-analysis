package com.datastax.driver.core;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.testng.annotations.*;

import com.datastax.driver.core.utils.Bytes;

import static com.datastax.driver.core.Assertions.assertThat;

public class SchemaChangesTest extends CCMBridge.PerClassSingleNodeCluster {

    // Create a second cluster to check that other clients also get notified
    Cluster cluster2;
    // The metadatas of the two clusters should be kept in sync
    List<Metadata> metadatas;
<<<<<<< /usr/src/app/output/datastax/java-driver/6af4247c0b94538f16fbac838eb9d6c0af4098b8/driver-core/src/test/java/com/datastax/driver/core/SchemaChangesTest.java/left.java
    @Override
    protected Collection<String> getTableDefinitions() {
        return Lists.newArrayList();
    }
||||||| /usr/src/app/output/datastax/java-driver/6af4247c0b94538f16fbac838eb9d6c0af4098b8/driver-core/src/test/java/com/datastax/driver/core/SchemaChangesTest.java/base.java
    @Override
    protected Collection<String> getTableDefinitions() {
        return Lists.newArrayList();
    }
=======
    @Override
    protected Collection<String> getTableDefinitions() {
        return Lists.newArrayList("CREATE KEYSPACE \"CaseSensitive\" WITH REPLICATION = { 'class' : 'org.apache.cassandra.locator.SimpleStrategy', 'replication_factor': '1' }");
    }
>>>>>>> /usr/src/app/output/datastax/java-driver/6af4247c0b94538f16fbac838eb9d6c0af4098b8/driver-core/src/test/java/com/datastax/driver/core/SchemaChangesTest.java/right.java
    @BeforeClass(groups = "short")
    public void setup() {
        cluster2 = Cluster.builder().addContactPoint(CCMBridge.ipOfNode(1)).build();
        metadatas = Lists.newArrayList(cluster.getMetadata(), cluster2.getMetadata());
    }
    @Test(groups = "short")
    public void should_notify_of_table_update() {
        session.execute("CREATE TABLE ks.table1(i int primary key)");
        session.execute("ALTER TABLE ks.table1 ADD j int");

        for (Metadata m : metadatas)
            assertThat(m.getKeyspace("ks").getTable("table1").getColumn("j"))
                .isNotNull();
    }
    @Test(groups = "short")
    public void should_notify_of_table_drop() {
        session.execute("CREATE TABLE ks.table1(i int primary key)");
        session.execute("DROP TABLE ks.table1");

        for (Metadata m : metadatas)
            assertThat(m.getKeyspace("ks").getTable("table1"))
                .isNull();
    }
    @Test(groups = "short")
    public void should_notify_of_udt_creation() {
        session.execute("CREATE TYPE ks.type1(i int)");

        for (Metadata m : metadatas)
            assertThat(m.getKeyspace("ks").getUserType("type1"))
                .isNotNull();
    }
    @Test(groups = "short")
    public void should_notify_of_udt_update() {
        session.execute("CREATE TYPE ks.type1(i int)");
        session.execute("ALTER TYPE ks.type1 ADD j int");

        for (Metadata m : metadatas)
            assertThat(m.getKeyspace("ks").getUserType("type1").getFieldType("j"))
                .isNotNull();
    }
    @Test(groups = "short")
    public void should_notify_of_udt_drop() {
        session.execute("CREATE TYPE ks.type1(i int)");
        session.execute("DROP TYPE ks.type1");

        for (Metadata m : metadatas)
            assertThat(m.getKeyspace("ks").getUserType("type1"))
                .isNull();
    }
    @Test(groups = "short")
    public void should_notify_of_keyspace_drop() {
        session.execute("CREATE KEYSPACE ks2 WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
        for (Metadata m : metadatas)
            assertThat(m.getReplicas("ks2", Bytes.fromHexString("0xCAFEBABE")))
                .isNotEmpty();

        session.execute("DROP KEYSPACE ks2");

        for (Metadata m : metadatas) {
            assertThat(m.getKeyspace("ks2"))
                .isNull();
            assertThat(m.getReplicas("ks2", Bytes.fromHexString("0xCAFEBABE")))
                .isEmpty();
        }
    }
<<<<<<< /usr/src/app/output/datastax/java-driver/6af4247c0b94538f16fbac838eb9d6c0af4098b8/driver-core/src/test/java/com/datastax/driver/core/SchemaChangesTest.java/left.java
    @AfterMethod(groups = "short")
    public void cleanup() {
        try {
            session.execute("DROP TABLE IF EXISTS ks.table1");
        } catch(InvalidQueryException ex) {}
        try {
            session.execute("DROP TYPE IF EXISTS ks.type1");
        } catch(InvalidQueryException ex) {}
        try {
            session.execute("DROP KEYSPACE IF EXISTS ks2");
        } catch(InvalidQueryException ex) {}
    }
||||||| /usr/src/app/output/datastax/java-driver/6af4247c0b94538f16fbac838eb9d6c0af4098b8/driver-core/src/test/java/com/datastax/driver/core/SchemaChangesTest.java/base.java
=======
    @AfterMethod(groups = "short")
    public void cleanup() {
        ListenableFuture<List<ResultSet>> f = Futures.successfulAsList(Lists.newArrayList(
            session.executeAsync("DROP TABLE ks.table1"),
            session.executeAsync("DROP TABLE \"CaseSensitive\".table1"),
            session.executeAsync("DROP KEYSPACE ks2"),
            session.executeAsync("DROP KEYSPACE \"CaseSensitive2\"")
        ));
        Futures.getUnchecked(f);
    }
>>>>>>> /usr/src/app/output/datastax/java-driver/6af4247c0b94538f16fbac838eb9d6c0af4098b8/driver-core/src/test/java/com/datastax/driver/core/SchemaChangesTest.java/right.java
    // Create a second cluster to check that other clients also get notified
    // The metadatas of the two clusters should be kept in sync
    @DataProvider(name = "existingKeyspaceName")
    public static Object[][] existingKeyspaceName() {
        return new Object[][]{ { "ks" }, { "\"CaseSensitive\"" } };
    }
    @Test(groups = "short", dataProvider = "existingKeyspaceName")
    public void should_notify_of_table_creation(String keyspace) {
        session.execute(String.format("CREATE TABLE %s.table1(i int primary key)", keyspace));

        for (Metadata m : metadatas)
            assertThat(m.getKeyspace(keyspace).getTable("table1"))
                .isNotNull();
    }
    @Test(groups = "short", dataProvider = "existingKeyspaceName")
    public void should_notify_of_table_update(String keyspace) {
        session.execute(String.format("CREATE TABLE %s.table1(i int primary key)", keyspace));
        session.execute(String.format("ALTER TABLE %s.table1 ADD j int", keyspace));

        for (Metadata m : metadatas)
            assertThat(m.getKeyspace(keyspace).getTable("table1").getColumn("j"))
                .isNotNull();
    }
    @Test(groups = "short", dataProvider = "existingKeyspaceName")
    public void should_notify_of_table_drop(String keyspace) {
        session.execute(String.format("CREATE TABLE %s.table1(i int primary key)", keyspace));
        session.execute(String.format("DROP TABLE %s.table1", keyspace));

        for (Metadata m : metadatas)
            assertThat(m.getKeyspace(keyspace).getTable("table1"))
                .isNull();
    }
    @DataProvider(name = "newKeyspaceName")
    public static Object[][] newKeyspaceName() {
        return new Object[][]{ { "ks2" }, { "\"CaseSensitive2\"" } };
    }
    @Test(groups = "short", dataProvider = "newKeyspaceName")
    public void should_notify_of_keyspace_creation(String keyspace) {
        session.execute(String.format("CREATE KEYSPACE %s WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}", keyspace));

        for (Metadata m : metadatas)
            assertThat(m.getKeyspace(keyspace))
                .isNotNull();
    }
    @Test(groups = "short", dataProvider = "newKeyspaceName")
    public void should_notify_of_keyspace_update(String keyspace) {
        session.execute(String.format("CREATE KEYSPACE %s WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}", keyspace));
        for (Metadata m : metadatas)
            assertThat(m.getKeyspace(keyspace).isDurableWrites())
                .isTrue();

        session.execute(String.format("ALTER KEYSPACE %s WITH durable_writes = false", keyspace));
        for (Metadata m : metadatas)
            assertThat(m.getKeyspace(keyspace).isDurableWrites())
                .isFalse();
    }
    @Test(groups = "short", dataProvider = "newKeyspaceName")
    public void should_notify_of_keyspace_drop(String keyspace) {
        session.execute(String.format("CREATE KEYSPACE %s WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}", keyspace));
        for (Metadata m : metadatas)
            assertThat(m.getReplicas(keyspace, Bytes.fromHexString("0xCAFEBABE")))
                .isNotEmpty();

        session.execute(String.format("DROP KEYSPACE %s", keyspace));

        for (Metadata m : metadatas) {
            assertThat(m.getKeyspace(keyspace))
                .isNull();
            assertThat(m.getReplicas(keyspace, Bytes.fromHexString("0xCAFEBABE")))
                .isEmpty();
        }
    }

    @AfterClass(groups = "short")
    public void teardown() {
        if (cluster2 != null)
            cluster2.close();
    }
}
