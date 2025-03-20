package com.aerospike.examples;
import java.util.concurrent.atomic.AtomicInteger;
import com.aerospike.client.Bin;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.ScanCallback;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.query.PartitionFilter;

public class ScanPage extends Example implements ScanCallback {
  private AtomicInteger recordCount;

  public ScanPage(Console console) {
    super(console);
  }

  /**
	 * Scan in pages.
	 */
  @Override public void runExample(IAerospikeClient client, Parameters params) throws Exception {
    String binName = "bin";
    String setName = "page";
    writeRecords(client, params, setName, binName, 190);
    recordCount = new AtomicInteger();
    ScanPolicy policy = new ScanPolicy();
    policy.maxRecords = 100;
    PartitionFilter filter = PartitionFilter.all();
    for (int i = 0; i < 3 && !filter.isDone(); i++) {
      recordCount.set(0);
      console.info("Scan page: " + i);
      client.scanPartitions(policy, filter, params.namespace, setName, this);
      console.info("Records returned: " + recordCount.get());
    }
  }

  private void writeRecords(IAerospikeClient client, Parameters params, String setName, String binName, int size) throws Exception {
    console.info("Write " + size + " records.");
    for (int i = 1; i <= size; i++) {
      Key key = new Key(params.namespace, setName, i);
      Bin bin = new Bin(binName, i);
      client.put(params.writePolicy, key, bin);
    }
  }

  @Override public void scanCallback(Key key, Record record) {
    recordCount.incrementAndGet();
  }
}