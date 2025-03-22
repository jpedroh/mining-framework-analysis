package com.lambdaworks.redis.cluster;
import com.lambdaworks.redis.RedisChannelWriter;
import com.lambdaworks.redis.protocol.AsyncCommand;
import com.lambdaworks.redis.protocol.CommandArgs;
import com.lambdaworks.redis.protocol.CommandKeyword;
import com.lambdaworks.redis.protocol.CommandWrapper;
import com.lambdaworks.redis.protocol.ProtocolKeyword;
import com.lambdaworks.redis.protocol.RedisCommand;
import io.netty.buffer.ByteBuf;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 3.0
 */
class ClusterCommand<K extends java.lang.Object, V extends java.lang.Object, T extends java.lang.Object> extends CommandWrapper<K, V, T> implements RedisCommand<K, V, T> {
  private boolean completed;

  private RedisChannelWriter<K, V> retry;

  private int executions;

  private int executionLimit;

  ClusterCommand(RedisCommand<K, V, T> command, RedisChannelWriter<K, V> retry, int executionLimit) {
    super(command);
    this.retry = retry;
    this.executionLimit = executionLimit;
  }

  @Override public void complete() {
    executions++;

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/ClusterCommand.java/left.java
    try {
      if (executions < executionLimit && (isMoved() || isAsk())) {
        retry.write(this);
        return;
      }
    } catch (Exception e) {
      setException(e);
      command.complete();
      return;
    }
=======
    if (executions < executionLimit && (isMoved() || isAsk())) {
      try {
        retry.write(this);
      } catch (Exception e) {
        completeExceptionally(e);
      }
      return;
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/ClusterCommand.java/right.java

    super.complete();
    completed = true;
  }

  public boolean isMoved() {
    if (command.getOutput() != null && command.getOutput().getError() != null && command.getOutput().getError().startsWith(CommandKeyword.MOVED.name())) {
      return true;
    }
    return false;
  }

  public boolean isAsk() {
    if (getError() != null && getError().startsWith(CommandKeyword.ASK.name())) {
      return true;
    }
    return false;
  }

  @Override public boolean isDone() {
    return isCompleted();
  }

  @Override public CommandArgs<K, V> getArgs() {
    return command.getArgs();
  }

  public String getError() {
    if (command.getOutput() != null) {
      return command.getOutput().getError();
    }
    return null;
  }

  @Override public void encode(ByteBuf buf) {
    command.encode(buf);
  }

  @Override public boolean completeExceptionally(Throwable ex) {
    boolean result = command.completeExceptionally(ex);
    completed = true;
    return result;
  }

  public int getExecutions() {
    return executions;
  }

  @Override public ProtocolKeyword getType() {
    return 
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/ClusterCommand.java/left.java
    command.getType()
=======
    command.getType()
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/ClusterCommand.java/right.java
    ;
  }

  public boolean isCompleted() {
    return completed;
  }


<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/ClusterCommand.java/left.java
  @Override public boolean setException(Throwable exception) {
    return command.setException(exception);
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName());
    sb.append(" [command=").append(command);
    sb.append(", executions=").append(executions);
    sb.append(']');
    return sb.toString();
  }
}