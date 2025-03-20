package com.aerospike.examples;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.ScanCallback;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.query.PartitionFilter;

public class ScanResume extends Example implements ScanCallback {
  private int recordCount;

  private int recordMax;

  public ScanResume(Console console) {
    super(console);
  }

  /**
	 * Terminate a scan and then resume scan later.
	 */
  @Override public void runExample(IAerospikeClient client, Parameters params) throws Exception {
    String binName = "bin";
    String setName = "resume";
    writeRecords(client, params, setName, binName, 200);
    ScanPolicy policy = new ScanPolicy();
    policy.concurrentNodes = false;
    PartitionFilter filter = PartitionFilter.all();
    recordCount = 0;
    recordMax = 50;
    console.info("Start scan terminate");
    try {
      client.scanPartitions(policy, filter, params.namespace, setName, this);
    } catch (AerospikeException.ScanTerminated e) {
      console.info("Scan terminated as expected");
    }
    console.info("Records returned: " + recordCount);
    recordCount = 0;
    recordMax = 0;
    console.info("Start scan resume");
    client.scanPartitions(policy, filter, params.namespace, setName, this);
    console.info("Records returned: " + recordCount);
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
    recordCount++;
    if (recordMax > 0 && recordCount >= recordMax) {
      throw new AerospikeException.ScanTerminated();
    }
  }
}