package com.amazonaws.services.kinesis.clientlibrary.lib.worker;

/**
 * Top level container for all the possible states a {@link ShardConsumer} can be in. The logic for creation of tasks,
 * and state transitions is contained within the {@link ConsumerState} objects.
 *
 * <h2>State Diagram</h2>
 * 
 * <pre>
 *       +-------------------+
 *       | Waiting on Parent |                               +------------------+
 *  +----+       Shard       |                               |     Shutdown     |
 *  |    |                   |          +--------------------+   Notification   |
 *  |    +----------+--------+          |  Shutdown:         |     Requested    |
 *  |               | Success           |   Requested        +-+-------+--------+
 *  |               |                   |                      |       |
 *  |        +------+-------------+     |                      |       | Shutdown:
 *  |        |    Initializing    +-----+                      |       |  Requested
 *  |        |                    |     |                      |       |
 *  |        |                    +-----+-------+              |       |
 *  |        +---------+----------+     |       | Shutdown:    | +-----+-------------+
 *  |                  | Success        |       |  Terminated  | |     Shutdown      |
 *  |                  |                |       |  Zombie      | |   Notification    +-------------+
 *  |           +------+-------------+  |       |              | |     Complete      |             |
 *  |           |     Processing     +--+       |              | ++-----------+------+             |
 *  |       +---+                    |          |              |  |           |                    |
 *  |       |   |                    +----------+              |  |           | Shutdown:          |
 *  |       |   +------+-------------+          |              \  /           |  Requested         |
 *  |       |          |                        |               \/            +--------------------+
 *  |       |          |                        |               ||
 *  |       | Success  |                        |               || Shutdown:
 *  |       +----------+                        |               ||  Terminated
 *  |                                           |               ||  Zombie
 *  |                                           |               ||
 *  |                                           |               ||
 *  |                                           |           +---++--------------+
 *  |                                           |           |   Shutting Down   |
 *  |                                           +-----------+                   |
 *  |                                                       |                   |
 *  |                                                       +--------+----------+
 *  |                                                                |
 *  |                                                                | Shutdown:
 *  |                                                                |  All Reasons
 *  |                                                                |
 *  |                                                                |
 *  |      Shutdown:                                        +--------+----------+
 *  |        All Reasons                                    |     Shutdown      |
 *  +-------------------------------------------------------+     Complete      |
 *                                                          |                   |
 *                                                          +-------------------+
 * </pre>
 */
class ConsumerStates {
  enum ShardConsumerState {
    WAITING_ON_PARENT_SHARDS(new BlockedOnParentState()),
    INITIALIZING(new InitializingState()),
    PROCESSING(new ProcessingState()),
    SHUTDOWN_REQUESTED(new ShutdownNotificationState()),
    SHUTTING_DOWN(new ShuttingDownState()),
    SHUTDOWN_COMPLETE(new ShutdownCompleteState())
    ;

    private final ConsumerState consumerState;

    ShardConsumerState(ConsumerState consumerState) {
      this.consumerState = consumerState;
    }

    public ConsumerState getConsumerState() {
      return consumerState;
    }
  }

  interface ConsumerState {
    /**
         * Creates a new task for this state using the passed in consumer to build the task. If there is no task
         * required for this state it may return a null value. {@link ConsumerState}'s are allowed to modify the
         * consumer during the execution of this method.
         * 
         * @param consumer
         *            the consumer to use build the task, or execute state.
         * @return a valid task for this state or null if there is no task required.
         */
    ITask createTask(ShardConsumer consumer);

    /**
         * Provides the next state of the consumer upon success of the task return by
         * {@link ConsumerState#createTask(ShardConsumer)}.
         * 
         * @return the next state that the consumer should transition to, this may be the same object as the current
         *         state.
         */
    ConsumerState successTransition();

    /**
         * Provides the next state of the consumer when a shutdown has been requested. The returned state is dependent
         * on the current state, and the shutdown reason.
         * 
         * @param shutdownReason
         *            the reason that a shutdown was requested
         * @return the next state that the consumer should transition to, this may be the same object as the current
         *         state.
         */
    ConsumerState shutdownTransition(ShutdownReason shutdownReason);

    /**
         * The type of task that {@link ConsumerState#createTask(ShardConsumer)} would return. This is always a valid state
         * even if createTask would return a null value.
         * 
         * @return the type of task that this state represents.
         */
    TaskType getTaskType();

    /**
         * An enumeration represent the type of this state. Different consumer states may return the same
         * {@link ShardConsumerState}.
         * 
         * @return the type of consumer state this represents.
         */
    ShardConsumerState getState();

    boolean isTerminal();
  }

  /**
     * The initial state that any {@link ShardConsumer} should start in.
     */
  static final ConsumerState INITIAL_STATE = ShardConsumerState.WAITING_ON_PARENT_SHARDS.getConsumerState();

  private static ConsumerState shutdownStateFor(ShutdownReason reason) {
    switch (reason) {
      case REQUESTED:
      return ShardConsumerState.SHUTDOWN_REQUESTED.getConsumerState();
      case TERMINATE:
      case ZOMBIE:
      return ShardConsumerState.SHUTTING_DOWN.getConsumerState();
      default:
      throw new IllegalArgumentException("Unknown reason: " + reason);
    }
  }

  static class BlockedOnParentState implements ConsumerState {
    @Override public ITask createTask(ShardConsumer consumer) {
      return new BlockOnParentShardTask(consumer.getShardInfo(), consumer.getLeaseManager(), consumer.getParentShardPollIntervalMillis());
    }

    @Override public ConsumerState successTransition() {
      return ShardConsumerState.INITIALIZING.getConsumerState();
    }

    @Override public ConsumerState shutdownTransition(ShutdownReason shutdownReason) {
      return ShardConsumerState.SHUTDOWN_COMPLETE.getConsumerState();
    }

    @Override public TaskType getTaskType() {
      return TaskType.BLOCK_ON_PARENT_SHARDS;
    }

    @Override public ShardConsumerState getState() {
      return ShardConsumerState.WAITING_ON_PARENT_SHARDS;
    }

    @Override public boolean isTerminal() {
      return false;
    }
  }

  static class InitializingState implements ConsumerState {
    @Override public ITask createTask(ShardConsumer consumer) {
      return new InitializeTask(consumer.getShardInfo(), consumer.getRecordProcessor(), consumer.getCheckpoint(), consumer.getRecordProcessorCheckpointer(), consumer.getDataFetcher(), consumer.getTaskBackoffTimeMillis(), consumer.getStreamConfig(), consumer.getGetRecordsCache());
    }

    @Override public ConsumerState successTransition() {
      return ShardConsumerState.PROCESSING.getConsumerState();
    }

    @Override public ConsumerState shutdownTransition(ShutdownReason shutdownReason) {
      return shutdownReason.getShutdownState();
    }

    @Override public TaskType getTaskType() {
      return TaskType.INITIALIZE;
    }

    @Override public ShardConsumerState getState() {
      return ShardConsumerState.INITIALIZING;
    }

    @Override public boolean isTerminal() {
      return false;
    }
  }

  static class ProcessingState implements ConsumerState {
    @Override public ITask createTask(ShardConsumer consumer) {
      return new ProcessTask(consumer.getShardInfo(), consumer.getStreamConfig(), consumer.getRecordProcessor(), consumer.getRecordProcessorCheckpointer(), consumer.getDataFetcher(), consumer.getTaskBackoffTimeMillis(), consumer.isSkipShardSyncAtWorkerInitializationIfLeasesExist(), consumer.getGetRecordsCache());
    }

    @Override public ConsumerState successTransition() {
      return ShardConsumerState.PROCESSING.getConsumerState();
    }

    @Override public ConsumerState shutdownTransition(ShutdownReason shutdownReason) {
      return shutdownReason.getShutdownState();
    }

    @Override public TaskType getTaskType() {
      return TaskType.PROCESS;
    }

    @Override public ShardConsumerState getState() {
      return ShardConsumerState.PROCESSING;
    }

    @Override public boolean isTerminal() {
      return false;
    }
  }

  static final ConsumerState SHUTDOWN_REQUEST_COMPLETION_STATE = new ShutdownNotificationCompletionState();

  static class ShutdownNotificationState implements ConsumerState {
    @Override public ITask createTask(ShardConsumer consumer) {
      return new ShutdownNotificationTask(consumer.getRecordProcessor(), consumer.getRecordProcessorCheckpointer(), consumer.getShutdownNotification(), consumer.getShardInfo());
    }

    @Override public ConsumerState successTransition() {
      return SHUTDOWN_REQUEST_COMPLETION_STATE;
    }

    @Override public ConsumerState shutdownTransition(ShutdownReason shutdownReason) {
      if (shutdownReason == ShutdownReason.REQUESTED) {
        return SHUTDOWN_REQUEST_COMPLETION_STATE;
      }
      return shutdownReason.getShutdownState();
    }

    @Override public TaskType getTaskType() {
      return TaskType.SHUTDOWN_NOTIFICATION;
    }

    @Override public ShardConsumerState getState() {
      return ShardConsumerState.SHUTDOWN_REQUESTED;
    }

    @Override public boolean isTerminal() {
      return false;
    }
  }

  static class ShutdownNotificationCompletionState implements ConsumerState {
    @Override public ITask createTask(ShardConsumer consumer) {
      return null;
    }

    @Override public ConsumerState successTransition() {
      return this;
    }

    @Override public ConsumerState shutdownTransition(ShutdownReason shutdownReason) {
      if (shutdownReason != ShutdownReason.REQUESTED) {
        return shutdownReason.getShutdownState();
      }
      return this;
    }

    @Override public TaskType getTaskType() {
      return TaskType.SHUTDOWN_NOTIFICATION;
    }

    @Override public ShardConsumerState getState() {
      return ShardConsumerState.SHUTDOWN_REQUESTED;
    }

    @Override public boolean isTerminal() {
      return false;
    }
  }

  static class ShuttingDownState implements ConsumerState {
    @Override public ITask createTask(ShardConsumer consumer) {
      return new ShutdownTask(consumer.getShardInfo(), consumer.getRecordProcessor(), consumer.getRecordProcessorCheckpointer(), consumer.getShutdownReason(), consumer.getStreamConfig().getStreamProxy(), consumer.getStreamConfig().getInitialPositionInStream(), consumer.isCleanupLeasesOfCompletedShards(), consumer.isIgnoreUnexpectedChildShards(), consumer.getLeaseManager(), consumer.getTaskBackoffTimeMillis(), consumer.getGetRecordsCache());
    }

    @Override public ConsumerState successTransition() {
      return ShardConsumerState.SHUTDOWN_COMPLETE.getConsumerState();
    }

    @Override public ConsumerState shutdownTransition(ShutdownReason shutdownReason) {
      return ShardConsumerState.SHUTDOWN_COMPLETE.getConsumerState();
    }

    @Override public TaskType getTaskType() {
      return TaskType.SHUTDOWN;
    }

    @Override public ShardConsumerState getState() {
      return ShardConsumerState.SHUTTING_DOWN;
    }

    @Override public boolean isTerminal() {
      return false;
    }
  }

  static class ShutdownCompleteState implements ConsumerState {
    @Override public ITask createTask(ShardConsumer consumer) {
      if (consumer.getShutdownNotification() != null) {
        consumer.getShutdownNotification().shutdownComplete();
      }
      return null;
    }

    @Override public ConsumerState successTransition() {
      return this;
    }

    @Override public ConsumerState shutdownTransition(ShutdownReason shutdownReason) {
      return this;
    }

    @Override public TaskType getTaskType() {
      return TaskType.SHUTDOWN_COMPLETE;
    }

    @Override public ShardConsumerState getState() {
      return ShardConsumerState.SHUTDOWN_COMPLETE;
    }

    @Override public boolean isTerminal() {
      return true;
    }
  }
}