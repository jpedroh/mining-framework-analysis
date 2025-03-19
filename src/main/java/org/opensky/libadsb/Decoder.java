package org.opensky.libadsb;
import org.opensky.libadsb.exceptions.BadFormatException;
import org.opensky.libadsb.exceptions.UnspecifiedFormatError;
import org.opensky.libadsb.msgs.AirbornePositionMsg;
import org.opensky.libadsb.msgs.AirspeedHeadingMsg;
import org.opensky.libadsb.msgs.AllCallReply;
import org.opensky.libadsb.msgs.AltitudeReply;
import org.opensky.libadsb.msgs.CommBAltitudeReply;
import org.opensky.libadsb.msgs.CommBIdentifyReply;
import org.opensky.libadsb.msgs.EmergencyOrPriorityStatusMsg;
import org.opensky.libadsb.msgs.ExtendedSquitter;
import org.opensky.libadsb.msgs.IdentificationMsg;
import org.opensky.libadsb.msgs.IdentifyReply;
import org.opensky.libadsb.msgs.LongACAS;
import org.opensky.libadsb.msgs.ModeSReply;
import org.opensky.libadsb.msgs.OperationalStatusMsg;
import org.opensky.libadsb.msgs.ShortACAS;
import org.opensky.libadsb.msgs.SurfacePositionMsg;
import org.opensky.libadsb.msgs.TCASResolutionAdvisoryMsg;
import org.opensky.libadsb.msgs.VelocityOverGroundMsg;

/**
 * General decoder for ADS-B messages
 * @author Matthias Schäfer <schaefer@opensky-network.org>
 */
public class Decoder {
  /**
	 * A easy-to-use top-down ADS-B decoder. Use msg.getType() to
	 * check the message type and then cast to the appropriate class.
	 * @param raw_message the Mode S message in hex representation
	 * @return an instance of the most specialized ModeSReply possible
	 * @throws UnspecifiedFormatError if format is not specified
	 * @throws BadFormatException if format contains error
	 */
  public static ModeSReply genericDecoder(String raw_message) throws BadFormatException, UnspecifiedFormatError {
    ModeSReply modes = new ModeSReply(raw_message);
    switch (modes.getDownlinkFormat()) {
      case 0:
      return new ShortACAS(modes);
      case 4:
      return new AltitudeReply(modes);
      case 5:
      return new IdentifyReply(modes);
      case 11:
      return new AllCallReply(modes);
      case 16:
      return new LongACAS(modes);
      case 17:
      case 18:
      ExtendedSquitter es1090 = new ExtendedSquitter(modes);
      byte ftc = es1090.getFormatTypeCode();
      if (ftc >= 1 && ftc <= 4) {
        return new IdentificationMsg(es1090);
      }
      if (ftc >= 5 && ftc <= 8) {
        return new SurfacePositionMsg(es1090);
      }
      if ((ftc >= 9 && ftc <= 18) || (ftc >= 20 && ftc <= 22)) {
        return new AirbornePositionMsg(es1090);
      }
      if (ftc == 19) {
        int subtype = es1090.getMessage()[0] & 0x7;
        if (subtype == 1 || subtype == 2) {
          return new VelocityOverGroundMsg(es1090);
        } else {
          if (subtype == 3 || subtype == 4) {
            return new AirspeedHeadingMsg(es1090);
          }
        }
      }
      if (ftc == 28) {
        int subtype = es1090.getMessage()[0] & 0x7;
        if (subtype == 1) {
          return new EmergencyOrPriorityStatusMsg(es1090);
        }
        if (subtype == 2) {
          return new TCASResolutionAdvisoryMsg(es1090);
        }
      }
      if (ftc == 31) {
        int subtype = es1090.getMessage()[0] & 0x7;
        if (subtype == 0 || subtype == 1) {
          return new OperationalStatusMsg(es1090);
        }
      }
      return es1090;
      case 20:
      return new CommBAltitudeReply(modes);
      case 21:
      return new CommBIdentifyReply(modes);
      default:
      return modes;
    }
  }
}