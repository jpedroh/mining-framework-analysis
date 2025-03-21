package com.glines.socketio.server;
import javax.servlet.http.HttpServletRequest;
import com.glines.socketio.common.DisconnectReason;

public interface SocketIOInbound {
  /**
	 * Called when the connection is established. This will only ever be called once.
	 * @param outbound The SocketOutbound associated with the connection
	 */
  void onConnect(SocketIOOutbound outbound);

  /**
	 * Called when the socket connection is closed. This will only ever be called once.
	 * This method may be called instead of onConnect() if the connection handshake isn't
	 * completed successfully.
	 * @param reason The reason for the disconnect.
	 * @param errorMessage Possibly non null error message associated with the reason for disconnect.
	 */
  void onDisconnect(DisconnectReason reason, String errorMessage);

  /**
	 * Called one per arriving message.
	 * @param messageType
	 * @param message
	 * @param parseError
	 */
  void onMessage(int messageType, String message);
}