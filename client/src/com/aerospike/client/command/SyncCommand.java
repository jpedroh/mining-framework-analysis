package com.aerospike.client.command;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.ResultCode;
import com.aerospike.client.cluster.Cluster;
import com.aerospike.client.cluster.Connection;
import com.aerospike.client.cluster.ConnectionRecover;
import com.aerospike.client.cluster.Node;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.util.ThreadLocalData;
import com.aerospike.client.util.Util;

public abstract class SyncCommand extends Command {
  protected final Cluster cluster;

  protected final Policy policy;

  int iteration = 1;

  int commandSentCounter;

  long deadline;

  /**
	 * Default constructor.
	 */
  public SyncCommand(Cluster cluster, Policy policy) {
    super(policy.socketTimeout, policy.totalTimeout, policy.maxRetries);
    this.cluster = cluster;
    this.policy = policy;
  }

  /**
	 * Scan/Query constructor.
	 */
  public SyncCommand(Cluster cluster, Policy policy, int socketTimeout, int totalTimeout) {
    super(socketTimeout, totalTimeout, 0);
    this.cluster = cluster;
    this.policy = policy;
  }

  public void execute() {
    if (totalTimeout > 0) {
      deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(totalTimeout);
    }
    executeCommand();
  }

  public final void executeCommand() {
    Node node;
    AerospikeException exception = null;
    boolean isClientTimeout;
    while (true) {
      try {
        node = getNode();
      } catch (AerospikeException ae) {
        if (cluster.isActive()) {
          ae.setPolicy(policy);
          ae.setIteration(iteration);
          ae.setInDoubt(isWrite(), commandSentCounter);
          throw ae;
        } else {
          throw new AerospikeException("Cluster has been closed");
        }
      }
      try {
        node.validateErrorCount();
        Connection conn = node.getConnection(this, policy.connectTimeout, socketTimeout, policy.timeoutDelay);
        try {
          writeBuffer();
          conn.write(dataBuffer, dataOffset);
          commandSentCounter++;
          parseResult(conn);
          node.putConnection(conn);
          return;
        } catch (AerospikeException ae) {
          if (ae.keepConnection()) {
            node.putConnection(conn);
          } else {
            node.closeConnection(conn);
          }
          if (ae.getResultCode() == ResultCode.TIMEOUT) {
            exception = new AerospikeException.Timeout(policy, false);
            isClientTimeout = false;
            node.incrErrorCount();
          } else {
            if (ae.getResultCode() == ResultCode.DEVICE_OVERLOAD) {
              exception = ae;
              isClientTimeout = false;
              node.incrErrorCount();
            } else {
              throw ae;
            }
          }
        } catch (Connection.ReadTimeout crt) {
          if (policy.timeoutDelay > 0) {
            cluster.recoverConnection(new ConnectionRecover(conn, node, policy.timeoutDelay, crt, isSingle()));
          } else {
            node.closeConnection(conn);
          }
          isClientTimeout = true;
        } catch (RuntimeException re) {
          node.closeConnection(conn);
          throw re;
        } catch (SocketTimeoutException ste) {
          node.closeConnection(conn);
          isClientTimeout = true;
        } catch (IOException ioe) {
          node.closeConnection(conn);
          exception = new AerospikeException.Connection(ioe);
          isClientTimeout = false;
        }
      } catch (Connection.ReadTimeout crt) {
        isClientTimeout = true;
      } catch (AerospikeException.Connection ce) {
        exception = ce;
        isClientTimeout = false;
      } catch (AerospikeException.Backoff be) {
        exception = be;
        isClientTimeout = false;
      } catch (AerospikeException ae) {
        ae.setNode(node);
        ae.setPolicy(policy);
        ae.setIteration(iteration);
        ae.setInDoubt(isWrite(), commandSentCounter);
        throw ae;
      }
      if (iteration > maxRetries) {
        break;
      }
      if (totalTimeout > 0) {
        long remaining = deadline - System.nanoTime() - TimeUnit.MILLISECONDS.toNanos(policy.sleepBetweenRetries);
        if (remaining <= 0) {
          break;
        }
        remaining = TimeUnit.NANOSECONDS.toMillis(remaining);
        if (remaining < totalTimeout) {
          totalTimeout = (int) remaining;
          if (socketTimeout > totalTimeout) {
            socketTimeout = totalTimeout;
          }
        }
      }
      if (!isClientTimeout && policy.sleepBetweenRetries > 0) {
        Util.sleep(policy.sleepBetweenRetries);
      }
      iteration++;
      if (!prepareRetry(isClientTimeout || exception.getResultCode() != ResultCode.SERVER_NOT_AVAILABLE)) {
        if (retryBatch(cluster, socketTimeout, totalTimeout, deadline, iteration, commandSentCounter)) {
          return;
        }
      }
    }
    if (isClientTimeout) {
      exception = new AerospikeException.Timeout(policy, true);
    }
    exception.setNode(node);
    exception.setPolicy(policy);
    exception.setIteration(iteration);
    exception.setInDoubt(isWrite(), commandSentCounter);
    throw exception;
  }

  public void resetDeadline(long startTime) {
    long elapsed = System.nanoTime() - startTime;
    deadline += elapsed;
  }

  protected void sizeBuffer(int size) {
    if (size > dataBuffer.length) {
      dataBuffer = ThreadLocalData.resizeBuffer(size);
    }
  }

  protected boolean retryBatch(Cluster cluster, int socketTimeout, int totalTimeout, long deadline, int iteration, int commandSentCounter) {
    return false;
  }

  protected boolean isSingle() {
    return true;
  }

  protected boolean isWrite() {
    return false;
  }

  protected abstract Node getNode();

  protected abstract void writeBuffer();

  protected abstract void parseResult(Connection conn) throws AerospikeException, IOException;

  protected abstract boolean prepareRetry(boolean timeout);
}