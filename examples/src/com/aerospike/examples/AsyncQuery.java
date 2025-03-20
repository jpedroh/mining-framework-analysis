package com.aerospike.examples;
import java.util.concurrent.atomic.AtomicInteger;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.ResultCode;
import com.aerospike.client.async.EventLoop;
import com.aerospike.client.command.Buffer;
import com.aerospike.client.listener.RecordSequenceListener;
import com.aerospike.client.listener.WriteListener;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.Statement;
import com.aerospike.client.task.IndexTask;
import com.aerospike.client.util.Util;

public class AsyncQuery extends AsyncExample {
  /**
	 * Asynchronous query example.
	 */
  @Override public void runExample(IAerospikeClient client, EventLoop eventLoop) {
    String indexName = "asqindex";
    String keyPrefix = "asqkey";
    String binName = "asqbin";
    int size = 50;
    if (!params.useProxyClient) {
      createIndex(client, indexName, binName);
    }
    runQueryExample(client, eventLoop, keyPrefix, binName, size);
    waitTillComplete();
  }

  private void createIndex(IAerospikeClient client, String indexName, String binName) {
    console.info("Create index: ns=%s set=%s index=%s bin=%s", params.namespace, params.set, indexName, binName);
    Policy policy = new Policy();
    policy.socketTimeout = 0;
    try {
      IndexTask task = client.createIndex(policy, params.namespace, params.set, indexName, binName, IndexType.NUMERIC);
      task.waitTillComplete();
    } catch (AerospikeException ae) {
      if (ae.getResultCode() != ResultCode.INDEX_ALREADY_EXISTS) {
        throw ae;
      }
    }
  }

  private void runQueryExample(final IAerospikeClient client, final EventLoop eventLoop, final String keyPrefix, final String binName, final int size) {
    console.info("Write " + size + " records.");
    WriteListener listener = new WriteListener() {
      private int count = 0;

      public void onSuccess(final Key key) {
        if (++count == size) {
          runQuery(client, eventLoop, binName);
        }
      }

      public void onFailure(AerospikeException e) {
        console.error("Failed to put: " + e.getMessage());
        notifyComplete();
      }
    };
    for (int i = 1; i <= size; i++) {
      Key key = new Key(params.namespace, params.set, keyPrefix + i);
      Bin bin = new Bin(binName, i);
      client.put(eventLoop, listener, writePolicy, key, bin);
    }
  }

  private void runQuery(IAerospikeClient client, EventLoop eventLoop, final String binName) {
    int begin = 26;
    int end = 34;
    console.info("Query for: ns=%s set=%s bin=%s >= %s <= %s", params.namespace, params.set, binName, begin, end);
    Statement stmt = new Statement();
    stmt.setNamespace(params.namespace);
    stmt.setSetName(params.set);
    stmt.setBinNames(binName);
    stmt.setFilter(Filter.range(binName, begin, end));
    final AtomicInteger count = new AtomicInteger();
    client.query(eventLoop, new RecordSequenceListener() {
      public void onRecord(Key key, Record record) throws AerospikeException {
        int result = record.getInt(binName);
        console.info("Record found: ns=%s set=%s bin=%s digest=%s value=%s", key.namespace, key.setName, binName, Buffer.bytesToHexString(key.digest), result);
        count.incrementAndGet();
      }

      public void onSuccess() {
        int size = count.get();
        if (size != 9) {
          console.error("Query count mismatch. Expected 9. Received " + size);
        }
        notifyComplete();
      }

      public void onFailure(AerospikeException e) {
        console.error("Query failed: " + Util.getErrorMessage(e));
        notifyComplete();
      }
    }, null, stmt);
  }
}