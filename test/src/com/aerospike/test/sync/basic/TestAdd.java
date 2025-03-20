package com.aerospike.test.sync.basic;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.ResultCode;
import com.aerospike.client.Value;
import com.aerospike.client.util.Version;
import com.aerospike.test.sync.TestSync;

public class TestAdd extends TestSync {
  @Test public void add() {
    Key key = new Key(args.namespace, args.set, "addkey");
    String binName = "addbin";
    client.delete(null, key);
    Bin bin = new Bin(binName, 10);
    client.add(null, key, bin);
    bin = new Bin(binName, 5);
    client.add(null, key, bin);
    Record record = client.get(null, key, bin.name);
    assertBinEqual(key, record, bin.name, 15);
    bin = new Bin(binName, 30);
    record = client.operate(null, key, Operation.add(bin), Operation.get(bin.name));
    assertBinEqual(key, record, bin.name, 45);
  }

  @Test public void addNullValue() {
    Version version = Version.getServerVersion(client, null);
    if (version.isLess(3, 6, 1)) {
      return;
    }
    Key key = new Key(args.namespace, args.set, "addkey");
    String binName = "addbin";
    client.delete(null, key);
    Bin bin = new Bin(binName, Value.get((Object) null));
    AerospikeException ae = assertThrows(AerospikeException.class, new ThrowingRunnable() {
      public void run() {
        client.add(null, key, bin);
      }
    });
    assertEquals(ae.getResultCode(), ResultCode.PARAMETER_ERROR);
  }
}