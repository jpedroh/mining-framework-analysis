package org.opensky.libadsb.msgs;
import java.io.Serializable;
import org.opensky.libadsb.tools;
import org.opensky.libadsb.exceptions.BadFormatException;

/**
 * Decoder for Mode S all-call replies
 * @author Matthias Schäfer <schaefer@opensky-network.org>
 */
public class AllCallReply extends ModeSReply implements Serializable {
  private static final long serialVersionUID = -1156158096293306435L;

  private byte capabilities;

  private byte[] interrogator;

  /**
	 * @param raw_message raw all-call reply as hex string
	 * @throws BadFormatException if message is not all-call reply or 
	 * contains wrong values.
	 */
  public AllCallReply(String raw_message) throws BadFormatException {
    this(new ModeSReply(raw_message));
  }

  /**
	 * @param reply Mode S reply containing this all-call reply
	 * @throws BadFormatException if message is not all-call reply or 
	 * contains wrong values.
	 */
  public AllCallReply(ModeSReply reply) throws BadFormatException {
    super(reply);
    setType(subtype.ALL_CALL_REPLY);
    if (getDownlinkFormat() != 11) {
      throw new BadFormatException("Message is not an all-call reply!");
    }
    capabilities = getFirstField();
    interrogator = tools.xor(calcParity(), getParity());
  }

  /**
	 * @return The emitter's capabilities (see ICAO Annex 10 V4, 3.1.2.5.2.2.1)
	 */
  public byte getCapabilities() {
    return capabilities;
  }

  /**
	 * Some receivers already subtract the crc checksum
	 * from the parity field right after reception.
	 * In that case, use {@link #getParity() getParity} to get the interrogator ID.
	 * @return the interrogator ID as a 3-byte array
	 */
  public byte[] getInterrogatorID() {
    return interrogator;
  }

  public String toString() {
    return super.toString() + "\n" + "All-call Reply:\n" + "\tCapabilities:\t\t" + getCapabilities() + "\n" + "\tInterrogator:\t\t" + tools.toHexString(getInterrogatorID());
  }
}