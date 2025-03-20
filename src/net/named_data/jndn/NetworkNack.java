package net.named_data.jndn;
import net.named_data.jndn.lp.LpPacket;

/**
 * NetworkNack represents a network Nack packet and includes a Nack reason.
 */
public class NetworkNack {
  public static enum Reason {
    NONE(0),
    CONGESTION(50),
    OTHER_CODE(0x7fff),
    DUPLICATE(100),
    NO_ROUTE(150)
    ;

    Reason(int type) {
      type_ = type;
    }

    public final int getNumericType() {
      return type_;
    }

    private final int type_;
  }

  /**
   * Get the network Nack reason.
   * @return The reason enum value. If this is Reason.OTHER_CODE, then call
   * getOtherReasonCode() to get the unrecognized reason code.
   */
  public Reason getReason() {
    return reason_;
  }

  /**
   * Get the reason code from the packet which is other than a recognized
   * Reason enum value. This is only meaningful if getReason() is
   * Reason.OTHER_CODE.
   * @return The reason code.
   */
  public int getOtherReasonCode() {
    return otherReasonCode_;
  }

  /**
   * Set the network Nack reason.
   * @param reason The network Nack reason enum value. If the packet's reason
   * code is not a recognized Reason enum value, use Reason.OTHER_CODE and call
   * setOtherReasonCode().
   */
  public void setReason(Reason reason) {
    reason_ = reason;
  }

  /**
   * Set the packet's reason code to use when the reason enum is
   * Reason.OTHER_CODE. If the packet's reason code is a recognized enum value,
   * just call setReason().
   * @param otherReasonCode The packet's unrecognized reason code.
   */
  public void setOtherReasonCode(int otherReasonCode) {
    if (otherReasonCode < 0) {
      throw new Error("NetworkNack other reason code must be non-negative");
    }
    otherReasonCode_ = otherReasonCode;
  }

  /**
   * Get the first header field in lpPacket which is a NetworkNack. This is
   * an internal method which the application normally would not use.
   * @param lpPacket The LpPacket with the header fields to search.
   * @return The first NetworkNack header field, or null if not found.
   */
  static public NetworkNack getFirstHeader(LpPacket lpPacket) {
    for (int i = 0; i < lpPacket.countHeaderFields(); ++i) {
      Object field = lpPacket.getHeaderField(i);
      if (field instanceof NetworkNack) {
        return (NetworkNack) field;
      }
    }
    return null;
  }

  private Reason reason_ = Reason.NONE;

  private int otherReasonCode_ = -1;
}