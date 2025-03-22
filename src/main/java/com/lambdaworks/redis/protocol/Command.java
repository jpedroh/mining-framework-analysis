package com.lambdaworks.redis.protocol;
import static com.google.common.base.Preconditions.checkArgument;
import com.lambdaworks.redis.output.CommandOutput;
import io.netty.buffer.ByteBuf;

/**
 * A redis command and its result. All successfully executed commands will eventually return a {@link CommandOutput} object.
 * 
 * @param <K> Key type.
 * @param <V> Value type.
 * @param <T> Command output type.
 * 
 * @author Will Glozer
 */
public class Command<K extends java.lang.Object, V extends java.lang.Object, T extends java.lang.Object> implements RedisCommand<K, V, T> {
  private static final byte[] CRLF = "\r\n".getBytes(LettuceCharsets.ASCII);

  protected CommandArgs<K, V> args;

  private final ProtocolKeyword type;

  protected CommandOutput<K, V, T> output;

  protected boolean completed = false;

  protected Throwable exception;

  protected boolean cancelled = false;

  /**
     * Create a new command with the supplied type and args.
     *
     * @param type Command type.
     * @param output Command output.
     * @param args Command args, if any.
     */
  public Command(ProtocolKeyword type, CommandOutput<K, V, T> output) {
    this(type, output, null);
  }

  /**
     * Create a new command with the supplied type and args.
     *
     * @param type Command type.
     * @param output Command output.
     * @param args Command args, if any.
     * @param multi Flag indicating if MULTI active.
     */
  public Command(ProtocolKeyword type, CommandOutput<K, V, T> output, CommandArgs<K, V> args) {
    checkArgument(type != null, "Command type must not be null");
    this.type = type;
    this.output = output;
    this.args = args;
  }

  /**
     * Check if the command has been cancelled.
     * 
     * @return True if the command was cancelled.
     */
  @Override public boolean isCancelled() {
    return cancelled;
  }

  /**
     * Get the object that holds this command's output.
     *
     * @return The command output object.
     */
  @Override public CommandOutput<K, V, T> getOutput() {
    return output;
  }

  @Override public boolean completeExceptionally(Throwable throwable) {
    if (output != null) {
      output.setError(throwable.getMessage());
    }
    exception = throwable;
    return true;
  }

  /**
     * Check if the command has completed.
     * 
     * @return true if the command has completed.
     */
  @Override public boolean isDone() {
    return completed;
  }

  /**
     * Mark this command complete and notify all waiting threads.
     */
  @Override public void complete() {
    completed = true;
  }

  @Override public void cancel() {
    cancelled = true;
  }

  /**
     * Get the command output and if the command hasn't completed yet, wait up to the specified time until it does.
     * 
     * @param timeout Maximum time to wait for a result.
     * @param unit Unit of time for the timeout.
     * 
     * @return The command output.
     * 
     * @throws TimeoutException if the wait timed out.
     */
  public T get() {
    if (output != null) {
      return output.get();
    }
    return null;
  }

  /**
     * Encode and write this command to the supplied buffer using the new <a href="http://redis.io/topics/protocol">Unified
     * Request Protocol</a>.
     *
     * @param buf Buffer to write to.
     */
  public void encode(ByteBuf buf) {
    buf.writeByte('*');
    writeInt(buf, 1 + (args != null ? args.count() : 0));
    buf.writeBytes(CRLF);
    buf.writeByte('$');
    writeInt(buf, type.getBytes().length);
    buf.writeBytes(CRLF);
    buf.writeBytes(type.getBytes());
    buf.writeBytes(CRLF);
    if (args != null) {
      buf.writeBytes(args.buffer());
    }
  }

  /**
     * Write the textual value of a positive integer to the supplied buffer.
     *
     * @param buf Buffer to write to.
     * @param value Value to write.
     */
  protected static void writeInt(ByteBuf buf, int value) {
    if (value < 10) {
      buf.writeByte((byte) ('0' + value));
      return;
    }
    String asString = Integer.toString(value);
    for (int i = 0; i < asString.length(); i++) {
      buf.writeByte((byte) asString.charAt(i));
    }
  }

  public String getError() {
    return output.getError();
  }

  @Override public CommandArgs<K, V> getArgs() {
    return args;
  }

  @Override public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName());
    sb.append(" [type=").append(type);
    sb.append(", output=").append(output);
    sb.append(']');
    return sb.toString();
  }

  public void setOutput(CommandOutput<K, V, T> output) {
    if (isCancelled() || completed) {
      throw new IllegalStateException("Command is completed/cancelled. Cannot set a new output");
    }
    this.output = output;
  }

  @Override public ProtocolKeyword getType() {
    return type;
  }
}