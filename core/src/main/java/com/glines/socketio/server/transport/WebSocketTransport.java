package com.glines.socketio.server.transport;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;
import com.glines.socketio.common.ConnectionState;
import com.glines.socketio.common.DisconnectReason;
import com.glines.socketio.common.SocketIOException;
import com.glines.socketio.server.SocketIOClosedException;
import com.glines.socketio.server.SocketIOInbound;
import com.glines.socketio.server.SocketIOFrame;
import com.glines.socketio.server.SocketIOSession;
import com.glines.socketio.server.Transport;

public class WebSocketTransport extends AbstractTransport {
  public static final String TRANSPORT_NAME = "websocket";

  public static final long CONNECTION_TIMEOUT = 10 * 1000;

  private final WebSocketFactory wsFactory;

  private final long maxIdleTime;

  private class SessionWrapper implements WebSocket, SocketIOSession.SessionTransportHandler {
    private final SocketIOSession session;

    private Outbound outbound = null;

    private boolean initiated = false;

    SessionWrapper(SocketIOSession session) {
      this.session = session;
      session.setHeartbeat(maxIdleTime / 2);
      session.setTimeout(CONNECTION_TIMEOUT);
    }

    @Override public void onConnect(final Outbound outbound) {
      this.outbound = outbound;
    }

    @Override public void onFragment(boolean more, byte opcode, byte[] data, int offset, int length) {
      throw new UnsupportedOperationException();
    }

    @Override public void onDisconnect() {
      session.onShutdown();
    }

    @Override public void onMessage(byte frame, String message) {
      session.startHeartbeatTimer();
      if (!initiated) {
        if ("OPEN".equals(message)) {
          try {
            outbound.sendMessage(SocketIOFrame.encode(SocketIOFrame.FrameType.SESSION_ID, 0, session.getSessionId()));
            outbound.sendMessage(SocketIOFrame.encode(SocketIOFrame.FrameType.HEARTBEAT_INTERVAL, 0, "" + session.getHeartbeat()));
            session.onConnect(this);
            initiated = true;
          } catch (IOException e) {
            outbound.disconnect();
            session.onShutdown();
          }
        } else {
          outbound.disconnect();
          session.onShutdown();
        }
      } else {
        List<SocketIOFrame> messages = SocketIOFrame.parse(message);
        for (SocketIOFrame msg : messages) {
          session.onMessage(msg);
        }
      }
    }

    @Override public void onMessage(byte frame, byte[] data, int offset, int length) {
      try {
        onMessage(frame, new String(data, offset, length, "UTF-8"));
      } catch (UnsupportedEncodingException e) {
      }
    }

    @Override public void disconnect() {
      session.onDisconnect(DisconnectReason.DISCONNECT);
      outbound.disconnect();
    }

    @Override public void close() {
      session.startClose();
    }

    @Override public ConnectionState getConnectionState() {
      return session.getConnectionState();
    }

    @Override public void sendMessage(SocketIOFrame frame) throws SocketIOException {
      if (outbound.isOpen()) {
        Log.debug("Session[" + session.getSessionId() + "]: sendMessage: [" + frame.getFrameType() + "]: " + frame.getData());
        try {
          outbound.sendMessage(frame.encode());
        } catch (IOException e) {
          outbound.disconnect();
          throw new SocketIOException(e);
        }
      } else {
        throw new SocketIOClosedException();
      }
    }

    @Override public void sendMessage(String message) throws SocketIOException {
      sendMessage(SocketIOFrame.TEXT_MESSAGE_TYPE, message);
    }

    @Override public void sendMessage(int messageType, String message) throws SocketIOException {
      if (outbound.isOpen() && session.getConnectionState() == ConnectionState.CONNECTED) {
        sendMessage(new SocketIOFrame(SocketIOFrame.FrameType.DATA, messageType, message));
      } else {
        throw new SocketIOClosedException();
      }
    }

    @Override public void handle(HttpServletRequest request, HttpServletResponse response, SocketIOSession session) throws IOException {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unexpected request on upgraded WebSocket connection");
      return;
    }

    @Override public void disconnectWhenEmpty() {
    }

    @Override public void abort() {
      outbound.disconnect();
      outbound = null;
      session.onShutdown();
    }
  }

  public WebSocketTransport(int bufferSize, int maxIdleTime) {
    wsFactory = new WebSocketFactory();
    wsFactory.setBufferSize(bufferSize);
    wsFactory.setMaxIdleTime(maxIdleTime);
    this.maxIdleTime = maxIdleTime;
  }

  @Override public String getName() {
    return TRANSPORT_NAME;
  }

  @Override public void handle(HttpServletRequest request, HttpServletResponse response, Transport.InboundFactory inboundFactory, SocketIOSession.Factory sessionFactory) throws IOException {
    String sessionId = extractSessionId(request);
    if ("GET".equals(request.getMethod()) && sessionId == null && "WebSocket".equals(request.getHeader("Upgrade"))) {
      boolean hixie = request.getHeader("Sec-WebSocket-Key1") != null;
      String protocol = request.getHeader(hixie ? "Sec-WebSocket-Protocol" : "WebSocket-Protocol");
      if (protocol == null) {
        protocol = request.getHeader("Sec-WebSocket-Protocol");
      }
      String host = request.getHeader("Host");
      String origin = request.getHeader("Origin");
      if (origin == null) {
        origin = host;
      }
      SocketIOInbound inbound = inboundFactory.getInbound(request);
      if (inbound == null) {
        if (hixie) {
          response.setHeader("Connection", "close");
        }
        response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
      } else {
        SocketIOSession session = sessionFactory.createSession(inbound);
        SessionWrapper wrapper = new SessionWrapper(session);
        wsFactory.upgrade(request, response, wrapper, origin, protocol);
      }
    } else {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid " + TRANSPORT_NAME + " transport request");
    }
  }
}