package com.relayrides.pushy.apns;
import java.util.Date;

/**
 * <p>A deliberately-malformed push notification used to trigger a remote shutdown of an APNs connection.</p>
 * 
 * <p>The APNs gateway does not acknowledge successful push notifications, and so it is impossible to know for sure
 * when a connection can be safely closed from the client side, or the state of messages sent before the connection
 * was closed. Because the APNs gateway DOES reject bad notifications, though, we can send a known-bad notification to
 * the gateway and wait for the remote host to close the connection. In that case, we know that all notifications sent
 * before the known-bad notification have been processed and that all messages after the known-bad notification were
 * not.</p>
 * 
 * @author <a href="mailto:jon@relayrides.com">Jon Chambers</a>
 */
class KnownBadPushNotification implements ApnsPushNotification {
  public byte[] getToken() {
    return new byte[0];
  }

  public String getPayload() {
    return "";
  }

  public Date getDeliveryInvalidationTime() {
    return null;
  }

  public DeliveryPriority getPriority() {
    return DeliveryPriority.IMMEDIATE;
  }
}