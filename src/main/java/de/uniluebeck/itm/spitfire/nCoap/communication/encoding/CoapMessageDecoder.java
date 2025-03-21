package de.uniluebeck.itm.spitfire.nCoap.communication.encoding;
import com.google.common.primitives.UnsignedBytes;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapMessage;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapRequest;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapResponse;
import de.uniluebeck.itm.spitfire.nCoap.message.header.Code;
import de.uniluebeck.itm.spitfire.nCoap.message.header.Header;
import de.uniluebeck.itm.spitfire.nCoap.message.header.InvalidHeaderException;
import de.uniluebeck.itm.spitfire.nCoap.message.header.MsgType;
import de.uniluebeck.itm.spitfire.nCoap.message.options.*;
import de.uniluebeck.itm.spitfire.nCoap.message.options.OptionRegistry.OptionName;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import static org.jboss.netty.channel.Channels.fireMessageReceived;

/**
 *
 * @author Oliver Kleine
 */
public class CoapMessageDecoder extends OneToOneDecoder {
  private static Logger log = LoggerFactory.getLogger(CoapMessageDecoder.class.getName());

  @Override public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
    try {
      super.handleUpstream(ctx, evt);
    } catch (InvalidOptionException e) {
      handleInvalidOptionException(ctx, (MessageEvent) evt, e);
    }
  }

  private void handleInvalidOptionException(ChannelHandlerContext ctx, final MessageEvent me, InvalidOptionException e) {
    log.debug("Invalid option in received message.", e);
    Header header = e.getMessageHeader();
    if (header == null) {
      log.error("This should never happen.", e);
      return;
    }
    if (header.getMsgType() == MsgType.CON) {
      try {
        CoapResponse response = new CoapResponse(MsgType.RST, Code.EMPTY, header.getMsgID());
        ChannelFuture future = Channels.future(ctx.getChannel());
        DownstreamMessageEvent dme = new DownstreamMessageEvent(ctx.getChannel(), future, response, me.getRemoteAddress());
        future.addListener(new ChannelFutureListener() {
          @Override public void operationComplete(ChannelFuture future) throws Exception {
            log.info("RST message succesfully sent to " + me.getRemoteAddress());
          }
        });
        ctx.sendDownstream(dme);
      } catch (ToManyOptionsException e1) {
        log.error("This should never happen.", e);
      } catch (InvalidHeaderException e1) {
        log.error("This should never happen.", e);
      }
    }
  }

  @Override protected Object decode(ChannelHandlerContext ctx, Channel channel, Object obj) throws Exception {
    if (!(obj instanceof ChannelBuffer)) {
      return obj;
    }
    ChannelBuffer buffer = (ChannelBuffer) obj;
    if (buffer.readableBytes() < 4) {
      String msg = "Buffer must contain at least readable 4 bytes (but has " + buffer.readableBytes() + ")";
      throw new InvalidHeaderException(msg);
    }
    int encHeader = buffer.readInt();
    int msgTypeNumber = ((encHeader << 2) >>> 30);
    int optionCount = ((encHeader << 4) >>> 28);
    int codeNumber = ((encHeader << 8) >>> 24);
    int msgID = ((encHeader << 16) >>> 16);
    Header header = new Header(MsgType.getMsgTypeFromNumber(msgTypeNumber), Code.getCodeFromNumber(codeNumber), msgID);
    log.debug("Header created: {}", header);
    OptionList optionList = decodeOptionList(buffer, optionCount, Code.getCodeFromNumber(codeNumber));
    buffer.discardReadBytes();
    CoapMessage result;
    if (header.getCode().isRequest()) {
      result = new CoapRequest(header, optionList, buffer);
      log.debug("Decoded CoapRequest.");
    } else {
      result = new CoapResponse(header, optionList, buffer);
      log.debug("Decoded CoapResponse.");
    }
    InetAddress rcptAddress = ((InetSocketAddress) channel.getLocalAddress()).getAddress();
    result.setRcptAdress(rcptAddress);
    log.debug("Set receipient address to: " + rcptAddress);

<<<<<<< /usr/src/app/output/okleine/ncoap/cedf40c2211b5e353f2f2fcfd3a209daaa715595/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/encoding/CoapMessageDecoder.java/left.java
    try {
      OptionList optionList = decodeOptionList(buffer, optionCount, Code.getCodeFromNumber(codeNumber), header);
      buffer.discardReadBytes();
      CoapMessage result;
      if (header.getCode().isRequest()) {
        result = new CoapRequest(header, optionList, buffer);
        log.debug("Decoded CoapRequest.");
      } else {
        result = new CoapResponse(header, optionList, buffer);
        log.debug("Decoded CoapResponse.");
      }
      InetAddress rcptAddress = ((InetSocketAddress) channel.getLocalAddress()).getAddress();
      result.setRcptAdress(rcptAddress);
      log.debug("Set receipient address to: " + rcptAddress);
      return result;
    } catch (InvalidOptionException e) {
      log.debug("Invalid option in received message.", e);
      return null;
    }
=======
    return result;
>>>>>>> /usr/src/app/output/okleine/ncoap/cedf40c2211b5e353f2f2fcfd3a209daaa715595/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/encoding/CoapMessageDecoder.java/right.java
  }

  /**
     * This method method creates an OptionList containing the specified number of options. It does
     * not matter whether there are more options or payload contained in the ChannelBuffer. The
     * creation process stops right after the specified number of options. This method assumes
     * the first option to begin at the current reader position of the ChannelBuffer.
     *
     * After the creation process the reader index of the ChannelBuffer points to the position
     * right after the last byte used to create the last option in the OptionList. In most
     * cases this will be the starting position of the payload (if there is any).
     *
     * Note, that eventually contained malformed but elective options will not be added to the list but will be
     * silently ignored. Malformed critical options cause an InvalidOptionException. No list will be created in the
     * latter case.
     *
     * @param buffer The ChannelBuffer containing the options to be decoded
     * @param optionCount The number of options to be decoded
     * @param code The message code of the message that is intended to include the new OptionList
     * @param header
     * @return An OptionList object containing the decoded options
     * @throws InvalidOptionException if a critical option is malformed, e.g. size is out of defined bounds
     * @throws ToManyOptionsException if there are too many options contained in the list
     */
  private OptionList decodeOptionList(ChannelBuffer buffer, int optionCount, Code code, Header header) throws InvalidOptionException, ToManyOptionsException {
    if (optionCount > 15) {
      throw new ToManyOptionsException("Option count of " + optionCount + " exceeds the number of allowed options");
    }
    OptionList result = new OptionList();
    int prevOptionNumber = 0;
    for (int i = 0; i < optionCount; i++) {
      try {
        Option newOption = decodeOption(buffer, prevOptionNumber, header);
        OptionName optionName = OptionRegistry.getOptionName(newOption.getOptionNumber());
        log.debug("Option " + optionName + " to be created.");
        result.addOption(code, optionName, newOption);
        prevOptionNumber = Math.abs(newOption.getOptionNumber());
      } catch (InvalidOptionException e) {
        if (e.isCritical()) {
          log.error("Malformed " + e.getOptionName() + " option is critical.");
          throw e;
        }
        log.debug("Malformed " + e.getOptionName() + " option silently ignored.", e);
      }
    }
    return result;
  }

  /**
     * This static methodes creates reads and decodes the Option starting at the current reader index of
     * the given ChannelBuffer. Thus, there must be an encoded option starting at the current reader index.
     * Otherwise an InvalidOptionException is thrown
     * @param buf A ChannelBuffer with its reader index at an options starting position
     * @param prevOptionNumber The option number of the previous option in the ChannelBuffer (or ZERO if there is no)
     * @param header
     * @return The decoded Option
     * @throws InvalidOptionException if the option to be decoded is invalid
     */
  private Option decodeOption(ChannelBuffer buf, int prevOptionNumber, Header header) throws InvalidOptionException {
    byte firstByte = buf.readByte();
    int optionNumber = (UnsignedBytes.toInt(firstByte) >>> 4) + prevOptionNumber;
    if (!header.getCode().isRequest() && optionNumber == OptionName.OBSERVE_REQUEST.number) {
      optionNumber = OptionName.OBSERVE_RESPONSE.number;
    }
    log.debug("Actually decoded option has number: " + optionNumber);
    OptionName optionName = OptionRegistry.getOptionName(optionNumber);
    log.debug("Actually decoded option has name:" + optionName);
    if (optionName.equals(OptionRegistry.OptionName.IF_NONE_MATCH)) {
      return Option.createEmptyOption(optionName);
    }
    int valueLength = firstByte & 0x0f;
    if (valueLength == 15) {
      valueLength = UnsignedBytes.toInt(buf.readByte()) + 15;
    }
    int minLength = OptionRegistry.getMinLength(optionName);
    int maxLength = OptionRegistry.getMaxLength(optionName);
    if (valueLength < minLength || valueLength > maxLength) {
      throw new InvalidOptionException(optionName, "[Option] " + optionName + " options must have a value length" + " between " + minLength + " and " + maxLength + " (both including) but has " + valueLength);
    }
    log.debug("Creating " + optionName + " option with encoded value length of " + valueLength + " bytes");
    byte[] encodedValue = new byte[valueLength];
    buf.readBytes(encodedValue);
    return Option.createOption(optionName, encodedValue);
  }
}