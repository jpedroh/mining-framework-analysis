package ws.wamp.jawampa.transport;
import ws.wamp.jawampa.WampRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * A websocket server adapter for WAMP that integrates into a Netty pipeline.
 */
public class WampServerWebsocketHandler extends ChannelInboundHandlerAdapter {
  final String websocketPath;

  final WampRouter router;

  Serialization serialization = Serialization.Invalid;

  boolean handshakeInProgress = false;

  public WampServerWebsocketHandler(String websocketPath, WampRouter router) {
    this.websocketPath = websocketPath;
    this.router = router;
  }

  @Override public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    FullHttpRequest request = (msg instanceof FullHttpRequest) ? (FullHttpRequest) msg : null;
    if (request != null && handshakeInProgress) {
      request.release();
      sendBadRequestAndClose(ctx, null);
      return;
    }
    if (request != null && isUpgradeRequest(request)) {
      try {
        tryWebsocketHandshake(ctx, (FullHttpRequest) msg);
      }  finally {
        request.release();
      }
    } else {
      ctx.fireChannelRead(msg);
    }
  }

  private boolean isUpgradeRequest(FullHttpRequest request) {
    if (!request.getDecoderResult().isSuccess()) {
      return false;
    }
    String connectionHeaderValue = request.headers().get(HttpHeaders.Names.CONNECTION);
    if (connectionHeaderValue == null) {
      return false;
    }
    String[] connectionHeaderFields = StringUtil.split(connectionHeaderValue.toLowerCase(), ',');
    boolean hasUpgradeField = false;
    for (String s : connectionHeaderFields) {
      if (s.trim().equals(HttpHeaders.Values.UPGRADE.toLowerCase())) {
        hasUpgradeField = true;
        break;
      }
    }
    if (!hasUpgradeField) {
      return false;
    }
    if (!request.headers().contains(HttpHeaders.Names.UPGRADE, HttpHeaders.Values.WEBSOCKET, true)) {
      return false;
    }
    return request.getUri().equals(websocketPath);
  }

  private void tryWebsocketHandshake(final ChannelHandlerContext ctx, FullHttpRequest request) {
    String wsLocation = getWebSocketLocation(ctx, request);
    WebSocketServerHandshaker handshaker = new WebSocketServerHandshakerFactory(wsLocation, WampHandlerConfiguration.WAMP_WEBSOCKET_PROTOCOLS, false, WampHandlerConfiguration.MAX_WEBSOCKET_FRAME_SIZE).newHandshaker(request);
    if (handshaker == null) {
      WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
    } else {
      handshakeInProgress = true;
      final ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), request);
      String actualProtocol = handshaker.selectedSubprotocol();
      serialization = Serialization.fromString(actualProtocol);
      if (serialization == Serialization.Invalid) {
        handshakeFuture.addListener(ChannelFutureListener.CLOSE);
        return;
      }
      ChannelHandler last = ctx.pipeline().last();
      while (last != null && last != this) {
        ctx.pipeline().removeLast();
        last = ctx.pipeline().last();
      }
      if (last == null) {
        throw new IllegalStateException("Can\'t find the WAMP server handler in the pipeline");
      }
      ProtocolHandler protocolHandler = new ProtocolHandler();
      ctx.pipeline().replace(this, "wamp-websocket-protocol-handler", protocolHandler);
      final ChannelHandlerContext protocolHandlerCtx = ctx.pipeline().context(protocolHandler);
      protocolHandlerCtx.pipeline().addLast(new WebSocketFrameAggregator(WampHandlerConfiguration.MAX_WEBSOCKET_FRAME_SIZE));
      protocolHandlerCtx.pipeline().addLast("wamp-serializer", new WampSerializationHandler(serialization));
      protocolHandlerCtx.pipeline().addLast("wamp-deserializer", new WampDeserializationHandler(serialization));
      protocolHandlerCtx.pipeline().addLast(router.eventLoop(), "wamp-router", router.createRouterHandler());
      handshakeFuture.addListener(new ChannelFutureListener() {
        @Override public void operationComplete(ChannelFuture future) throws Exception {
          if (!future.isSuccess()) {
            ctx.fireExceptionCaught(future.cause());
          } else {
            ctx.fireChannelActive();
          }
        }
      });
    }
  }

  private String getWebSocketLocation(ChannelHandlerContext ctx, FullHttpRequest req) {
    String location = req.headers().get(HOST) + websocketPath;
    if (ctx.pipeline().get(SslHandler.class) != null) {
      return "wss://" + location;
    } else {
      return "ws://" + location;
    }
  }

  @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (cause instanceof WebSocketHandshakeException) {
      sendBadRequestAndClose(ctx, cause.getMessage());
    } else {
      ctx.close();
    }
  }

  private static void sendBadRequestAndClose(ChannelHandlerContext ctx, String message) {
    FullHttpResponse response;
    if (message != null) {
      response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(message.getBytes()));
    } else {
      response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
    }
    ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  public static class ProtocolHandler extends ChannelInboundHandlerAdapter {
    enum ReadState {
      Closed,
      Reading,
      Error
    }

    ReadState readState = ReadState.Reading;

    @Override public void handlerAdded(ChannelHandlerContext ctx) {
    }

    @Override public void channelActive(ChannelHandlerContext ctx) {
      ctx.fireChannelActive();
    }

    @Override public void channelInactive(ChannelHandlerContext ctx) {
      readState = ReadState.Closed;
      ctx.fireChannelInactive();
    }

    @Override public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if (readState != ReadState.Reading) {
        ReferenceCountUtil.release(msg);
        return;
      }
      if (msg instanceof FullHttpRequest) {
        ((FullHttpRequest) msg).release();
        WampServerWebsocketHandler.sendBadRequestAndClose(ctx, null);
        return;
      }
      if (msg instanceof PingWebSocketFrame) {
        try {
          ctx.writeAndFlush(new PongWebSocketFrame());
        }  finally {
          ((PingWebSocketFrame) msg).release();
        }
      } else {
        if (msg instanceof CloseWebSocketFrame) {
          readState = ReadState.Closed;
          ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
        } else {
          ctx.fireChannelRead(msg);
        }
      }
    }

    @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      readState = ReadState.Error;
      ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
      ctx.fireExceptionCaught(cause);
    }
  }
}