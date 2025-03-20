/*
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

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
@Data
@Accessors(fluent = true)
public class RetrievalConfig {
    /**
     * User agent set when Amazon Kinesis Client Library makes AWS requests.
     */
    public static final String KINESIS_CLIENT_LIB_USER_AGENT = "amazon-kinesis-client-library-java";

<<<<<<< /usr/src/app/output/awslabs/amazon-kinesis-client/84f2b673f920b265ad970d1819760c623805865b/amazon-kinesis-client/src/main/java/software/amazon/kinesis/retrieval/RetrievalConfig.java/left.java
    public static final String KINESIS_CLIENT_LIB_USER_AGENT_VERSION = "2.0.3-experimental-6";
||||||| /usr/src/app/output/awslabs/amazon-kinesis-client/84f2b673f920b265ad970d1819760c623805865b/amazon-kinesis-client/src/main/java/software/amazon/kinesis/retrieval/RetrievalConfig.java/base.java
    public static final String KINESIS_CLIENT_LIB_USER_AGENT_VERSION = "2.0.3-experimental-4";
=======
    public static final String KINESIS_CLIENT_LIB_USER_AGENT_VERSION = "2.0.3-experimental-5";
>>>>>>> /usr/src/app/output/awslabs/amazon-kinesis-client/84f2b673f920b265ad970d1819760c623805865b/amazon-kinesis-client/src/main/java/software/amazon/kinesis/retrieval/RetrievalConfig.java/right.java

    /**
     * Client used to make calls to Kinesis for records retrieval
     */
    @NonNull
    private final KinesisAsyncClient kinesisClient;

    /**
     * The name of the stream to process records from.
     */
    @NonNull
    private final String streamName;

    @NonNull
    private final String applicationName;

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
    private InitialPositionInStreamExtended initialPositionInStreamExtended = InitialPositionInStreamExtended
            .newInitialPosition(InitialPositionInStream.LATEST);

    private RetrievalSpecificConfig retrievalSpecificConfig;

    private RetrievalFactory retrievalFactory;

    public RetrievalFactory retrievalFactory() {

        if (retrievalFactory == null) {
            if (retrievalSpecificConfig == null) {
                retrievalSpecificConfig = new FanOutConfig(kinesisClient).streamName(streamName())
                        .applicationName(applicationName());
            }
            retrievalFactory = retrievalSpecificConfig.retrievalFactory();
        }
        return retrievalFactory;
    }
}
