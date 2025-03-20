package com.aerospike.benchmarks;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.Value;
import com.aerospike.client.async.EventLoop;
import com.aerospike.client.listener.RecordArrayListener;
import com.aerospike.client.listener.RecordListener;
import com.aerospike.client.listener.WriteListener;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.util.RandomShift;

public final class RWTaskAsync extends RWTask {
  private final IAerospikeClient client;

  private final EventLoop eventLoop;

  private final RandomShift random;

  private final WriteListener writeListener;

  private final RecordListener recordListener;

  private final RecordArrayListener recordArrayListener;

  private long begin;

  private final boolean useLatency;

  public RWTaskAsync(IAerospikeClient client, EventLoop eventLoop, Arguments args, CounterStore counters, long keyStart, long keyCount) {
    super(args, counters, keyStart, keyCount);
    this.client = client;
    this.eventLoop = eventLoop;
    this.random = new RandomShift();
    this.useLatency = counters.write.latency != null;
    if (useLatency) {
      writeListener = new LatencyWriteHandler();
      recordListener = new LatencyReadHandler();
      recordArrayListener = new LatencyBatchReadHandler();
    } else {
      writeListener = new WriteHandler();
      recordListener = new ReadHandler();
      recordArrayListener = new BatchReadHandler();
    }
  }

  @Override protected void runNextCommand() {
    if (valid) {
      runCommand(random);
    }
  }

  @Override protected void put(WritePolicy policy, Key key, Bin[] bins) {
    if (useLatency) {
      begin = System.nanoTime();
    }
    client.put(eventLoop, writeListener, policy, key, bins);
  }

  @Override protected void add(Key key, Bin[] bins) {
    if (useLatency) {
      begin = System.nanoTime();
    }
    client.add(eventLoop, writeListener, writePolicyGeneration, key, bins);
  }

  @Override protected void get(Key key, String binName) {
    if (useLatency) {
      begin = System.nanoTime();
    }
    client.get(eventLoop, recordListener, args.readPolicy, key, binName);
  }

  @Override protected void get(Key key) throws AerospikeException {
    if (useLatency) {
      begin = System.nanoTime();
    }
    client.get(eventLoop, recordListener, args.readPolicy, key);
  }

  @Override protected void get(Key key, String udfPackageName, String udfFunctionName, Value[] udfValues) {
  }

  @Override protected void get(Key[] keys, String binName) throws AerospikeException {
    if (useLatency) {
      begin = System.nanoTime();
    }
    client.get(eventLoop, recordArrayListener, args.batchPolicy, keys, binName);
  }

  @Override protected void get(Key[] keys) throws AerospikeException {
    if (useLatency) {
      begin = System.nanoTime();
    }
    client.get(eventLoop, recordArrayListener, args.batchPolicy, keys);
  }

  private final class WriteHandler implements WriteListener {
    @Override public void onSuccess(Key key) {
      counters.write.count.getAndIncrement();
      runNextCommand();
    }

    @Override public void onFailure(AerospikeException ae) {
      writeFailure(ae);
      runNextCommand();
    }
  }

  private final class LatencyWriteHandler implements WriteListener {
    @Override public void onSuccess(Key key) {
      long elapsed = System.nanoTime() - begin;
      counters.write.latency.add(elapsed);
      counters.write.count.getAndIncrement();
      runNextCommand();
    }

    @Override public void onFailure(AerospikeException ae) {
      writeFailure(ae);
      runNextCommand();
    }
  }

  private final class ReadHandler implements RecordListener {
    @Override public void onSuccess(Key key, Record record) {
      if (record == null && args.reportNotFound) {
        counters.readNotFound.getAndIncrement();
      } else {
        counters.read.count.getAndIncrement();
      }
      runNextCommand();
    }

    @Override public void onFailure(AerospikeException ae) {
      readFailure(ae);
      runNextCommand();
    }
  }

  private final class LatencyReadHandler implements RecordListener {
    @Override public void onSuccess(Key key, Record record) {
      long elapsed = System.nanoTime() - begin;
      counters.read.latency.add(elapsed);
      if (record == null && args.reportNotFound) {
        counters.readNotFound.getAndIncrement();
      } else {
        counters.read.count.getAndIncrement();
      }
      runNextCommand();
    }

    @Override public void onFailure(AerospikeException ae) {
      readFailure(ae);
      runNextCommand();
    }
  }

  private final class BatchReadHandler implements RecordArrayListener {
    @Override public void onSuccess(Key[] keys, Record[] records) {
      counters.read.count.getAndIncrement();
      runNextCommand();
    }

    @Override public void onFailure(AerospikeException ae) {
      readFailure(ae);
      runNextCommand();
    }
  }

  private final class LatencyBatchReadHandler implements RecordArrayListener {
    @Override public void onSuccess(Key[] keys, Record[] records) {
      long elapsed = System.nanoTime() - begin;
      counters.read.latency.add(elapsed);
      counters.read.count.getAndIncrement();
      runNextCommand();
    }

    @Override public void onFailure(AerospikeException ae) {
      readFailure(ae);
      runNextCommand();
    }
  }
}