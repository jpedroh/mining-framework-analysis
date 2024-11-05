package fr.azelart.artnetstack.utils;
import fr.azelart.artnetstack.constants.Constants;
import java.io.ByteArrayOutputStream;
import fr.azelart.artnetstack.constants.MagicNumbers;
import java.io.IOException;
import fr.azelart.artnetstack.domain.arttimecode.ArtTimeCode;
import java.net.InetAddress;
import fr.azelart.artnetstack.domain.controller.Controller;
import java.util.BitSet;
import fr.azelart.artnetstack.domain.controller.ControllerGoodInput;
import java.util.Map;
import fr.azelart.artnetstack.domain.controller.ControllerGoodOutput;
import fr.azelart.artnetstack.domain.controller.ControllerPortType;
import fr.azelart.artnetstack.domain.enums.PortInputOutputEnum;
import fr.azelart.artnetstack.domain.enums.PortTypeEnum;

/**
 * Encoder for ArtNet Packets.
 *
 * @author Corentin Azelart
 */
public final class ArtNetPacketEncoder {
  /**
	 * ArtPollCounter.
	 */
  private static volatile int artPollCounter = 1;

  /**
	 * ArtDmxCounter.
	 */
  private static volatile int artDmxCounter = 1;

  /**
	 * Private constructor to respect checkstyle and protect class.
	 */
  private ArtNetPacketEncoder() {
    super();
  }

  /**
	 * Encode an ArtPoll packet.
	 *
	 * @param controller is the controller
	 * @return the ArtPollPacket in array
	 * @throws IOException is the OutputStream have problem
	 */
  public static byte[] encodeArtPollPacket(final Controller controller) throws IOException {
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byteArrayOutputStream.write(ByteUtils.toByta(Constants.ID));
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_32);
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    byteArrayOutputStream.write(new Integer(Constants.ART_NET_VERSION).byteValue());
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_6);
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    return byteArrayOutputStream.toByteArray();
  }

  /**
	 * Encode an ArtTimeCode packet.
	 *
	 * @param artTimeCode is timecode informations
	 * @return the ArtTimeCode in array
	 * @throws IOException in error with byte array
	 */
  public static byte[] encodeArtTimeCodePacket(final ArtTimeCode artTimeCode) throws IOException {
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byteArrayOutputStream.write(ByteUtils.toByta(Constants.ID));
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    byteArrayOutputStream.write(ByteUtilsArt.in16toByte(38656));
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    byteArrayOutputStream.write(new Integer(Constants.ART_NET_VERSION).byteValue());
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(artTimeCode.getFrameTime()));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(artTimeCode.getSeconds()));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(artTimeCode.getMinutes()));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(artTimeCode.getHours()));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(artTimeCode.getArtTimeCodeType().ordinal()));
    return byteArrayOutputStream.toByteArray();
  }

  /**
	 * Encode a ArtDMX packet.
	 *
	 * @param universe is the universe
	 * @param network is the network
	 * @param dmx     is the 512 DMX parameters
	 * @return the ArtDmxCode in array
	 * @throws IOException in error with byte array
	 */
  public static byte[] encodeArtDmxPacket(final int universe, final int network, final int[] dmx) throws IOException {
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    artDmxCounter++;
    byteArrayOutputStream.write(ByteUtils.toByta(Constants.ID));
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    byteArrayOutputStream.write(ByteUtilsArt.in16toByte(20480));
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    byteArrayOutputStream.write(ByteUtilsArt.in16toBit(Constants.ART_NET_VERSION));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(artDmxCounter));
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    byteArrayOutputStream.write(universe);
    byteArrayOutputStream.write(network);
    byteArrayOutputStream.write(ByteUtilsArt.in16toBit(dmx.length));
    byte bdmx;
    for (int i = 0; i != Constants.DMX_512_SIZE; i++) {
      if (dmx.length > i) {
        bdmx = (byte) dmx[i];
        byteArrayOutputStream.write(ByteUtilsArt.in8toByte(bdmx));
      } else {
        byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
      }
    }
    return byteArrayOutputStream.toByteArray();
  }

  /**
	 * Encode an ArtPollReply packet.
	 *
	 * @param controller is the controller
	 * @param inetAdress is the address informations
	 * @param port       is the port information
	 * @return the ArtTimeCode in array
	 * @throws IOException in error with byte array
	 */
  public static byte[] encodeArtPollReplyPacket(final Controller controller, final InetAddress inetAdress, final int port) throws IOException {
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    artPollCounter++;
    byteArrayOutputStream.write(ByteUtils.toByta(Constants.ID));
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_ZERO);
    byteArrayOutputStream.write(MagicNumbers.MAGIC_NUMBER_33);
    byteArrayOutputStream.write(inetAdress.getAddress());
    byteArrayOutputStream.write(ByteUtilsArt.in16toByte(port));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(Constants.VERSION_LIB_HIGHT));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(Constants.VERSION_LIB_LOW));
    byteArrayOutputStream.write(controller.getNetwork());
    byteArrayOutputStream.write(controller.getSubNetwork());
    byteArrayOutputStream.write(ByteUtilsArt.hexStringToByteArray(("0x00ff")));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_199));
    byteArrayOutputStream.write(ByteUtils.toByta("CZ"));
    byteArrayOutputStream.write(ByteUtils.toByta(encodeString(Constants.SHORT_NAME, Constants.MAX_LENGTH_SHORT_NAME)));
    byteArrayOutputStream.write(ByteUtils.toByta(encodeString(Constants.LONG_NAME, Constants.MAX_LENGTH_LONG_NAME)));
    final int vArtPollCounter = artPollCounter + 1;
    final StringBuffer nodeReport = new StringBuffer();
    nodeReport.append("#").append("0x0000");
    nodeReport.append("[").append(vArtPollCounter).append("]");
    nodeReport.append("ok");
    byteArrayOutputStream.write(ByteUtils.toByta(encodeString(nodeReport.toString(), Constants.MAX_LENGTH_NODE_REPORT)));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_4));
    final Map<Integer, ControllerPortType> portsTypesMap = controller.getPortTypeMap();
    ControllerPortType controlerPortType = null;
    BitSet bitSet = null;
    for (int i = 0; i != Constants.MAX_PORT; i++) {
      controlerPortType = portsTypesMap.get(i);
      if (controlerPortType == null) {
        byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
      } else {
        bitSet = new BitSet(MagicNumbers.MAGIC_NUMBER_BITSET);
        if (controlerPortType.getType().equals(PortTypeEnum.DMX512)) {
          bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_4, false);
        } else {
          if (controlerPortType.getType().equals(PortTypeEnum.MIDI)) {
            bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_3, false);
            bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_4, true);
          } else {
            if (controlerPortType.getType().equals(PortTypeEnum.AVAB)) {
              bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_2, false);
              bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_3, true);
              bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_4, false);
            } else {
              if (controlerPortType.getType().equals(PortTypeEnum.COLORTRANCMX)) {
                bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_2, false);
                bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_3, MagicNumbers.MAGIC_NUMBER_BIT_4, true);
              } else {
                if (controlerPortType.getType().equals(PortTypeEnum.ADB)) {
                  bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_1, false);
                  bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_2, true);
                  bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_3, MagicNumbers.MAGIC_NUMBER_BIT_4, false);
                } else {
                  if (controlerPortType.getType().equals(PortTypeEnum.ARTNET)) {
                    bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_1, false);
                    bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_2, true);
                    bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_3, false);
                    bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_4, true);
                  }
                }
              }
            }
          }
        }
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_5, controlerPortType.isInputArtNet());
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_6, controlerPortType.isOutputArtNet());
        byteArrayOutputStream.write(toByteArray(bitSet));
      }
    }
    final Map<Integer, ControllerGoodInput> portsGoodInputsMap = controller.getGoodInputMapping();
    ControllerGoodInput controlerGoodInput = null;
    for (int i = 0; i != Constants.MAX_PORT; i++) {
      controlerGoodInput = portsGoodInputsMap.get(i);
      if (controlerGoodInput == null) {
        byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
      } else {
        bitSet = new BitSet(MagicNumbers.MAGIC_NUMBER_BITSET);
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_1, false);
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_2, controlerGoodInput.getReceivedDataError());
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_3, controlerGoodInput.getDisabled());
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_4, controlerGoodInput.getIncludeDMXTextPackets());
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_5, controlerGoodInput.getIncludeDMXSIPsPackets());
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_6, controlerGoodInput.getIncludeDMXTestPackets());
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_7, controlerGoodInput.getDataReceived());
        byteArrayOutputStream.write(toByteArray(bitSet));
      }
    }
    final Map<Integer, ControllerGoodOutput> portsGoodOutputsMap = controller.getGoodOutputMapping();
    ControllerGoodOutput controlerGoodOutput = null;
    for (int i = 0; i != Constants.MAX_PORT; i++) {
      controlerGoodOutput = portsGoodOutputsMap.get(i);
      if (controlerGoodOutput == null) {
        byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
      } else {
        bitSet = new BitSet(MagicNumbers.MAGIC_NUMBER_BITSET);
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_0, MagicNumbers.MAGIC_NUMBER_BIT_1, false);
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_1, controlerGoodOutput.getMergeLTP());
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_2, controlerGoodOutput.getOutputPowerOn());
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_3, controlerGoodOutput.getOutputmergeArtNet());
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_4, controlerGoodOutput.getIncludeDMXTextPackets());
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_5, controlerGoodOutput.getIncludeDMXSIPsPackets());
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_6, controlerGoodOutput.getIncludeDMXTestPackets());
        bitSet.set(MagicNumbers.MAGIC_NUMBER_BIT_7, controlerGoodOutput.getDataTransmited());
        byteArrayOutputStream.write(toByteArray(bitSet));
      }
    }
    BitSet bitSetIn;
    BitSet bitSetOut;
    final ByteArrayOutputStream byteArrayInTempOutputStream = new ByteArrayOutputStream();
    final ByteArrayOutputStream byteArrayOutTempOutputStream = new ByteArrayOutputStream();
    for (int i = 0; i != Constants.MAX_PORT; i++) {
      controlerPortType = portsTypesMap.get(i);
      bitSetIn = new BitSet(MagicNumbers.MAGIC_NUMBER_BITSET);
      bitSetOut = new BitSet(MagicNumbers.MAGIC_NUMBER_BITSET);
      if (controlerPortType == null || controlerPortType.getDirection() == null) {
        bitSetIn.set(i, false);
        bitSetOut.set(i, false);
      } else {
        if (controlerPortType.getDirection().equals(PortInputOutputEnum.INPUT)) {
          bitSetIn.set(i, true);
        } else {
          if (controlerPortType.getDirection().equals(PortInputOutputEnum.OUTPUT)) {
            bitSetOut.set(i, true);
          } else {
            if (controlerPortType.getDirection().equals(PortInputOutputEnum.BOTH)) {
              bitSetIn.set(i, true);
              bitSetOut.set(i, true);
            } else {
              bitSetIn.set(i, false);
              bitSetOut.set(i, false);
            }
          }
        }
      }
      if (bitSetIn.isEmpty()) {
        byteArrayInTempOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
      } else {
        byteArrayInTempOutputStream.write(toByteArray(bitSetIn));
      }
      if (bitSetOut.isEmpty()) {
        byteArrayOutTempOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
      } else {
        byteArrayOutTempOutputStream.write(toByteArray(bitSetOut));
      }
    }
    byteArrayOutputStream.write(byteArrayInTempOutputStream.toByteArray());
    byteArrayOutputStream.write(byteArrayOutTempOutputStream.toByteArray());
    bitSet = new BitSet(MagicNumbers.MAGIC_NUMBER_BITSET);
    if (controller.getScreen()) {
      bitSet.set(1, true);
      byteArrayOutputStream.write(toByteArray(bitSet));
    } else {
      byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    }
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    byteArrayOutputStream.write(ByteUtilsArt.in8toByte(MagicNumbers.MAGIC_NUMBER_ZERO));
    return byteArrayOutputStream.toByteArray();
  }

  /**
	 * Encode string with finals white spaces.
	 *
	 * @param text is text
	 * @param size is max size
	 * @return the string
	 */
  private static String encodeString(final String text, final int size) {
    final StringBuffer sb = new StringBuffer();
    sb.append(text);
    for (int i = text.length(); i != size; i++) {
      sb.append(" ");
    }
    return sb.toString();
  }

  private static byte[] toByteArray(BitSet bits) {
    byte[] bytes = new byte[(bits.length() + 7) / 8];
    for (int i = 0; i < bits.length(); i++) {
      if (bits.get(i)) {
        bytes[bytes.length - i / 8 - 1] |= 1 << (i % 8);
      }
    }
    return bytes;
  }
}