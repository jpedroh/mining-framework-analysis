
<<<<<<< /usr/src/app/output/okleine/ncoap/0fe28d174e89355a76eb698dd4f13327180400a3/src/test/java/de/uniluebeck/itm/spitfire/nCoap/communication/ServerAdaptsMaxRetransmitForConNotification.java/left.java
package de.uniluebeck.itm.spitfire.nCoap.communication;
import de.uniluebeck.itm.spitfire.nCoap.communication.core.CoapServerDatagramChannelFactory;
import de.uniluebeck.itm.spitfire.nCoap.communication.utils.CoapMessageReceiver;
import de.uniluebeck.itm.spitfire.nCoap.communication.utils.CoapTestServer;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapMessage;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapRequest;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapResponse;
import de.uniluebeck.itm.spitfire.nCoap.message.header.Code;
import de.uniluebeck.itm.spitfire.nCoap.message.header.MsgType;
import de.uniluebeck.itm.spitfire.nCoap.message.options.UintOption;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedMap;
import org.junit.BeforeClass;
import org.junit.Test;
import static junit.framework.Assert.*;
import static de.uniluebeck.itm.spitfire.nCoap.message.options.OptionRegistry.OptionName.*;
import static de.uniluebeck.itm.spitfire.nCoap.communication.core.CoapServerDatagramChannelFactory.*;
import de.uniluebeck.itm.spitfire.nCoap.communication.utils.ObservableDummyWebService;
import static de.uniluebeck.itm.spitfire.nCoap.testtools.ByteTestTools.*;

/**
 * Tests if the server adapts MAX_RETRANSMIT to avoid CON timeout before Max-Age ends.
 * (Only for observe notifications)
 *
 * @author Stefan Hueske
 */
public class ServerAdaptsMaxRetransmitForConNotification {
  private static CoapTestServer testServer = CoapTestServer.getInstance();

  private static CoapMessageReceiver testReceiver = CoapMessageReceiver.getInstance();

  private static CoapRequest regRequest;

  private static CoapResponse notification1;

  private static CoapResponse notification2;

  @BeforeClass public static void init() throws Exception {
    testServer.reset();
    Thread.sleep(150);
    testReceiver.reset();
    testReceiver.setReceiveEnabled(true);
    String requestPath = "/testpath";
    URI targetUri = new URI("coap://localhost:" + CoapServerDatagramChannelFactory.COAP_SERVER_PORT + requestPath);
    regRequest = new CoapRequest(MsgType.CON, Code.GET, targetUri);
    regRequest.getHeader().setMsgID(12314);
    regRequest.setToken(new byte[] { 0x12, 0x23, 0x34 });
    regRequest.setObserveOptionRequest();
    (notification1 = new CoapResponse(Code.CONTENT_205)).setPayload("testpayload1".getBytes("UTF-8"));
    (notification2 = new CoapResponse(Code.CONTENT_205)).setPayload("testpayload2".getBytes("UTF-8"));
    notification2.setMaxAge(100);
    ObservableDummyWebService observableDummyWebService = new ObservableDummyWebService(requestPath, true, 0, 0);
    observableDummyWebService.addPreparedResponses(notification1, notification2);
    testServer.registerService(observableDummyWebService);
    testReceiver.writeMessage(regRequest, new InetSocketAddress("localhost", COAP_SERVER_PORT));
    Thread.sleep(150);
    observableDummyWebService.setResourceStatus(true);
    Thread.sleep(130 * 1000);
    CoapResponse cancelRSTmsg = new CoapResponse(MsgType.RST, Code.EMPTY);
    cancelRSTmsg.setMessageID(notification2.getMessageID());
    testReceiver.writeMessage(cancelRSTmsg, new InetSocketAddress("localhost", COAP_SERVER_PORT));
    testReceiver.setReceiveEnabled(false);
  }

  @Test public void testReceiverReceived7Messages() {
    String message = "Receiver did not receive 7 messages";
    assertEquals(message, 7, testReceiver.getReceivedMessages().values().size());
  }

  @Test public void testLast6ConRetransmissions() {
    SortedMap<Long, CoapMessage> receivedMessages = testReceiver.getReceivedMessages();
    Iterator<Long> timeKeys = receivedMessages.keySet().iterator();
    timeKeys.next();
    for (int i = 1; i < 6; i++) {
      CoapMessage receivedMessage = receivedMessages.get(timeKeys.next());
      String message = "Notification Nr. " + i + "was not of type CON";
      assertEquals(message, MsgType.CON, receivedMessage.getMessageType());
      message = "Notification Nr. " + i + "has invalid message ID";
      assertEquals(message, notification2.getMessageID(), receivedMessage.getMessageID());
      message = "Notification Nr. " + i + "has invalid payload";
      assertEquals(message, notification2.getPayload(), receivedMessage.getPayload());
    }
  }
}
=======


>>>>>>> /usr/src/app/output/okleine/ncoap/0fe28d174e89355a76eb698dd4f13327180400a3/src/test/java/de/uniluebeck/itm/spitfire/nCoap/communication/ServerAdaptsMaxRetransmitForConNotification.java/right.java
