package com.digi.xbee.api;
import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.packet.common.TransmitStatusPacket;
import com.digi.xbee.api.packet.raw.TX16Packet;
import com.digi.xbee.api.packet.raw.TX64Packet;
import com.digi.xbee.api.packet.raw.TXStatusPacket;

@RunWith(value = PowerMockRunner.class) @PrepareForTest(value = { XBeeDevice.class }) public class SendSerialDataTest {
  private static final XBee16BitAddress XBEE_16BIT_ADDRESS = new XBee16BitAddress("0123");

  private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");

  private static final String SEND_DATA = "data";

  private static final byte[] SEND_DATA_BYTES = SEND_DATA.getBytes();

  private static final String SEND_XBEE_PACKET_METHOD = "sendXBeePacket";

  private SerialPortRxTx mockedPort;

  private XBeeDevice xbeeDevice;

  private XBeeDevice mockedDevice;

  private TX16Packet tx16Packet;

  private TX64Packet tx64Packet;

  private TXStatusPacket txStatusSuccess;

  private TXStatusPacket txStatusError;

  private TransmitPacket transmitPacket;

  private TransmitStatusPacket transmitStatusSuccess;

  private TransmitStatusPacket transmitStatusError;

  @Before public void setup() throws Exception {
    mockedPort = Mockito.mock(SerialPortRxTx.class);
    Mockito.when(mockedPort.isOpen()).thenReturn(true);
    xbeeDevice = PowerMockito.spy(new XBeeDevice(mockedPort));
    tx16Packet = Mockito.mock(TX16Packet.class);
    tx64Packet = Mockito.mock(TX64Packet.class);
    txStatusSuccess = Mockito.mock(TXStatusPacket.class);
    Mockito.when(txStatusSuccess.getTransmitStatus()).thenReturn(XBeeTransmitStatus.SUCCESS);
    txStatusError = Mockito.mock(TXStatusPacket.class);
    Mockito.when(txStatusError.getTransmitStatus()).thenReturn(XBeeTransmitStatus.ADDRESS_NOT_FOUND);
    transmitPacket = Mockito.mock(TransmitPacket.class);
    transmitStatusSuccess = Mockito.mock(TransmitStatusPacket.class);
    Mockito.when(transmitStatusSuccess.getTransmitStatus()).thenReturn(XBeeTransmitStatus.SUCCESS);
    transmitStatusError = Mockito.mock(TransmitStatusPacket.class);
    Mockito.when(transmitStatusError.getTransmitStatus()).thenReturn(XBeeTransmitStatus.ADDRESS_NOT_FOUND);
    mockedDevice = Mockito.mock(XBeeDevice.class);
    Mockito.when(mockedDevice.get64BitAddress()).thenReturn(XBEE_64BIT_ADDRESS);
    PowerMockito.whenNew(TX16Packet.class).withAnyArguments().thenReturn(tx16Packet);
    PowerMockito.whenNew(TX64Packet.class).withAnyArguments().thenReturn(tx64Packet);
    PowerMockito.whenNew(TransmitPacket.class).withAnyArguments().thenReturn(transmitPacket);
  }

  /**
	 * Verify that serial data is considered successfully sent when the received TxStatus packet 
	 * contains a SUCCESS status. In this test case the protocol of the XBee device is 802.15.4 
	 * and the test is executed using all the different addressing parameters.
	 * 
	 * @throws Exception
	 */
  @Test public void testSendSerialData802Success() throws XBeeException, IOException {
    Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
    PowerMockito.doReturn(txStatusSuccess).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx16Packet), Mockito.anyBoolean());
    PowerMockito.doReturn(txStatusSuccess).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx64Packet), Mockito.anyBoolean());
    xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
    xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
    xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
  }

  /**
	 * Verify that we receive a {@code NullPointerException} when either the address or the 
	 * data to be sent are null.
	 */
  @Test public void testSendSerialDataInvalidParams() {
    try {
      xbeeDevice.sendSerialData((XBee16BitAddress) null, SEND_DATA_BYTES);
      fail("Serial data shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(NullPointerException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData((XBee64BitAddress) null, SEND_DATA_BYTES);
      fail("Serial data shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(NullPointerException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData((XBeeDevice) null, SEND_DATA_BYTES);
      fail("Serial data shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(NullPointerException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, null);
      fail("Serial data shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(NullPointerException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, null);
      fail("Serial data shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(NullPointerException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, null);
      fail("Serial data shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(NullPointerException.class, e.getClass());
    }
  }


<<<<<<< /usr/src/app/output/digidotcom/xbeejavalibrary/b93d1fb5307232991a8db9db216ae99239edf483/test/com/digi/xbee/api/SendSerialDataTest.java/left.java
  /**
	 * Verify that serial data send fails when the received TxStatus packet contains a status different 
	 * than SUCCESS. In this test case the protocol of the XBee device is 802.15.4 and the test is 
	 * executed using all the different addressing parameters.
	 * 
	 * @throws Exception
	 */
  @Test public void testSendSerialData802Error() throws Exception {
    Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
    PowerMockito.doReturn(txStatusError).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, tx16Packet, true);
    PowerMockito.doReturn(txStatusError).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, tx64Packet, true);
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Tx16 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TransmitException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Tx64 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TransmitException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TransmitException.class, e.getClass());
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
	 * Verify that we receive an interface not open exception when the device is not open and 
	 * we try to send the serial data.
	 * 
	 * @throws Exception
	 */
  @Test public void testSendSerialDataConnectionClosed() throws Exception {
    Mockito.when(mockedPort.isOpen()).thenReturn(false);
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Serial data shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(InterfaceNotOpenException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Serial data shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(InterfaceNotOpenException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
      fail("Serial data frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(InterfaceNotOpenException.class, e.getClass());
    }
  }

  /**
	 * Verify that serial data send fails when the received TxStatus packet contains a status different 
	 * than SUCCESS. In this test case the protocol of the XBee device is 802.15.4 and the test is 
	 * executed using all the different addressing parameters.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
  @Test public void testSendSerialData802TxStatusError() throws XBeeException, IOException {
    Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
    Mockito.doReturn(txStatusError).when(xbeeDevice).sendXBeePacket(tx16Packet);
    Mockito.doReturn(txStatusError).when(xbeeDevice).sendXBeePacket(tx64Packet);
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Tx16 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TransmitException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Tx64 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TransmitException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
      fail("Tx64 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TransmitException.class, e.getClass());
    }
  }

  /**
	 * Verify that serial data send fails when the operating mode is AT. In this test case the 
	 * protocol of the XBee device is 802.15.4 and the test is executed using all the different 
	 * addressing parameters.
	 */
  @Test public void testSendSerialData802InvalidOperatingMode() {
    Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.AT);
    Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Tx16 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(InvalidOperatingModeException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Tx64 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(InvalidOperatingModeException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
      fail("Tx64 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(InvalidOperatingModeException.class, e.getClass());
    }
  }

  /**
	 * Verify that we receive a timeout exception when there is a timeout trying to send the 
	 * serial data. In this test case the protocol of the XBee device is 802.15.4 and the test 
	 * is executed using all the different addressing parameters.
	 * 
	 * @throws Exception
	 */
  @Test public void testSendSerialData802Timeout() throws XBeeException, IOException {
    Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
    PowerMockito.doThrow(new TimeoutException()).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx16Packet), Mockito.anyBoolean());
    PowerMockito.doThrow(new TimeoutException()).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx64Packet), Mockito.anyBoolean());
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Tx16 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TimeoutException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Tx64 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TimeoutException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
      fail("Tx64 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TimeoutException.class, e.getClass());
    }
  }

  /**
	 * Verify that serial data send fails (XBee exception thrown) when the {@code sendXBeePacket} 
	 * method throws an IO exception. In this test case the protocol of the XBee device is 802.15.4 
	 * and the test is executed using all the different addressing parameters.
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
  @Test public void testSendSerialData802IOError() throws XBeeException, IOException {
    Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
    Mockito.doThrow(new IOException()).when(xbeeDevice).sendXBeePacket(tx16Packet);
    Mockito.doThrow(new IOException()).when(xbeeDevice).sendXBeePacket(tx64Packet);
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Tx16 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(XBeeException.class, e.getClass());
      assertEquals(IOException.class, e.getCause().getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Tx64 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(XBeeException.class, e.getClass());
      assertEquals(IOException.class, e.getCause().getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
      fail("Tx64 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(XBeeException.class, e.getClass());
      assertEquals(IOException.class, e.getCause().getClass());
    }
  }

  /**
	 * Verify that serial data is considered successfully sent when the received TxStatus packet 
	 * contains a SUCCESS status. In this test case the protocol of the XBee device is ZigBee 
	 * (other protocols but 802.15.4 behave the same way) and the test is executed using all the 
	 * different addressing parameters.
	 * 
	 * @throws Exception
	 */
  @Test public void testSendSerialDataOtherProtocolsSuccess() throws XBeeException, IOException {
    Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
    PowerMockito.doReturn(transmitStatusSuccess).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket), Mockito.anyBoolean());
    xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
    xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
    xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
  }


<<<<<<< /usr/src/app/output/digidotcom/xbeejavalibrary/b93d1fb5307232991a8db9db216ae99239edf483/test/com/digi/xbee/api/SendSerialDataTest.java/left.java
  /**
	 * Verify that serial data send fails when the received TxStatus packet contains a status different 
	 * than SUCCESS. In this test case the protocol of the XBee device is ZigBee (other protocols 
	 * but 802.15.4 behave the same way) and the test is  executed using all the different addressing 
	 * parameters.
	 * 
	 * @throws Exception
	 */
  @Test public void testSendSerialDataOtherProtocolsError() throws Exception {
    Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
    PowerMockito.doReturn(transmitStatusError).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket), Mockito.anyBoolean());
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Tx16 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TransmitException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
      fail("Tx64 frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TransmitException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TransmitException.class, e.getClass());
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
	 * Verify that serial data send fails when the operating mode is AT. In this test case 
	 * the protocol of the XBee device is ZigBee (other protocols but 802.15.4 behave the 
	 * same way) and the test is executed using all the different addressing parameters.
	 */
  @Test public void testSendSerialDataOtherProtocolsInvalidOperatingMode() {
    Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.AT);
    Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(InvalidOperatingModeException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(InvalidOperatingModeException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(InvalidOperatingModeException.class, e.getClass());
    }
  }

  /**
	 * Verify that serial data send fails when the received TxStatus packet contains a status different 
	 * than SUCCESS. In this test case the protocol of the XBee device is ZigBee (other protocols 
	 * but 802.15.4 behave the same way) and the test is  executed using all the different addressing 
	 * parameters.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
  @Test public void testSendSerialDataOtherProtocolsTxStatusError() throws Exception {
    Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
    Mockito.doReturn(transmitStatusError).when(xbeeDevice).sendXBeePacket(transmitPacket);
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TransmitException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TransmitException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TransmitException.class, e.getClass());
    }
  }

  /**
	 * Verify that we receive a timeout exception when there is a timeout trying to send the 
	 * serial data. In this test case the protocol of the XBee device is ZigBee (other protocols 
	 * but 802.15.4 behave the same way) and the test is executed using all the different 
	 * addressing parameters.
	 * 
	 * @throws Exception
	 */
  @Test public void testSendSerialDataOtherProtocolsTimeout() throws XBeeException, IOException {
    Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
    PowerMockito.doThrow(new TimeoutException()).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket), Mockito.anyBoolean());
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TimeoutException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TimeoutException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(TimeoutException.class, e.getClass());
    }
  }

  /**
	 * Verify that serial data send fails (XBee exception thrown) when the {@code sendXBeePacket} 
	 * method throws an IO exception. In this test case the protocol of the XBee device is ZigBee 
	 * (other protocols but 802.15.4 behave the same way) and the test is executed using all the 
	 * different addressing parameters.
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
  @Test public void testSendSerialDataOtherProtocolsIOError() throws XBeeException, IOException {
    Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
    Mockito.doThrow(new IOException()).when(xbeeDevice).sendXBeePacket(transmitPacket);
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(XBeeException.class, e.getClass());
      assertEquals(IOException.class, e.getCause().getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(XBeeException.class, e.getClass());
      assertEquals(IOException.class, e.getCause().getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(XBeeException.class, e.getClass());
      assertEquals(IOException.class, e.getCause().getClass());
    }
  }

  /**
	 * Verify that when trying to send serial data from a remote XBee device to a remote 
	 * XBee device, an OperationNotSupportedException is thrown.
	 */
  @Test public void testSendSerialDataFromRemoteDevices() {
    Mockito.when(xbeeDevice.isRemote()).thenReturn(true);
    try {
      xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(OperationNotSupportedException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(OperationNotSupportedException.class, e.getClass());
    }
    try {
      xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
      fail("TransmitRequest frame shouldn\'t have been sent successfully.");
    } catch (Exception e) {
      assertEquals(OperationNotSupportedException.class, e.getClass());
    }
  }
}