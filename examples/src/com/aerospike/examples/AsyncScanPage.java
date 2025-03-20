package com.aerospike.examples;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.async.EventLoop;
import com.aerospike.client.listener.RecordSequenceListener;
import com.aerospike.client.listener.WriteListener;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.query.PartitionFilter;
import com.aerospike.client.util.Util;

public class AsyncScanPage extends AsyncExample {
  private static final String binName = "bin";

  private static final String setName = "apage";

  private static final int size = 50;

  /**
	 * Asynchronous scan example.
	 */
  @Override public void runExample(IAerospikeClient client, EventLoop eventLoop) {
    console.info("Write " + size + " records.");
    WriteListener listener = new WriteListener() {
      private int count = 0;

      public void onSuccess(final Key key) {
        if (++count == size) {
          runScan(client, eventLoop);
        }
      }

      public void onFailure(AerospikeException e) {
        console.error("Failed to put: " + e.getMessage());
        notifyComplete();
      }
    };
    for (int i = 1; i <= size; i++) {
      Key key = new Key(params.namespace, setName, i);
      Bin bin = new Bin(binName, i);
      client.put(eventLoop, listener, writePolicy, key, bin);
    }
    waitTillComplete();
  }

  private void runScan(IAerospikeClient client, EventLoop eventLoop) {
    int pageSize = 30;
    console.info("Scan max " + pageSize + " records.");
    ScanPolicy policy = new ScanPolicy();
    policy.maxRecords = pageSize;
    PartitionFilter filter = PartitionFilter.all();
    RecordSequenceListener listener = new RecordSequenceListener() {
      private int count = 0;

      @Override public void onRecord(Key key, Record record) throws AerospikeException {
        count++;
      }

      @Override public void onSuccess() {
        console.info("Records returned: " + count);
        notifyComplete();
      }

      @Override public void onFailure(AerospikeException e) {
        console.error("Scan failed: " + Util.getErrorMessage(e));
        notifyComplete();
      }
    };
    client.scanPartitions(eventLoop, listener, policy, filter, params.namespace, setName);
  }
}