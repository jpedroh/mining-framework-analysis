package com.aerospike.client.proxy.grpc;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import com.aerospike.proxy.client.KVSGrpc;
import com.aerospike.proxy.client.Kvs;
import com.aerospike.proxy.client.QueryGrpc;
import com.aerospike.proxy.client.ScanGrpc;

/**
 * A default gRPC stream selector which selects a free stream.
 */
public class DefaultGrpcStreamSelector implements GrpcStreamSelector {
  private final int maxConcurrentStreamsPerChannel;

  private final int maxConcurrentRequestsPerStream;

  private final int totalRequestsPerStream;

  /**
	 * Streaming calls with less than these many responses will be
	 * multiplexed on the same stream.
	 */
  private static final int LARGE_RESPONSE_CUTOFF = 10;

  public DefaultGrpcStreamSelector(int maxConcurrentStreamsPerChannel, int maxConcurrentRequestsPerStream, int totalRequestsPerStream) {
    this.maxConcurrentStreamsPerChannel = maxConcurrentStreamsPerChannel;
    this.maxConcurrentRequestsPerStream = maxConcurrentRequestsPerStream;
    this.totalRequestsPerStream = totalRequestsPerStream;
  }

  @Override public SelectedStream select(List<GrpcStream> streams, GrpcStreamingCall call) {
    final String fullMethodName = call.getStreamingMethodDescriptor().getFullMethodName();
    if (
<<<<<<< /usr/src/app/output/aerospike/aerospike-client-java/94db6c183ad87d64bc7a89f731e55f65018ceea0/proxy/src/com/aerospike/client/proxy/grpc/DefaultGrpcStreamSelector.java/left.java
    isScan(call) || isLongQuery(call) || isLargeBatch(call)
=======
    isScan(call) || isLongQuery(call)
>>>>>>> /usr/src/app/output/aerospike/aerospike-client-java/94db6c183ad87d64bc7a89f731e55f65018ceea0/proxy/src/com/aerospike/client/proxy/grpc/DefaultGrpcStreamSelector.java/right.java
    ) {
      return new SelectedStream(1, 1);
    }
    List<GrpcStream> filteredStreams = streams.stream().filter((grpcStream) -> grpcStream.getMethodDescriptor().getFullMethodName().equals(fullMethodName)).sorted(Comparator.comparingInt(GrpcStream::getId)).collect(Collectors.toList());
    for (GrpcStream stream : filteredStreams) {
      if (stream.getOngoingRequests() < stream.getMaxConcurrentRequests()) {
        return new SelectedStream(stream);
      }
    }
    if (streams.size() < maxConcurrentStreamsPerChannel) {
      return new SelectedStream(maxConcurrentRequestsPerStream, totalRequestsPerStream);
    }
    if (filteredStreams.isEmpty()) {
      return new SelectedStream(maxConcurrentRequestsPerStream, totalRequestsPerStream);
    }
    GrpcStream selected = filteredStreams.get(0);
    for (GrpcStream stream : filteredStreams) {
      float executedPercent = (float) stream.getExecutedRequests() / stream.getTotalRequestsToExecute();
      float selectedPercent = (float) selected.getExecutedRequests() / stream.getTotalRequestsToExecute();
      if (executedPercent < selectedPercent) {
        selected = stream;
      }
    }
    return new SelectedStream(selected);
  }

  private boolean isLargeBatch(GrpcStreamingCall call) {
    String fullMethodName = call.getStreamingMethodDescriptor().getFullMethodName();
    String batchFullMethodName = KVSGrpc.getBatchOperateMethod().getFullMethodName();
    String batchStreamingFullMethodName = KVSGrpc.getBatchOperateStreamingMethod().getFullMethodName();
    if (!batchFullMethodName.equals(fullMethodName) && !batchStreamingFullMethodName.equals(fullMethodName)) {
      return false;
    }
    return call.getNumExpectedResponses() < LARGE_RESPONSE_CUTOFF;
  }

  private boolean isScan(GrpcStreamingCall call) {
    String fullMethodName = call.getStreamingMethodDescriptor().getFullMethodName();
    String scanFullMethodName = ScanGrpc.getScanMethod().getFullMethodName();
    String scanStreamingFullMethodName = ScanGrpc.getScanStreamingMethod().getFullMethodName();
    return scanFullMethodName.equals(fullMethodName) || scanStreamingFullMethodName.equals(fullMethodName);
  }

  private boolean isLongQuery(GrpcStreamingCall call) {
    String fullMethodName = call.getStreamingMethodDescriptor().getFullMethodName();
    String queryFullMethodName = QueryGrpc.getQueryMethod().getFullMethodName();
    String queryStreamingFullMethodName = QueryGrpc.getQueryStreamingMethod().getFullMethodName();
    if (!queryFullMethodName.equals(fullMethodName) && !queryStreamingFullMethodName.equals(fullMethodName)) {
      return false;
    }
    Kvs.QueryRequest queryRequest = call.getRequestBuilder().getQueryRequest();
    if (queryRequest.getBackground()) {
      return false;
    }
    if (
<<<<<<< /usr/src/app/output/aerospike/aerospike-client-java/94db6c183ad87d64bc7a89f731e55f65018ceea0/proxy/src/com/aerospike/client/proxy/grpc/DefaultGrpcStreamSelector.java/left.java
    queryRequest.getStatement().getMaxRecords() < LARGE_RESPONSE_CUTOFF
=======
    queryRequest.getStatement().getMaxRecords() < 10
>>>>>>> /usr/src/app/output/aerospike/aerospike-client-java/94db6c183ad87d64bc7a89f731e55f65018ceea0/proxy/src/com/aerospike/client/proxy/grpc/DefaultGrpcStreamSelector.java/right.java
    ) {
      return false;
    }
    if (!queryRequest.getStatement().getFunctionName().isEmpty()) {
      return false;
    }
    if (queryRequest.hasQueryPolicy() && queryRequest.getQueryPolicy().getShortQuery()) {
      return false;
    }
    return true;
  }
}