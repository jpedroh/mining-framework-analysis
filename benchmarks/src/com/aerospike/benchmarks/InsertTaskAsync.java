package com.aerospike.benchmarks;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.async.EventLoop;
import com.aerospike.client.listener.WriteListener;
import com.aerospike.client.util.RandomShift;

public final class InsertTaskAsync extends InsertTask {
  private final IAerospikeClient client;

  private final EventLoop eventLoop;

  private final RandomShift random;

  private final WriteListener listener;

  private final long keyStart;

  private final long keyMax;

  private long keyCount;

  private long begin;

  private final boolean useLatency;

  public InsertTaskAsync(IAerospikeClient client, EventLoop eventLoop, Arguments args, CounterStore counters, long keyStart, long keyMax) {
    super(args, counters);
    this.client = client;
    this.eventLoop = eventLoop;
    this.random = new RandomShift();
    this.keyStart = keyStart;
    this.keyMax = keyMax;
    this.useLatency = counters.write.latency != null;
    if (useLatency) {
      listener = new LatencyWriteHandler();
    } else {
      listener = new WriteHandler();
    }
  }

  public void runCommand() {
    long currentKey = keyStart + keyCount;
    Key key = new Key(args.namespace, args.setName, currentKey);
    Bin[] bins = args.getBins(random, true, currentKey);
    if (useLatency) {
      begin = System.nanoTime();
    }
    client.put(eventLoop, listener, args.writePolicy, key, bins);
  }

  private final class LatencyWriteHandler implements WriteListener {
    @Override public void onSuccess(Key key) {
      long elapsed = System.nanoTime() - begin;
      counters.write.latency.add(elapsed);
      counters.write.count.getAndIncrement();
      keyCount++;
      if (keyCount < keyMax) {
        runCommand();
      }
    }

    @Override public void onFailure(AerospikeException ae) {
      writeFailure(ae);
      runCommand();
    }
  }

  private final class WriteHandler implements WriteListener {
    @Override public void onSuccess(Key key) {
      counters.write.count.getAndIncrement();
      keyCount++;
      if (keyCount < keyMax) {
        runCommand();
      }
    }

    @Override public void onFailure(AerospikeException ae) {
      writeFailure(ae);
      runCommand();
    }
  }
}