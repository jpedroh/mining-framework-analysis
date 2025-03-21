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
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 *
 * @author Oliver Kleine
 */
public class CoapMessageDecoder extends OneToOneDecoder {
  private static Logger log = Logger.getLogger(CoapMessageDecoder.class.getName());

  @Override protected Object decode(ChannelHandlerContext ctx, Channel channel, Object obj) throws Exception {
    if (!(obj instanceof ChannelBuffer)) {
      return obj;
    }
    ChannelBuffer buffer = (ChannelBuffer) obj;
    if (log.isDebugEnabled()) {
      log.debug("[Message] Create new message object from ChannelBuffer");
    }
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
    if (log.isDebugEnabled()) {
      log.debug("[CoAPMessageDecoder] New Header created from ChannelBuffer (type: " + header.getMsgType() + ", code: " + header.getCode() + ", msgID: " + header.getMsgID() + ")");
    }
    try {
      OptionList optionList = decodeOptionList(buffer, optionCount, Code.getCodeFromNumber(codeNumber));
      buffer.discardReadBytes();
      CoapMessage result;
      if (header.getCode().isRequest()) {
        result = new CoapRequest(header, optionList, buffer);
      } else {
        result = new CoapResponse(header, optionList, buffer);
      }
      InetAddress rcptAddress = ((InetSocketAddress) channel.getLocalAddress()).getAddress();
      result.setRcptAdress(rcptAddress);
      if (log.isDebugEnabled()) {
        log.debug(
<<<<<<< /usr/src/app/output/okleine/ncoap/553a89b66dd92de4f155b99ca36859fc71c70738/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/encoding/CoapMessageDecoder.java/left.java
        "[CoapMessageDecoder] Recipient address is " + ((channel.getLocalAddress() + ", bound: " + channel.isBound()))
=======
        "[CoapMessageDecoder] Set receipient address to: " + rcptAddress
>>>>>>> /usr/src/app/output/okleine/ncoap/553a89b66dd92de4f155b99ca36859fc71c70738/src/main/java/de/uniluebeck/itm/spitfire/nCoap/communication/encoding/CoapMessageDecoder.java/right.java
        );
      }
      return result;
    } catch (InvalidOptionException e) {
      return null;
    }
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
     * @return An OptionList object containing the decoded options
     * @throws InvalidOptionException if a critical option is malformed, e.g. size is out of defined bounds
     * @throws ToManyOptionsException if there are too many options contained in the list
     */
  private OptionList decodeOptionList(ChannelBuffer buffer, int optionCount, Code code) throws InvalidOptionException, ToManyOptionsException {
    if (optionCount > 15) {
      throw new ToManyOptionsException("Option count of " + optionCount + " exceeds the number of allowed options");
    }
    OptionList result = new OptionList();
    int prevOptionNumber = 0;
    for (int i = 0; i < optionCount; i++) {
      try {
        Option newOption = decodeOption(buffer, prevOptionNumber);
        OptionName optionName = OptionRegistry.getOptionName(newOption.getOptionNumber());
        result.addOption(code, optionName, newOption);
        prevOptionNumber = newOption.getOptionNumber();
      } catch (InvalidOptionException e) {
        if (e.isCritical()) {
          log.error("[CoapMessageDecoder] Malformed " + e.getOptionName() + " option is critical. Send RST!");
          throw e;
        }
        if (log.isDebugEnabled()) {
          log.debug("[CoapMessageDecoder] Malformed " + e.getOptionName() + " option silently ignored.");
        }
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
     * @return The decoded Option
     * @throws InvalidOptionException if the option to be decoded is invalid
     */
  private Option decodeOption(ChannelBuffer buf, int prevOptionNumber) throws InvalidOptionException {
    byte firstByte = buf.readByte();
    OptionName optionName = OptionRegistry.getOptionName((UnsignedBytes.toInt(firstByte) >>> 4) + prevOptionNumber);
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
    if (log.isDebugEnabled()) {
      log.debug("[Option] Creating " + optionName + " option with encoded value length of " + valueLength + " bytes");
    }
    byte[] encodedValue = new byte[valueLength];
    buf.readBytes(encodedValue);
    return Option.createOption(optionName, encodedValue);
  }
}