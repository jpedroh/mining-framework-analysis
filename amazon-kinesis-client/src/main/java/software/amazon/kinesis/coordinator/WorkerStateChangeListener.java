package software.amazon.kinesis.coordinator;

/**
 * A listener for callbacks on changes worker state
 */
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


<<<<<<< Unknown file: This is a bug in JDime.
=======
  default void onInitializationFailure(Throwable e) {
  }
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/8f9f8b7033e8f195a8166628e1585484956756df/amazon-kinesis-client/src/main/java/software/amazon/kinesis/coordinator/WorkerStateChangeListener.java/right.java
}