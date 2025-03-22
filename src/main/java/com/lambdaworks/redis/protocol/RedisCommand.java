package com.lambdaworks.redis.protocol;
import com.lambdaworks.redis.output.CommandOutput;
import io.netty.buffer.ByteBuf;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @param <K> Key type.
 * @param <V> Value type.
 * @param <T> Output type.
 * @since 3.0
 */
public interface RedisCommand<K extends java.lang.Object, V extends java.lang.Object, T extends java.lang.Object> {
  /**
     * The command output. Can be null.
     * 
     * @return the command output.
     */
  CommandOutput<K, V, T> getOutput();

  /**
     * Complete a command.
     */
  void complete();

  /**
     * Cancel a command.
     */
  void cancel();

  /**
     * 
     * @return the current command args
     */
  CommandArgs<K, V> getArgs();

  /**
     *
     * @param throwable the exception
     * @return {@code true} if this invocation caused this CompletableFuture to transition to a completed state, else
     *         {@code false}
     */
  boolean completeExceptionally(Throwable throwable);

  /**
     *
     * @return the redis command type like {@literal SADD}, {@literal HMSET}, {@literal QUIT}.
     */
  ProtocolKeyword getType();

  /**
     * Encode the command.
     * 
     * @param buf byte buffer to operate on.
     */
  void encode(ByteBuf buf);

  /**
     *
     * @return true if the command is cancelled.
     */
  boolean isCancelled();

  /**
     *
     * @return true if the command is completed.
     */
  boolean isDone();

  /**
     * Set a new output. Only possible as long as the command is not completed/cancelled.
     * 
     * @param output the new command output
     * @throws IllegalStateException if the command is cancelled/completed
     */
  void setOutput(CommandOutput<K, V, T> output);
}