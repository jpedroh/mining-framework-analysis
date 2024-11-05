package software.amazon.kinesis.coordinator;

@FunctionalInterface public interface WorkerStateChangeListener {
  enum WorkerState {
    CREATED,
    INITIALIZING,
    STARTED,
    SHUT_DOWN
  }

  void onWorkerStateChange(WorkerState newState);

  default void onInitializationFailed(Throwable e) {
  }

  default void onInitializationFailure(Throwable e) {
  }
}