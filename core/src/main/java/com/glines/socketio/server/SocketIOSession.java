package com.glines.socketio.server;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.glines.socketio.common.ConnectionState;
import com.glines.socketio.common.DisconnectReason;
import com.glines.socketio.common.SocketIOException;

public interface SocketIOSession {
  interface Factory {
    SocketIOSession createSession(SocketIOInbound inbound);

    SocketIOSession getSession(String sessionId);
  }

  interface SessionTransportHandler extends SocketIOOutbound {
    void handle(HttpServletRequest request, HttpServletResponse response, SocketIOSession session) throws IOException;

    void sendMessage(SocketIOFrame message) throws SocketIOException;

    void disconnectWhenEmpty();

    /**
		 * Cause connection and all activity to be aborted and all resources to be released.
		 * The handler is expected to call the session's onShutdown() when it is finished.
		 * The only session method that the handler can legally call after this is onShutdown();
		 */
    void abort();
  }

  interface SessionTask {
    /**
		 * @return True if task was or was already canceled, false if the task is executing or has executed.
		 */
    boolean cancel();
  }

  String generateRandomString(int length);

  String getSessionId();

  ConnectionState getConnectionState();

  SocketIOInbound getInbound();

  SessionTransportHandler getTransportHandler();

  void setHeartbeat(long delay);

  long getHeartbeat();

  void setTimeout(long timeout);

  long getTimeout();

  void startTimeoutTimer();

  void clearTimeoutTimer();

  void startHeartbeatTimer();

  void clearHeartbeatTimer();

  /**
	 * Initiate close.
	 */
  void startClose();

  void onMessage(SocketIOFrame message);

  void onPing(String data);

  void onPong(String data);

  void onClose(String data);

  /**
	 * Schedule a task (e.g. timeout timer)
	 * @param task The task to execute after specified delay.
	 * @param delay Delay in milliseconds.
	 * @return
	 */
  SessionTask scheduleTask(Runnable task, long delay);

  /**
	 * @param handler The handler or null if the connection failed.
	 */
  void onConnect(SessionTransportHandler handler);

  /**
	 * Pass message through to contained SocketIOInbound
	 * If a timeout timer is set, then it will be reset.
	 * @param message
	 */
  void onMessage(String message);

  /**
	 * Pass disconnect through to contained SocketIOInbound and update any internal state.
	 * @param message
	 */
  void onDisconnect(DisconnectReason reason);

  /**
	 * Called by handler to report that it is done and the session can be cleaned up.
	 * If onDisconnect has not been called yet, then it will be called with DisconnectReason.ERROR.
	 */
  void onShutdown();
}