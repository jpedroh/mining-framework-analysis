package com.aerospike.client.proxy.grpc;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Log;
import com.aerospike.client.ResultCode;
import com.aerospike.proxy.client.Kvs;
import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;
import io.netty.channel.EventLoop;

/**
 * This class executes a single Aerospike API method like get, put, etc.
 * throughout its lifetime. It executes a maximum of `totalRequestsPerStream`
 * before closing the stream.
 * <p>
 * <em>NOTE</em> All methods of the stream are executed within a single
 * thread. This is implemented by
 * <ul>
 *     <li>having the channel configured to use the direct executor</li>
 *     <li>have the channel and streams associated with the channel be
 *     executed on a single event loop</li>
 * </ul>
 * <p>
 * TODO: Should the stream be closed if it has been idle for some duration?
 */
public class GrpcStream implements StreamObserver<Kvs.AerospikeResponsePayload>, Closeable {
  /**
	 * Unique stream id in the channel.
	 */
  private final int id;

  /**
	 * The event loop within which all of GrpcStream calls are executed.
	 */
  private final EventLoop eventLoop;

  /**
	 * The request observer of the stream.
	 */
  private StreamObserver<Kvs.AerospikeRequestPayload> requestObserver;

  /**
	 * Maximum number of concurrent requests that can be in-flight.
	 */
  private final int maxConcurrentRequests;

  /**
	 * Total number of requests to process in this stream for its lifetime.
	 */
  private final int totalRequestsToExecute;

  /**
	 * The executor for this stream.
	 */
  private final GrpcChannelExecutor channelExecutor;

  /**
	 * The method processed by this stream.
	 */
  private final MethodDescriptor<Kvs.AerospikeRequestPayload, Kvs.AerospikeResponsePayload> methodDescriptor;

  /**
	 * Queued calls pending execution.
	 */
  private final LinkedList<GrpcStreamingCall> pendingCalls;

  /**
	 * Map of request id to the calls executing in this stream.
	 */
  private final Map<Integer, GrpcStreamingCall> executingCalls = new HashMap<>();

  /**
	 * Is the stream closed. This variable is only accessed from the event
	 * loop thread assigned to this stream and its channel.
	 */
  private boolean isClosed = false;

  private volatile int requestsSent;

  private volatile int requestsCompleted;

  public GrpcStream(GrpcChannelExecutor channelExecutor, MethodDescriptor<Kvs.AerospikeRequestPayload, Kvs.AerospikeResponsePayload> methodDescriptor, LinkedList<GrpcStreamingCall> pendingCalls, CallOptions callOptions, int streamIndex, EventLoop eventLoop, int maxConcurrentRequests, int totalRequestsToExecute) {
    this.channelExecutor = channelExecutor;
    this.methodDescriptor = methodDescriptor;
    this.pendingCalls = pendingCalls;
    this.id = streamIndex;
    this.eventLoop = eventLoop;
    this.maxConcurrentRequests = maxConcurrentRequests;
    this.totalRequestsToExecute = totalRequestsToExecute;
    ClientCall<Kvs.AerospikeRequestPayload, Kvs.AerospikeResponsePayload> call = channelExecutor.getChannel().newCall(methodDescriptor, callOptions);
    StreamObserver<Kvs.AerospikeRequestPayload> requestObserver = ClientCalls.asyncBidiStreamingCall(call, this);
    setRequestObserver(requestObserver);
  }

  private void setRequestObserver(StreamObserver<Kvs.AerospikeRequestPayload> requestObserver) {
    this.requestObserver = requestObserver;
  }

  @Override public void onNext(Kvs.AerospikeResponsePayload aerospikeResponsePayload) {
    if (!eventLoop.inEventLoop()) {
      eventLoop.schedule(() -> onNext(aerospikeResponsePayload), 0, TimeUnit.NANOSECONDS);
      return;
    }
    int callId = aerospikeResponsePayload.getId();
    GrpcStreamingCall call;
    if (aerospikeResponsePayload.getHasNext()) {
      call = executingCalls.get(callId);
    } else {
      call = executingCalls.remove(callId);
      requestsCompleted++;
      channelExecutor.onRequestCompleted();
    }
    if (call != null && !call.isAborted()) {
      try {
        call.onNext(aerospikeResponsePayload);
      } catch (Throwable t) {
        if (aerospikeResponsePayload.getHasNext()) {
          abortCallAtServer(call, callId);
        }
      }
    }
    if (requestsCompleted >= totalRequestsToExecute) {
      requestObserver.onCompleted();
    } else {
      executeCall();
    }
  }

  private void abortCallAtServer(GrpcStreamingCall call, int callId) {
    call.markAborted();
    int requestId = requestsSent++;
    Kvs.AerospikeRequestPayload.Builder builder = Kvs.AerospikeRequestPayload.newBuilder();
    builder.setId(requestId);
    builder.setAbortRequest(Kvs.AbortRequest.newBuilder().setAbortId(callId));
    requestObserver.onNext(builder.build());
  }

  private void abortExecutingCalls(Throwable throwable) {
    isClosed = true;
    for (GrpcStreamingCall call : executingCalls.values()) {
      call.onError(throwable);
    }
    executingCalls.clear();
    channelExecutor.onStreamClosed(this);
  }

  @Override public void onError(Throwable throwable) {
    if (!eventLoop.inEventLoop()) {
      eventLoop.schedule(() -> onError(throwable), 0, TimeUnit.NANOSECONDS);
      return;
    }
    abortExecutingCalls(throwable);
  }

  @Override public void onCompleted() {
    if (!eventLoop.inEventLoop()) {
      eventLoop.schedule(this::onCompleted, 0, TimeUnit.NANOSECONDS);
      return;
    }
    abortExecutingCalls(new AerospikeException(ResultCode.SERVER_ERROR, "stream completed before all responses have been received"));
  }

  LinkedList<GrpcStreamingCall> getPendingCalls() {
    return pendingCalls;
  }

  MethodDescriptor<Kvs.AerospikeRequestPayload, Kvs.AerospikeResponsePayload> getMethodDescriptor() {
    return methodDescriptor;
  }

  int getOngoingRequests() {
    return executingCalls.size() + pendingCalls.size();
  }

  public int getId() {
    return id;
  }

  public int getRequestsCompleted() {
    return requestsCompleted;
  }

  int getMaxConcurrentRequests() {
    return maxConcurrentRequests;
  }

  int getTotalRequestsToExecute() {
    return totalRequestsToExecute;
  }

  @Override public String toString() {
    return "GrpcStream{id=" + id + ", channelExecutor=" + channelExecutor + '}';
  }

  public int getExecutedRequests() {
    return getRequestsCompleted() + getOngoingRequests();
  }

  public void executeCall() {
    if (isClosed) {
      return;
    }
    Iterator<GrpcStreamingCall> iterator = pendingCalls.iterator();
    while (iterator.hasNext()) {
      GrpcStreamingCall call = iterator.next();
      if (call.hasSendDeadlineExpired() || call.hasExpired()) {
        call.onError(new AerospikeException.Timeout(call.getPolicy(), call.getIteration()));
        iterator.remove();
      } else {
        if (requestsSent < totalRequestsToExecute && executingCalls.size() < maxConcurrentRequests) {
          execute(call);
          iterator.remove();
        } else {
        }
      }
    }
  }

  private void execute(GrpcStreamingCall call) {
    try {
      if (call.hasExpired()) {
        call.onError(new AerospikeException.Timeout(call.getPolicy(), call.getIteration()));
        return;
      }
      Kvs.AerospikeRequestPayload.Builder requestBuilder = call.getRequestBuilder();
      int requestId = requestsSent++;
      requestBuilder.setId(requestId).setIteration(call.getIteration());
      GrpcConversions.setRequestPolicy(call.getPolicy(), requestBuilder);
      Kvs.AerospikeRequestPayload requestPayload = requestBuilder.build();
      executingCalls.put(requestId, call);
      requestObserver.onNext(requestPayload);
      if (call.hasExpiry()) {
        eventLoop.schedule(() -> onCallExpired(requestId), call.nanosTillExpiry(), TimeUnit.NANOSECONDS);
      }
    } catch (Exception e) {
      call.onError(e);
    }
  }

  private void onCallExpired(int callId) {
    GrpcStreamingCall call = executingCalls.remove(callId);
    if (call == null) {
      return;
    }
    call.onError(new AerospikeException.Timeout(call.getPolicy(), call.getIteration()));
    if (!call.isSingleResponse()) {
      abortCallAtServer(call, callId);
    }
  }

  void enqueue(GrpcStreamingCall call) {
    pendingCalls.add(call);
  }

  @Override public void close() throws IOException {
    pendingCalls.forEach((call) -> {
      try {
        call.failIfNotComplete(ResultCode.CLIENT_ERROR);
      } catch (Exception e) {
        Log.error("Error shutting down " + this.getClass() + ": " + e.getMessage());
      }
    });
    pendingCalls.clear();
    executingCalls.values().forEach((call) -> {
      try {
        call.failIfNotComplete(ResultCode.CLIENT_ERROR);
      } catch (Exception e) {
        Log.error("Error shutting down " + this.getClass() + ": " + e.getMessage());
      }
    });
    executingCalls.clear();
    try {
      requestObserver.onCompleted();
    } catch (Throwable t) {
    }
  }
}