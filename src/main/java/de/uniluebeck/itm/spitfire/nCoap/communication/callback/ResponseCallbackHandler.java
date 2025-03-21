package de.uniluebeck.itm.spitfire.nCoap.communication.callback;
import com.google.common.collect.HashBasedTable;
import de.uniluebeck.itm.spitfire.nCoap.communication.internal.InternalAcknowledgementMessage;
import de.uniluebeck.itm.spitfire.nCoap.communication.internal.InternalErrorMessage;
import de.uniluebeck.itm.spitfire.nCoap.message.header.Code;
import de.uniluebeck.itm.spitfire.nCoap.message.header.MsgType;
import de.uniluebeck.itm.spitfire.nCoap.toolbox.ByteArrayWrapper;
import de.uniluebeck.itm.spitfire.nCoap.toolbox.Tools;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapRequest;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapResponse;
import de.uniluebeck.itm.spitfire.nCoap.message.options.InvalidOptionException;
import de.uniluebeck.itm.spitfire.nCoap.message.options.ToManyOptionsException;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.ExceptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * @author Oliver Kleine
 */
public class ResponseCallbackHandler extends SimpleChannelHandler {
  private static Logger log = LoggerFactory.getLogger(ResponseCallbackHandler.class.getName());

  private static ResponseCallbackHandler instance = new ResponseCallbackHandler();

  HashBasedTable<ByteArrayWrapper, InetSocketAddress, ResponseCallback> callbacks = HashBasedTable.create();

  private ResponseCallbackHandler() {
  }

  public static ResponseCallbackHandler getInstance() {
    return instance;
  }

  /**
     * This method handles downstream message events. It adds a token to outgoing requests to enable the method
     * <code>messageReceived</code> to relate incoming responses to requests.
     *
     * @param ctx The {@link ChannelHandlerContext} to relate this handler to the
     * {@link org.jboss.netty.channel.Channel}
     * @param me The {@link MessageEvent} containing the {@link de.uniluebeck.itm.spitfire.nCoap.message.CoapMessage}
     */
  @Override public void writeRequested(ChannelHandlerContext ctx, MessageEvent me) {
    if (me.getMessage() instanceof CoapRequest) {
      log.debug(
<<<<<<< /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/left.java
      " Handling downstream event!"
=======
      "CoapRequest received on downstream"
>>>>>>> /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/right.java
      );
      CoapRequest coapRequest = (CoapRequest) me.getMessage();
      if (coapRequest.getResponseCallback() != null) {
        try {
          coapRequest.setToken(TokenFactory.getInstance().getNextToken());
        } catch (InvalidOptionException e) {
          String errorMessage = "Internal CoAP error while setting token: " + e.getCause();
          log.
<<<<<<< /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/left.java
          debug(" Error while setting token.\n", e)
=======
          error(errorMessage)
>>>>>>> /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/right.java
          ;
          UpstreamMessageEvent ume = new UpstreamMessageEvent(ctx.getChannel(), new InternalErrorMessage(errorMessage, coapRequest.getToken()), me.getRemoteAddress());
          ctx.sendUpstream(ume);
          return;
        } catch (ToManyOptionsException e) {
          String errorMessage = "Internal CoAP error while setting token: " + e.getCause();
          log.
<<<<<<< /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/left.java
          debug(" Error while setting token.\n", e)
=======
          error(errorMessage)
>>>>>>> /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/right.java
          ;
          UpstreamMessageEvent ume = new UpstreamMessageEvent(ctx.getChannel(), new InternalErrorMessage(errorMessage, coapRequest.getToken()), me.getRemoteAddress());
          ctx.sendUpstream(ume);
          return;
        }

<<<<<<< /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/left.java
        log.debug(" New Confirmable Request added: \n" + "\tRemote Address: " + me.getRemoteAddress() + "\n" + "\tToken: " + Tools.toHexString(coapRequest.getToken()));
=======
>>>>>>> Unknown file: This is a bug in JDime.

        callbacks.put(new ByteArrayWrapper(coapRequest.getToken()), (InetSocketAddress) me.getRemoteAddress(), coapRequest.getResponseCallback());
        log.info("New confirmable Request added (Remote Address: " + me.getRemoteAddress() + ", Token: " + Tools.toHexString(coapRequest.getToken()));
        log.debug(
<<<<<<< /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/left.java
        " Number of registered callbacks: "
=======
        "Number of registered callbacks: "
>>>>>>> /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/right.java
         + callbacks.size());
      }
    }
    ctx.sendDownstream(me);
  }

  /**
     * This method relates incoming responses to open requests and invokes the method <code>reveiveCoapResponse</code>
     * of the client that sent the response. CoaP clients thus should implement the {@link ResponseCallback} interface
     * by extending the abstract class {@link de.uniluebeck.itm.spitfire.nCoap.application.CoapClientApplication}.
     *
     * @param ctx The {@link ChannelHandlerContext} to relate this handler to the
     * {@link org.jboss.netty.channel.Channel}
     * @param me The {@link MessageEvent} containing the {@link de.uniluebeck.itm.spitfire.nCoap.message.CoapMessage}
     */
  @Override public void messageReceived(ChannelHandlerContext ctx, MessageEvent me) {
    log.debug(" Handle Upstream Message Event.");
    log.debug(" Received message is a response: \n" + "\tRemote Address: " + me.getRemoteAddress() + "\n" + "\tToken: " + Tools.toHexString(coapResponse.getToken()));
    if (me.getMessage() instanceof CoapResponse) {
      CoapResponse coapResponse = (CoapResponse) me.getMessage();
      log.debug(
<<<<<<< /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/left.java
      " Response callback found. "
=======
      " Received message (" + coapResponse.getMessageType() + ", " + coapResponse.getCode() + ") is a response (Remote Address: " + me.getRemoteAddress() + ", Token: "
>>>>>>> /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/right.java
       + 
<<<<<<< /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/left.java
      "Invoking method receiveCoapResponse"
=======
      Tools.toHexString(coapResponse.getToken())
>>>>>>> /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/right.java
      );
      ResponseCallback callback = callbacks.remove(new ByteArrayWrapper(coapResponse.getToken()), me.getRemoteAddress());
      if (callback != null) {
        log.debug(" Received response for request with token " + Tools.toHexString(coapResponse.getToken()));
        callback.receiveResponse(coapResponse);
      }
    } else {
      if (me.getMessage() instanceof InternalAcknowledgementMessage) {
        ByteArrayWrapper token = ((InternalAcknowledgementMessage) me.getMessage()).getContent();
        ResponseCallback callback = callbacks.get(token, me.getRemoteAddress());
        if (callback != null) {
          log.debug("Received empty acknowledgement for request with token " + token.toHexString());
          callback.receiveEmptyACK();
        }
      } else {
        if (me.getMessage() instanceof InternalErrorMessage) {
          InternalErrorMessage errorMessage = (InternalErrorMessage) me.getMessage();
          ByteArrayWrapper token = new ByteArrayWrapper(errorMessage.getToken());
          ResponseCallback callback = callbacks.get(token, me.getRemoteAddress());
          if (callback != null) {
            String error = "Received internal error message for request with token " + token.toHexString() + ":\n" + errorMessage.getContent();
            log.debug(error);
            callback.receiveInternalError(error);
          }
        } else {
          ctx.sendUpstream(me);
        }
      }
    }
  }

  @Override public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
    log.debug(
<<<<<<< /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/left.java
    " Exception caught:\n"
=======
    " Exception caught:"
>>>>>>> /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/right.java
    , 
<<<<<<< /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/left.java
    e
=======
    e.getCause()
>>>>>>> /usr/src/app/output/okleine/ncoap/75c39babb6ba9d725f6efa3372406a04931ee74b/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/callback/ResponseCallbackHandler.java/right.java
    );
  }
}