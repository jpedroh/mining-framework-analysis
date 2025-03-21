package org.apache.accumulo.test.functional;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.UncheckedIOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.accumulo.core.Constants;
import org.apache.accumulo.core.client.Accumulo;
import org.apache.accumulo.core.client.AccumuloClient;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.conf.Property;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.TableId;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.fate.zookeeper.ZooReaderWriter;
import org.apache.accumulo.core.fate.zookeeper.ZooUtil;
import org.apache.accumulo.core.gc.GcCandidate;
import org.apache.accumulo.core.gc.ReferenceFile;
import org.apache.accumulo.core.lock.ServiceLock;
import org.apache.accumulo.core.lock.ServiceLockData;
import org.apache.accumulo.core.lock.ServiceLockData.ThriftService;
import org.apache.accumulo.core.metadata.MetadataTable;
import org.apache.accumulo.core.metadata.StoredTabletFile;
import org.apache.accumulo.core.metadata.schema.Ample;
import org.apache.accumulo.core.metadata.schema.Ample.DataLevel;
import org.apache.accumulo.core.metadata.schema.MetadataSchema.DeletesSection;
import org.apache.accumulo.core.metadata.schema.MetadataSchema.DeletesSection.SkewedKeyValue;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.accumulo.core.security.TablePermission;
import org.apache.accumulo.gc.SimpleGarbageCollector;
import org.apache.accumulo.minicluster.MemoryUnit;
import org.apache.accumulo.minicluster.ServerType;
import org.apache.accumulo.miniclusterImpl.MiniAccumuloClusterImpl.ProcessInfo;
import org.apache.accumulo.miniclusterImpl.MiniAccumuloConfigImpl;
import org.apache.accumulo.miniclusterImpl.ProcessNotFoundException;
import org.apache.accumulo.miniclusterImpl.ProcessReference;
import org.apache.accumulo.test.TestIngest;
import org.apache.accumulo.test.VerifyIngest;
import org.apache.accumulo.test.VerifyIngest.VerifyParams;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RawLocalFileSystem;
import org.apache.hadoop.io.Text;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Iterators;
import com.google.common.net.HostAndPort;

public class GarbageCollectorIT extends ConfigurableMacBase {
  private static final String OUR_SECRET = "itsreallysecret";

  public static final Logger log = LoggerFactory.getLogger(GarbageCollectorIT.class);

  @Override protected Duration defaultTimeout() {
    return Duration.ofMinutes(5);
  }

  @Override public void configure(MiniAccumuloConfigImpl cfg, Configuration hadoopCoreSite) {
    cfg.setProperty(Property.INSTANCE_ZK_TIMEOUT, "15s");
    cfg.setProperty(Property.INSTANCE_SECRET, OUR_SECRET);
    cfg.setProperty(Property.GC_CYCLE_START, "1");
    cfg.setProperty(Property.GC_CYCLE_DELAY, "1");
    cfg.setProperty(Property.GC_PORT, "0");
    cfg.setProperty(Property.TSERV_MAXMEM, "5K");
    cfg.setProperty(Property.TSERV_MAJC_DELAY, "1");
    cfg.setProperty(Property.GC_CANDIDATE_BATCH_SIZE, "256K");
    hadoopCoreSite.set("fs.file.impl", RawLocalFileSystem.class.getName());
  }

  private void killMacGc() throws ProcessNotFoundException, InterruptedException, KeeperException {
    getCluster().killProcess(ServerType.GARBAGE_COLLECTOR, getCluster().getProcesses().get(ServerType.GARBAGE_COLLECTOR).iterator().next());
    var path = ServiceLock.path(getServerContext().getZooKeeperRoot() + Constants.ZGC_LOCK);
    ZooReaderWriter zk = getServerContext().getZooReaderWriter();
    try {
      ServiceLock.deleteLock(zk, path);
    } catch (IllegalStateException e) {
      log.error("Unable to delete ZooLock for mini accumulo-gc", e);
    }
    assertNull(getCluster().getProcesses().get(ServerType.GARBAGE_COLLECTOR));
  }

  @Test public void gcTest() throws Exception {
    killMacGc();
    final String table = "test_ingest";
    try (AccumuloClient c = Accumulo.newClient().from(getClientProperties()).build()) {
      c.tableOperations().create(table);
      c.tableOperations().setProperty(table, Property.TABLE_SPLIT_THRESHOLD.getKey(), "5K");
      VerifyParams params = new VerifyParams(getClientProperties(), table, 10_000);
      params.cols = 1;
      log.info("Ingesting files to {}", table);
      TestIngest.ingest(c, cluster.getFileSystem(), params);
      log.info("Compacting the table {}", table);
      c.tableOperations().compact(table, null, null, true, true);
      String pathString = cluster.getConfig().getDir() + "/accumulo/tables/1/*/*.rf";
      log.info("Counting files in path: {}", pathString);
      int before = countFiles(pathString);
      log.info("Counted {} files in path: {}", before, pathString);
      while (true) {
        Thread.sleep(SECONDS.toMillis(1));
        int more = countFiles(pathString);
        if (more <= before) {
          break;
        }
        before = more;
      }
      log.info("Restarting GC...");
      getCluster().start();
      Thread.sleep(SECONDS.toMillis(15));
      log.info("Again Counting files in path: {}", pathString);
      int after = countFiles(pathString);
      log.info("Counted {} files in path: {}", after, pathString);
      VerifyIngest.verifyIngest(c, params);
      assertTrue(after < before, "After count " + after + " was not less than " + before);
    }
  }

  @Test public void gcLotsOfCandidatesIT() throws Exception {
    killMacGc();
    log.info("Filling metadata table with bogus delete flags");
    try (AccumuloClient c = Accumulo.newClient().from(getClientProperties()).build()) {
      addEntries(c);
      cluster.getConfig().setDefaultMemory(32, MemoryUnit.MEGABYTE);
      ProcessInfo gc = cluster.exec(SimpleGarbageCollector.class);
      Thread.sleep(SECONDS.toMillis(20));
      String output = "";
      while (!output.contains("has exceeded the threshold")) {
        try {
          output = gc.readStdOut();
        } catch (UncheckedIOException ex) {
          log.error("IO error reading the IT\'s accumulo-gc STDOUT", ex);
          break;
        }
      }
      gc.getProcess().destroy();
      assertTrue(output.contains("has exceeded the threshold"));
    }
  }

  @Test public void dontGCRootLog() throws Exception {
    killMacGc();
    try (AccumuloClient c = Accumulo.newClient().from(getClientProperties()).build()) {
      String table = getUniqueNames(1)[0];
      c.tableOperations().create(table);
      cluster.start();
      Thread.sleep(SECONDS.toMillis(20));
      killMacGc();
      for (ProcessReference ref : cluster.getProcesses().get(ServerType.TABLET_SERVER)) {
        cluster.killProcess(ServerType.TABLET_SERVER, ref);
      }
      cluster.start();
      try (Scanner scanner = c.createScanner(MetadataTable.NAME, Authorizations.EMPTY)) {
        scanner.forEach((k, v) -> {
        });
      }
    }
  }

  private Mutation createDelMutation(String path, String cf, String cq, String val) {
    Text row = new Text(DeletesSection.encodeRow(path));
    Mutation delFlag = new Mutation(row);
    delFlag.put(cf, cq, val);
    return delFlag;
  }

  @Test public void testInvalidDelete() throws Exception {
    killMacGc();
    try (AccumuloClient c = Accumulo.newClient().from(getClientProperties()).build()) {
      String table = getUniqueNames(1)[0];
      c.tableOperations().create(table);
      try (BatchWriter bw = c.createBatchWriter(table)) {
        Mutation m1 = new Mutation("r1");
        m1.put("cf1", "cq1", "v1");
        bw.addMutation(m1);
      }
      c.tableOperations().flush(table, null, null, true);
      c.securityOperations().grantTablePermission(c.whoami(), MetadataTable.NAME, TablePermission.WRITE);
      try (BatchWriter bw = c.createBatchWriter(MetadataTable.NAME)) {
        bw.addMutation(createDelMutation("", "", "", ""));
        bw.addMutation(createDelMutation("", "testDel", "test", "valueTest"));
        bw.addMutation(createDelMutation("/", "", "", SkewedKeyValue.STR_NAME));
      }
      ProcessInfo gc = cluster.exec(SimpleGarbageCollector.class);
      try {
        String output = "";
        while (!output.contains("Ignoring invalid deletion candidate")) {
          Thread.sleep(250);
          try {
            output = gc.readStdOut();
          } catch (UncheckedIOException ioe) {
            log.error("Could not read all from cluster.", ioe);
          }
        }
      }  finally {
        gc.getProcess().destroy();
      }
      try (Scanner scanner = c.createScanner(table, Authorizations.EMPTY)) {
        Entry<Key, Value> entry = getOnlyElement(scanner);
        assertEquals("r1", entry.getKey().getRow().toString());
        assertEquals("cf1", entry.getKey().getColumnFamily().toString());
        assertEquals("cq1", entry.getKey().getColumnQualifier().toString());
        assertEquals("v1", entry.getValue().toString());
      }
    }
  }

  @Test public void testUserUniqueMutationDelete() throws Exception {
    killMacGc();
    try (AccumuloClient c = Accumulo.newClient().from(getClientProperties()).build()) {
      String table = getUniqueNames(1)[0];
      c.tableOperations().create(table);
      log.info("User GcCandidate Deletion test of table: {}", table);
      log.info("GcCandidates will be added/removed from table: {}", DataLevel.USER.metaTable());
      createAndDeleteUniqueMutation(TableId.of(table), Ample.GcCandidateType.INUSE);
    }
  }

  @Test public void testMetadataUniqueMutationDelete() throws Exception {
    killMacGc();
    TableId tableId = DataLevel.USER.tableId();
    log.info("Metadata GcCandidate Deletion test");
    log.info("GcCandidates will be added/removed from table: {}", DataLevel.METADATA.metaTable());
    createAndDeleteUniqueMutation(tableId, Ample.GcCandidateType.INUSE);
  }

  /**
   * Root INUSE deletions are not supported in 2.1.x. This test can be migrated to use
   * createAndDeleteUniqueMutation in 3.x
   *
   * @throws Exception may occur when killing the GC process.
   */
  @Test public void testRootUniqueMutationDelete() throws Exception {
    killMacGc();
    TableId tableId = DataLevel.METADATA.tableId();
    log.info("Root GcCandidate Deletion test");
    log.info("GcCandidates will be added but not removed from Zookeeper");
    Ample ample = cluster.getServerContext().getAmple();
    DataLevel datalevel = DataLevel.ROOT;
    Iterator<GcCandidate> cIter = ample.getGcCandidates(datalevel);
    ArrayList<GcCandidate> tempCandidates = new ArrayList<>();
    while (cIter.hasNext()) {
      GcCandidate cTemp = cIter.next();
      tempCandidates.add(cTemp);
    }
    if (tempCandidates.size() != 0) {
      ample.deleteGcCandidates(datalevel, tempCandidates, Ample.GcCandidateType.VALID);
      tempCandidates.clear();
      cIter = ample.getGcCandidates(datalevel);
      while (cIter.hasNext()) {
        GcCandidate cTemp = cIter.next();
        log.debug("PreExisting Candidate Found: {}", cTemp);
        tempCandidates.add(cTemp);
      }
      assertEquals(0, tempCandidates.size());
    }
    List<GcCandidate> candidates = List.of(new GcCandidate("hdfs://foo.com:6000/user/foo/tables/+r/t-0/F00.rf", 0L), new GcCandidate("hdfs://foo.com:6000/user/foo/tables/+r/t-0/F001.rf", 1L));
    List<StoredTabletFile> stfs = candidates.stream().map((temp) -> StoredTabletFile.of(new Path(temp.getPath()))).collect(Collectors.toList());
    log.debug("Adding root table GcCandidates");
    ample.putGcCandidates(tableId, stfs);
    cIter = ample.getGcCandidates(datalevel);
    int counter = 0;
    while (cIter.hasNext()) {
      ample.putGcCandidates(tableId, List.of(StoredTabletFile.of(new Path(cIter.next().getPath()))));
      counter++;
    }
    assertEquals(2, counter);
    cIter = ample.getGcCandidates(datalevel);
    while (cIter.hasNext()) {
      ample.deleteGcCandidates(datalevel, List.of(cIter.next()), Ample.GcCandidateType.INUSE);
    }
    cIter = ample.getGcCandidates(datalevel);
    counter = candidates.size();
    while (cIter.hasNext()) {
      GcCandidate gcC = cIter.next();
      log.debug("Candidate Found: {}", gcC);
      for (GcCandidate cand : candidates) {
        if (gcC.getPath().equals(cand.getPath())) {
          assertNotEquals(gcC.getUid(), cand.getUid());
          counter--;
        }
      }
    }
    assertEquals(0, counter);
    cIter = ample.getGcCandidates(datalevel);
    while (cIter.hasNext()) {
      ample.deleteGcCandidates(datalevel, List.of(cIter.next()), Ample.GcCandidateType.VALID);
    }
    cIter = ample.getGcCandidates(datalevel);
    counter = 0;
    while (cIter.hasNext()) {
      GcCandidate gcC = cIter.next();
      if (gcC != null) {
        log.error("Candidate Found: {}", gcC);
        counter++;
      }
    }
    assertEquals(0, counter);
  }

  @Test public void testProperPortAdvertisement() throws Exception {
    try (AccumuloClient client = Accumulo.newClient().from(getClientProperties()).build()) {
      ZooReaderWriter zk = cluster.getServerContext().getZooReaderWriter();
      var path = ServiceLock.path(ZooUtil.getRoot(client.instanceOperations().getInstanceId()) + Constants.ZGC_LOCK);
      for (int i = 0; i < 5; i++) {
        List<String> locks;
        try {
          locks = ServiceLock.validateAndSort(path, zk.getChildren(path.toString()));
        } catch (NoNodeException e) {
          Thread.sleep(5000);
          continue;
        }
        if (locks != null && !locks.isEmpty()) {
          String lockPath = path + "/" + locks.get(0);
          Optional<ServiceLockData> sld = ServiceLockData.parse(zk.getData(lockPath));
          assertNotNull(sld.orElseThrow());
          HostAndPort hostAndPort = sld.orElseThrow().getAddress(ThriftService.GC);
          assertNotEquals("0.0.0.0", hostAndPort.getHost());
          assertNotEquals(0, hostAndPort.getPort());
          return;
        }
        Thread.sleep(5000);
      }
      fail("Could not find advertised GC address");
    }
  }

  private int countFiles(String pathStr) throws Exception {
    Path path = new Path(pathStr);
    return Iterators.size(Arrays.asList(cluster.getFileSystem().globStatus(path)).iterator());
  }

  private void addEntries(AccumuloClient client) throws Exception {
    Ample ample = getServerContext().getAmple();
    client.securityOperations().grantTablePermission(client.whoami(), MetadataTable.NAME, TablePermission.WRITE);
    try (BatchWriter bw = client.createBatchWriter(MetadataTable.NAME)) {
      for (int i = 0; i < 100000; ++i) {
        String longpath = "aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeee" + "ffffffffffgggggggggghhhhhhhhhhiiiiiiiiiijjjjjjjjjj";
        var path = URI.create(String.format("file:/%020d/%s", i, longpath));
        Mutation delFlag = ample.createDeleteMutation(ReferenceFile.forFile(TableId.of("1"), new Path(path)));
        bw.addMutation(delFlag);
      }
    }
  }

  private void createAndDeleteUniqueMutation(TableId tableId, Ample.GcCandidateType type) {
    Ample ample = cluster.getServerContext().getAmple();
    DataLevel datalevel = Ample.DataLevel.of(tableId);
    List<GcCandidate> candidates = new ArrayList<>();
    Iterator<GcCandidate> candidate = ample.getGcCandidates(datalevel);
    while (candidate.hasNext()) {
      GcCandidate cTemp = candidate.next();
      log.debug("PreExisting Candidate Found: {}", cTemp);
      candidates.add(cTemp);
    }
    assertEquals(0, candidates.size());
    List<StoredTabletFile> stfs = Stream.of("hdfs://foo.com:6000/user/foo/tables/a/t-0/F00.rf", "hdfs://foo.com:6000/user/foo/tables/b/t-0/F00.rf").map(Path::new).map(StoredTabletFile::of).collect(Collectors.toList());
    log.debug("Adding candidates to table {}", tableId);
    ample.putGcCandidates(tableId, stfs);
    candidate = ample.getGcCandidates(datalevel);
    while (candidate.hasNext()) {
      GcCandidate cTemp = candidate.next();
      log.debug("Candidate Found: {}", cTemp);
      candidates.add(cTemp);
    }
    assertEquals(2, candidates.size());
    GcCandidate deleteCandidate = candidates.get(0);
    assertNotNull(deleteCandidate);
    ample.putGcCandidates(tableId, List.of(StoredTabletFile.of(new Path(deleteCandidate.getPath()))));
    log.debug("Deleting Candidate {}", deleteCandidate);
    ample.deleteGcCandidates(datalevel, List.of(deleteCandidate), Ample.GcCandidateType.INUSE);
    candidate = ample.getGcCandidates(datalevel);
    int counter = 0;
    boolean foundNewCandidate = false;
    while (candidate.hasNext()) {
      GcCandidate gcC = candidate.next();
      log.debug("Candidate Found: {}", gcC);
      if (gcC.getPath().equals(deleteCandidate.getPath())) {
        assertNotEquals(gcC.getUid(), deleteCandidate.getUid());
        foundNewCandidate = true;
      }
      counter++;
    }
    assertEquals(2, counter);
    assertTrue(foundNewCandidate);
  }
}