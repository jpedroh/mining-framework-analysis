package com.aerospike.examples;
import java.util.HashMap;
import java.util.Map;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.ResultCode;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.IndexCollectionType;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.aerospike.client.task.IndexTask;

public class QueryCollection extends Example {
  public QueryCollection(Console console) {
    super(console);
  }

  /**
	 * Query records using a map index.
	 */
  @Override public void runExample(IAerospikeClient client, Parameters params) throws Exception {
    String indexName = "mapkey_index";
    String keyPrefix = "qkey";
    String mapKeyPrefix = "mkey";
    String mapValuePrefix = "qvalue";
    String binName = "map_bin";
    int size = 20;
    createIndex(client, params, indexName, binName);
    writeRecords(client, params, keyPrefix, binName, mapKeyPrefix, mapValuePrefix, size);
    runQuery(client, params, indexName, binName, mapKeyPrefix + 2);
    client.dropIndex(params.policy, params.namespace, params.set, indexName);
  }

  private void createIndex(IAerospikeClient client, Parameters params, String indexName, String binName) throws Exception {
    console.info("Create mapkeys index: ns=%s set=%s index=%s bin=%s", params.namespace, params.set, indexName, binName);
    Policy policy = new Policy();
    policy.socketTimeout = 0;
    try {
      IndexTask task = client.createIndex(policy, params.namespace, params.set, indexName, binName, IndexType.STRING, IndexCollectionType.MAPKEYS);
      task.waitTillComplete();
    } catch (AerospikeException ae) {
      if (ae.getResultCode() != ResultCode.INDEX_ALREADY_EXISTS) {
        throw ae;
      }
    }
  }

  private void writeRecords(IAerospikeClient client, Parameters params, String keyPrefix, String binName, String mapKeyPrefix, String valuePrefix, int size) throws Exception {
    for (int i = 1; i <= size; i++) {
      Key key = new Key(params.namespace, params.set, keyPrefix + i);
      HashMap<String, String> map = new HashMap<String, String>();
      map.put(mapKeyPrefix + 1, valuePrefix + i);
      if (i % 2 == 0) {
        map.put(mapKeyPrefix + 2, valuePrefix + i);
      }
      if (i % 3 == 0) {
        map.put(mapKeyPrefix + 3, valuePrefix + i);
      }
      Bin bin = new Bin(binName, map);
      client.put(params.writePolicy, key, bin);
    }
  }

  private void runQuery(IAerospikeClient client, Parameters params, String indexName, String binName, String queryMapKey) throws Exception {
    console.info("Query for: ns=%s set=%s index=%s bin=%s mapkey contains=%s", params.namespace, params.set, indexName, binName, queryMapKey);
    Statement stmt = new Statement();
    stmt.setNamespace(params.namespace);
    stmt.setSetName(params.set);
    stmt.setBinNames(binName);
    stmt.setFilter(Filter.contains(binName, IndexCollectionType.MAPKEYS, queryMapKey));
    RecordSet rs = client.query(null, stmt);
    try {
      int count = 0;
      while (rs.next()) {
        Record record = rs.getRecord();
        Map<?, ?> result = (Map<?, ?>) record.getValue(binName);
        if (result.containsKey(queryMapKey)) {
        } else {
          console.error("Query mismatch: Expected mapKey %s. Received %s.", queryMapKey, result);
        }
        count++;
      }
      if (count == 0) {
        console.error("Query failed. No records returned.");
      } else {
        console.info("Number of records %d", count);
      }
    }  finally {
      rs.close();
    }
  }
}