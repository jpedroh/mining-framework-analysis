package net.logstash.logback.appender;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import net.logstash.logback.encoder.SeparatorParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.Duration;

@RunWith(value = MockitoJUnitRunner.class) public class LogstashTcpSocketAppenderTest {
  private static final int VERIFICATION_TIMEOUT = 1000 * 10;

  @InjectMocks private LogstashTcpSocketAppender appender;

  @Mock private Context context;

  @Mock private StatusManager statusManager;

  @Mock private ILoggingEvent event1;

  @Mock private ILoggingEvent event2;

  @Mock private SocketFactory socketFactory;

  @Mock private Socket socket;

  @Mock private OutputStream outputStream;

  @Mock private InputStream inputStream;

  @Mock private Encoder<ILoggingEvent> encoder;

  @Before public void setup() throws IOException {
    when(context.getStatusManager()).thenReturn(statusManager);
    when(socket.getOutputStream()).thenReturn(outputStream);
  }

  @After public void tearDown() {
    appender.stop();
  }

  @Test public void testEncoderCalled() throws Exception {
    appender.addDestination("localhost:10000");
    appender.setIncludeCallerData(true);
    when(socketFactory.createSocket()).thenReturn(socket);
    appender.start();
    verify(encoder).start();
    appender.append(event1);
    verify(event1).getCallerData();
    verify(encoder, timeout(VERIFICATION_TIMEOUT)).init(any(OutputStream.class));
    verify(encoder, timeout(VERIFICATION_TIMEOUT)).doEncode(event1);
  }

  @Test public void testReconnectOnOpen() throws Exception {
    appender.addDestination("localhost:10000");
    appender.setReconnectionDelay(new Duration(100));
    when(socketFactory.createSocket()).thenThrow(new SocketTimeoutException()).thenReturn(socket);
    when(socket.getInputStream()).thenReturn(inputStream);
    final CountDownLatch latch = new CountDownLatch(1);
    when(inputStream.read()).thenAnswer(new Answer<Integer>() {
      @Override public Integer answer(InvocationOnMock invocation) throws Throwable {
        latch.await();
        return -1;
      }
    });
    appender.start();
    verify(encoder).start();
    appender.append(event1);
    verify(encoder, timeout(VERIFICATION_TIMEOUT)).init(any(OutputStream.class));
    verify(encoder, timeout(VERIFICATION_TIMEOUT)).doEncode(event1);
    latch.countDown();
  }

  @Test public void testReconnectOnWrite() throws Exception {
    appender.addDestination("localhost:10000");
    appender.setReconnectionDelay(new Duration(100));
    when(socketFactory.createSocket()).thenReturn(socket);
    when(socket.getInputStream()).thenReturn(inputStream);
    final CountDownLatch latch = new CountDownLatch(1);
    when(inputStream.read()).thenAnswer(new Answer<Integer>() {
      @Override public Integer answer(InvocationOnMock invocation) throws Throwable {
        latch.await();
        return -1;
      }
    });
    appender.start();
    verify(encoder).start();
    doThrow(new SocketException()).doNothing().when(encoder).doEncode(event1);
    appender.append(event1);
    verify(encoder, timeout(VERIFICATION_TIMEOUT).times(2)).init(any(OutputStream.class));
    verify(encoder, timeout(VERIFICATION_TIMEOUT).times(2)).doEncode(event1);
    latch.countDown();
  }

  /**
     * Scenario:
     *   Two servers: localhost:10000 (primary), localhost:10001 (secondary)
     *   Primary is available at startup
     *   Appender should connect to PRIMARY and not any secondaries
     *   
     * @throws Exception
     */
  @Test public void testConnectOnPrimary() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    when(socketFactory.createSocket()).thenReturn(socket);
    appender.start();
    verify(encoder).start();
    verify(socket, timeout(VERIFICATION_TIMEOUT).times(1)).connect(any(SocketAddress.class), anyInt());
    verify(socket).connect(host("localhost", 10000), anyInt());
  }

  @Test public void testReconnectOnReadFailure() throws Exception {
    appender.setReconnectionDelay(new Duration(100));
    when(socketFactory.createSocket()).thenReturn(socket);
    when(socket.getInputStream()).thenReturn(inputStream);
    final CountDownLatch latch = new CountDownLatch(1);
    when(inputStream.read()).thenAnswer(new Answer<Integer>() {
      @Override public Integer answer(InvocationOnMock invocation) throws Throwable {
        latch.countDown();
        return -1;
      }
    });
    appender.start();
    verify(encoder).start();
    assertThat(latch.await(VERIFICATION_TIMEOUT, TimeUnit.MILLISECONDS)).isTrue();
    appender.append(event1);
    verify(encoder, timeout(VERIFICATION_TIMEOUT).times(2)).init(any(OutputStream.class));
    verify(encoder, timeout(VERIFICATION_TIMEOUT)).doEncode(event1);
  }

  /**
     * Scenario:
     *   Two servers: localhost:10000 (primary), localhost:10001 (secondary)
     *   Primary is not available at startup
     *   Appender should first try primary then immediately connect to secondary
     *   
     * @throws Exception
     */
  @Test public void testReconnectToSecondaryOnOpen() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    when(socketFactory.createSocket()).thenReturn(socket);
    doThrow(SocketTimeoutException.class).when(socket).connect(host("localhost", 10000), anyInt());
    appender.start();
    verify(encoder).start();
    verify(socket, timeout(VERIFICATION_TIMEOUT).times(2)).connect(any(SocketAddress.class), anyInt());
    InOrder inOrder = inOrder(socket);
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket).connect(host("localhost", 10001), anyInt());
  }

  /**
     * Scenario:
     *   Two servers: localhost:10000 (primary), localhost:10001 (secondary)
     *   Primary is available at startup then fails after the first event.
     *   Appender should then connect on secondary for the next event.
     *   
     * @throws Exception
     */
  @Test public void testReconnectToSecondaryOnWrite() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    when(socketFactory.createSocket()).thenReturn(socket);
    doNothing().doThrow(SocketTimeoutException.class).when(socket).connect(host("localhost", 10000), anyInt());
    doThrow(new SocketException()).doNothing().when(encoder).doEncode(event1);
    appender.start();
    verify(encoder).start();
    appender.append(event1);
    verify(socket, timeout(VERIFICATION_TIMEOUT).times(3)).connect(any(SocketAddress.class), anyInt());
    InOrder inOrder = inOrder(socket);
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket).connect(host("localhost", 10001), anyInt());
  }

  /**
     * Make sure the appender tries to reconnect to primary after a while.
     */
  @Test public void testReconnectToPrimaryWhileOnSecondary() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    appender.setSecondaryConnectionTTL(Duration.buildByMilliseconds(100));
    when(socketFactory.createSocket()).thenReturn(socket);
    doThrow(SocketTimeoutException.class).doNothing().when(socket).connect(host("localhost", 10000), anyInt());
    appender.start();
    verify(encoder).start();
    Thread.sleep(appender.getSecondaryConnectionTTL().getMilliseconds() + 50);
    appender.append(event1);
    verify(socket, timeout(VERIFICATION_TIMEOUT).times(3)).connect(any(SocketAddress.class), anyInt());
    InOrder inOrder = inOrder(socket, encoder);
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket).connect(host("localhost", 10001), anyInt());
    inOrder.verify(encoder).doEncode(event1);
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
  }

  /**
     * When a connection failure occurs, the appender retries immediately with the next 
     * available host. When all hosts are exhausted, the appender should wait {reconnectionDelay}
     * before retrying with the first server.
     */
  @Test public void testReconnectWaitWhenExhausted() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    appender.setReconnectionDelay(Duration.buildByMilliseconds(100));
    when(socketFactory.createSocket()).thenReturn(socket);
    doThrow(SocketTimeoutException.class).doNothing().when(socket).connect(host("localhost", 10000), anyInt());
    doThrow(SocketTimeoutException.class).doNothing().when(socket).connect(host("localhost", 10001), anyInt());
    appender.start();
    verify(encoder).start();
    verify(socket, timeout(appender.getReconnectionDelay().getMilliseconds() + 50).times(3)).connect(any(SocketAddress.class), anyInt());
    InOrder inOrder = inOrder(socket, encoder);
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket).connect(host("localhost", 10001), anyInt());
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
  }

  /**
     * Schedule keep alive and make sure we got the expected amount of messages
     * in the given time.
     */
  @Test public void testKeepAlive() throws Exception {
    appender.addDestination("localhost");
    when(socketFactory.createSocket()).thenReturn(socket);
    appender.setKeepAliveMessage("UNIX");
    appender.setKeepAliveCharset(Charset.forName("UTF-8"));
    appender.setKeepAliveDuration(Duration.buildByMilliseconds(100));
    String expectedKeepAlives = SeparatorParser.parseSeparator("UNIX") + SeparatorParser.parseSeparator("UNIX");
    byte[] expectedKeepAlivesBytes = expectedKeepAlives.getBytes("UTF-8");
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(bos);
    appender.start();
    verify(encoder).start();
    Thread.sleep(250);
    Assert.assertArrayEquals(expectedKeepAlivesBytes, bos.toByteArray());
  }

  /**
     * Make sure keep alive messages trigger reconnect to another host upon failure.
     * 
     */
  @Test public void testReconnectToSecondaryOnKeepAlive() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    appender.setKeepAliveMessage("UNIX");
    appender.setKeepAliveDuration(Duration.buildByMilliseconds(100));
    when(socketFactory.createSocket()).thenReturn(socket);
    doNothing().doThrow(SocketTimeoutException.class).when(socket).connect(host("localhost", 10000), anyInt());
    doThrow(SocketException.class).doNothing().when(outputStream).write(any(byte[].class), anyInt(), anyInt());
    appender.start();
    verify(encoder).start();
    verify(socket, timeout(VERIFICATION_TIMEOUT).times(3)).connect(any(SocketAddress.class), anyInt());
    InOrder inOrder = inOrder(socket);
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket).connect(host("localhost", 10001), anyInt());
  }

  /**
     * At least one valid destination must be configured. 
     * The appender refuses to start in case of error.
     */
  @Test public void testDestination_None() throws Exception {
    appender.start();
    Assert.assertFalse(appender.isStarted());
  }

  /**
     * Specify destinations using both <remoteHost>/<port> and <destination>.
     * Only one scheme can be used - make sure the appender refuses to start.
     */
  @Test @SuppressWarnings(value = { "deprecation" }) public void testDestination_MixedType() throws Exception {
    appender.setRemoteHost("localhost");
    appender.setPort(10000);
    appender.addDestination("localhost:10001");
    appender.start();
    Assert.assertFalse(appender.isStarted());
  }

  private SocketAddress host(final String host, final int port) {
    return argThat(hasHostAndPort(host, port));
  }

  private ArgumentMatcher<SocketAddress> hasHostAndPort(final String host, final int port) {
    return new ArgumentMatcher<SocketAddress>() {
      @Override public boolean matches(Object argument) {
        InetSocketAddress sockAddr = (InetSocketAddress) argument;
        return host.equals(sockAddr.getHostName()) && port == sockAddr.getPort();
      }
    };
  }
}