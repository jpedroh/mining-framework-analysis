package com.aerospike.client.command;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.cluster.Cluster;
import com.aerospike.client.cluster.LatencyType;
import com.aerospike.client.cluster.Node;

public final class OperateCommand extends ReadCommand {
  private final OperateArgs args;

  public OperateCommand(Cluster cluster, Key key, OperateArgs args) {
    super(cluster, args.writePolicy, key, args.getPartition(cluster, key), true);
    this.args = args;
  }

  @Override protected boolean isWrite() {
    return args.hasWrite;
  }

  @Override protected Node getNode() {
    return args.hasWrite ? partition.getNodeWrite(cluster) : partition.getNodeRead(cluster);
  }

  @Override protected LatencyType getLatencyType() {
    return args.hasWrite ? LatencyType.WRITE : LatencyType.READ;
  }

  @Override protected void writeBuffer() {
    setOperate(args.writePolicy, key, args);
  }

  @Override protected void handleNotFound(int resultCode) {
    if (args.hasWrite) {
      throw new AerospikeException(resultCode);
    }
  }

  @Override protected boolean prepareRetry(boolean timeout) {
    if (args.hasWrite) {
      partition.prepareRetryWrite(timeout);
    } else {
      partition.prepareRetryRead(timeout);
    }
    return true;
  }
}