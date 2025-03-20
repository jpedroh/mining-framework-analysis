package software.amazon.kinesis.retrieval;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.InitialPositionInStream;
import software.amazon.kinesis.common.InitialPositionInStreamExtended;
import software.amazon.kinesis.retrieval.fanout.FanOutConfig;

/**
 * Used by the KCL to configure the retrieval of records from Kinesis.
 */
@Data @Accessors(fluent = true) public class RetrievalConfig {
  /**
     * User agent set when Amazon Kinesis Client Library makes AWS requests.
     */
  public static final String KINESIS_CLIENT_LIB_USER_AGENT = "amazon-kinesis-client-library-java";

  public static final String KINESIS_CLIENT_LIB_USER_AGENT_VERSION = 
<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/84d135d65e268226831de22f40f1c041b11ac0f7/amazon-kinesis-client/src/main/java/software/amazon/kinesis/retrieval/RetrievalConfig.java/left.java
  "2.1.4"
=======
  "2.2.0"
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/84d135d65e268226831de22f40f1c041b11ac0f7/amazon-kinesis-client/src/main/java/software/amazon/kinesis/retrieval/RetrievalConfig.java/right.java
  ;

  /**
     * Client used to make calls to Kinesis for records retrieval
     */
  @NonNull private final KinesisAsyncClient kinesisClient;

  /**
     * The name of the stream to process records from.
     */
  @NonNull private final String streamName;

  @NonNull private final String applicationName;

  /**
     * Backoff time between consecutive ListShards calls.
     *
     * <p>
     * Default value: 1500L
     * </p>
     */
  private long listShardsBackoffTimeInMillis = 1500L;

  /**
     * Max number of retries for ListShards when throttled/exception is thrown.
     *
     * <p>
     * Default value: 50
     * </p>
     */
  private int maxListShardsRetryAttempts = 50;

  /**
     * The location in the shard from which the KinesisClientLibrary will start fetching records from
     * when the application starts for the first time and there is no checkpoint for the shard.
     *
     * <p>
     * Default value: {@link InitialPositionInStream#LATEST}
     * </p>
     */
  private InitialPositionInStreamExtended initialPositionInStreamExtended = InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.LATEST);

  private RetrievalSpecificConfig retrievalSpecificConfig;

  private RetrievalFactory retrievalFactory;

  public RetrievalFactory retrievalFactory() {
    if (retrievalFactory == null) {
      if (retrievalSpecificConfig == null) {
        retrievalSpecificConfig = new FanOutConfig(kinesisClient()).streamName(streamName()).applicationName(applicationName());
      }
      retrievalFactory = retrievalSpecificConfig.retrievalFactory();
    }
    return retrievalFactory;
  }
}