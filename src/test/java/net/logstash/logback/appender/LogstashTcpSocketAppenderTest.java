package net.logstash.logback.appender;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.function.UnaryOperator;
import javax.net.SocketFactory;
import net.logstash.logback.appender.destination.RandomDestinationConnectionStrategy;
import net.logstash.logback.appender.destination.RoundRobinDestinationConnectionStrategy;
import net.logstash.logback.appender.listener.TcpAppenderListener;
import net.logstash.logback.encoder.SeparatorParser;
import net.logstash.logback.encoder.StreamingEncoder;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.BasicStatusManager;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.EncoderBase;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationWithTimeout;

@ExtendWith(value = MockitoExtension.class) public class LogstashTcpSocketAppenderTest {
  private static final int VERIFICATION_TIMEOUT = 1000 * 10;

  @InjectMocks private final LogstashTcpSocketAppender appender = new TestableLogstashTcpSocketAppender();

  private StatusManager statusManager = new BasicStatusManager();

  @Mock private ILoggingEvent event1;

  @Mock(lenient = true) private SocketFactory socketFactory;

  @Mock(lenient = true) private Socket socket;

  @Mock private OutputStream outputStream;

  @Mock(lenient = true) private Encoder<ILoggingEvent> encoder;

  @Mock private Future<?> readableCallableFuture;

  @Mock private TcpAppenderListener<ILoggingEvent> listener;

  private class TestableLogstashTcpSocketAppender extends LogstashTcpSocketAppender {
    @Override protected Future<?> scheduleReaderCallable(Callable<Void> readerCallable) {
      return readableCallableFuture;
    }
  }

  @BeforeEach public void setup() throws IOException {
    OnConsoleStatusListener consoleListener = new OnConsoleStatusListener();
    consoleListener.start();
    statusManager.add(consoleListener);
    LoggerContext context = new LoggerContext();
    context.setStatusManager(statusManager);
    when(socketFactory.createSocket()).thenReturn(socket);
    when(socket.getOutputStream()).thenReturn(outputStream);
    when(encoder.encode(event1)).thenReturn("event1".getBytes(StandardCharsets.UTF_8));
    appender.addListener(listener);
    appender.setContext(context);
  }

  @AfterEach public void tearDown() {
    appender.stop();
  }

  @Test public void testEncoderCalled_logback12OrLater() {
    appender.addDestination("localhost:10000");
    appender.setIncludeCallerData(true);
    appender.start();
    verify(encoder).start();
    appender.append(event1);
    verify(event1).getCallerData();
    verify(encoder, async()).encode(event1);
  }

  @Test public void testReconnectOnOpen() throws Exception {
    appender.addDestination("localhost:10000");
    appender.setReconnectionDelay(Duration.buildByMilliseconds(100));
    reset(socketFactory);
    SocketTimeoutException exception = new SocketTimeoutException();
    when(socketFactory.createSocket()).thenThrow(exception).thenReturn(socket);
    appender.start();
    verify(encoder).start();
    appender.append(event1);
    verify(encoder, async()).encode(event1);
    assertThat(appender.getConnectedDestination()).isPresent();
    verify(listener).appenderStarted(appender);
    verify(listener).eventAppended(eq(appender), eq(event1), anyLong());
    verify(listener).connectionFailed(eq(appender), any(InetSocketAddress.class), eq(exception));
    verify(listener).connectionOpened(appender, socket);
    verify(listener).eventSent(eq(appender), eq(socket), eq(event1), anyLong());
    appender.stop();
    verify(listener, async()).connectionClosed(appender, socket);
    assertThat(appender.getConnectedDestination()).isNotPresent();
  }

  /**
     * Scenario:
     *   Failure to write in the connection output stream
     * 
     * Assert the appender closes the connection and reconnects
     */
  @Test public void testReconnectOnWrite() throws Exception {
    appender.addDestination("localhost:10000");
    appender.setReconnectionDelay(Duration.buildByMilliseconds(100));
    doThrow(IOException.class).doNothing().when(outputStream).write(any(byte[].class), anyInt(), anyInt());
    appender.start();
    verify(encoder).start();
    appender.append(event1);
    InOrder inOrder = inOrder(socket);
    inOrder.verify(socket, async()).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket, async()).close();
    inOrder.verify(socket, async()).connect(host("localhost", 10000), anyInt());
  }

  /**
     * Scenario:
     *   Destination closes the connection (detected by the readableCallableFuture).
     * 
     * Assert that the appender closes the socket and attempt to reconnect.
     */
  @Test public void testReconnectOnReadFailure() throws Exception {
    appender.addDestination("localhost:10000");
    appender.setReconnectionDelay(Duration.buildByMilliseconds(100));
    when(readableCallableFuture.isDone()).thenReturn(true).thenReturn(false);
    appender.start();
    verify(encoder).start();
    appender.append(event1);
    InOrder inOrder = inOrder(socket);
    inOrder.verify(socket, async()).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket, async()).close();
    inOrder.verify(socket, async()).connect(host("localhost", 10000), anyInt());
  }

  /**
     * Scenario:
     *   Two servers: localhost:10000 (primary), localhost:10001 (secondary)
     *   Primary is available at startup
     *   Appender should connect to PRIMARY and not to any secondaries
     */
  @Test public void testConnectOnPrimary() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    appender.start();
    verify(encoder).start();
    verify(listener, async()).connectionOpened(appender, socket);
    verify(socket, times(1)).connect(any(SocketAddress.class), anyInt());
    verify(socket).connect(host("localhost", 10000), anyInt());
    assertThat(appender.getConnectedDestination()).isPresent();
  }

  /**
     * Scenario:
     *   Two servers: localhost:10000 (primary), localhost:10001 (secondary)
     *   Primary is not available at startup
     *   Appender should first try primary then immediately connect to secondary
     */
  @Test public void testReconnectToSecondaryOnOpen() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    doThrow(SocketTimeoutException.class).when(socket).connect(host("localhost", 10000), anyInt());
    appender.start();
    verify(encoder).start();
    verify(socket, async().times(2)).connect(any(), anyInt());
    InOrder inOrder = inOrder(socket, listener);
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket).connect(host("localhost", 10001), anyInt());
  }

  @Test public void testRandomDestinationAndReconnectToSecondaryOnOpen() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    @SuppressWarnings(value = { "unchecked" }) UnaryOperator<Integer> randomGenerator = mock(UnaryOperator.class);
    appender.setConnectionStrategy(new RandomDestinationConnectionStrategy(randomGenerator));
    doThrow(SocketTimeoutException.class).when(socket).connect(host("localhost", 10001), anyInt());
    when(randomGenerator.apply(appender.getDestinations().size())).thenReturn(1).thenReturn(0);
    appender.start();
    verify(encoder).start();
    verify(socket, async().times(2)).connect(any(), anyInt());
    InOrder inOrder = inOrder(socket);
    inOrder.verify(socket).connect(host("localhost", 10001), anyInt());
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
  }

  /**
     * Scenario:
     *   Two servers: localhost:10000 (primary), localhost:10001 (secondary)
     *   Primary is available at startup then fails after the first event.
     *   Appender should then connect on secondary for the next event.
     */
  @Test public void testReconnectToSecondaryOnWrite() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    doThrow(IOException.class).doNothing().when(outputStream).write(any(byte[].class), anyInt(), anyInt());
    appender.start();
    verify(encoder).start();
    appender.append(event1);
    verify(socket, async().times(2)).connect(any(), anyInt());
    InOrder inOrder = inOrder(socket);
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket).close();
    inOrder.verify(socket).connect(host("localhost", 10001), anyInt());
    verify(encoder, times(2)).encode(event1);
  }

  /**
     * Make sure the appender tries to reconnect to primary after a while.
     */
  @Test public void testReconnectToPrimaryWhileOnSecondary() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    appender.setReconnectionDelay(Duration.buildByMilliseconds(1));
    appender.setSecondaryConnectionTTL(Duration.buildByMilliseconds(100));
    doThrow(SocketTimeoutException.class).doNothing().when(socket).connect(host("localhost", 10000), anyInt());
    appender.start();
    verify(encoder).start();
    Thread.sleep(appender.getSecondaryConnectionTTL().getMilliseconds() + 50);
    appender.append(event1);
    verify(socket, async().times(3)).connect(any(), anyInt());
    InOrder inOrder = inOrder(socket, encoder);
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket).connect(host("localhost", 10001), anyInt());
    inOrder.verify(encoder).encode(event1);
    inOrder.verify(socket).close();
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
    appender.setKeepAliveMessage("UNIX");
    appender.setKeepAliveCharset(StandardCharsets.UTF_8);
    appender.setKeepAliveDuration(Duration.buildByMilliseconds(100));
    String expectedKeepAlives = SeparatorParser.parseSeparator("UNIX") + SeparatorParser.parseSeparator("UNIX");
    byte[] expectedKeepAlivesBytes = expectedKeepAlives.getBytes(StandardCharsets.UTF_8);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(bos);
    appender.start();
    verify(encoder).start();
    Thread.sleep(250);
    Assertions.assertArrayEquals(expectedKeepAlivesBytes, bos.toByteArray());
  }

  @Test public void testKeepAlive_Enabled() {
    assertThat(appender.isKeepAliveEnabled()).isFalse();
    appender.setKeepAliveMessage("UNIX");
    assertThat(appender.isKeepAliveEnabled()).isFalse();
    appender.setKeepAliveDuration(null);
    assertThat(appender.isKeepAliveEnabled()).isFalse();
    appender.setKeepAliveDuration(Duration.buildByMilliseconds(-1));
    assertThat(appender.isKeepAliveEnabled()).isFalse();
    appender.setKeepAliveDuration(Duration.buildByMilliseconds(0));
    assertThat(appender.isKeepAliveEnabled()).isFalse();
    appender.setKeepAliveDuration(Duration.buildByMilliseconds(100));
    assertThat(appender.isKeepAliveEnabled()).isTrue();
  }

  @Test public void testWriteTimeout() throws Exception {
    appender.addDestination("localhost");
    appender.setWriteTimeout(Duration.buildByMilliseconds(100));
    appender.setReconnectionDelay(Duration.buildByMilliseconds(1));
    appender.setWriteBufferSize(0);
    Socket badSocket = mock(Socket.class, "badSocket");
    Socket goodSocket = mock(Socket.class, "goodSocket");
    when(socketFactory.createSocket()).thenReturn(badSocket).thenReturn(goodSocket);
    OutputStream badOutputStream = mock(OutputStream.class, "badOutputStream");
    when(badSocket.getOutputStream()).thenReturn(badOutputStream);
    CountDownLatch closeLatch = new CountDownLatch(1);
    doAnswer((invocation) -> {
      closeLatch.countDown();
      return null;
    }).when(badSocket).close();
    doAnswer((invocation) -> {
      closeLatch.await();
      throw new SocketException("socket closed");
    }).when(badOutputStream).write(any());
    OutputStream goodOutputStream = mock(OutputStream.class, "goodOutputStream");
    when(goodSocket.getOutputStream()).thenReturn(goodOutputStream);
    appender.start();
    verify(encoder).start();
    appender.append(event1);
    verify(goodOutputStream, timeout(1000)).write(any());
    verify(goodOutputStream, timeout(1000)).flush();
  }

  @Test public void testInvalidWriteTimeout() {
    assertThatThrownBy(() -> appender.setWriteTimeout(null)).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> appender.setWriteTimeout(Duration.buildByMilliseconds(-1))).isInstanceOf(IllegalArgumentException.class);
    assertThatCode(() -> appender.setWriteTimeout(Duration.buildByMilliseconds(0))).doesNotThrowAnyException();
    assertThatCode(() -> appender.setWriteTimeout(Duration.buildByMilliseconds(0))).doesNotThrowAnyException();
  }

  /**
     * Make sure keep alive messages trigger a reconnect to another host upon failure.
     */
  @Test public void testReconnectToSecondaryOnKeepAlive() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    appender.setKeepAliveMessage("UNIX");
    appender.setKeepAliveDuration(Duration.buildByMilliseconds(100));
    doThrow(IOException.class).doNothing().when(outputStream).write(any(byte[].class), anyInt(), anyInt());
    appender.start();
    verify(encoder).start();
    verify(socket, async().times(2)).connect(any(), anyInt());
    InOrder inOrder = inOrder(socket);
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket).connect(host("localhost", 10001), anyInt());
  }

  /**
     * Assert that nothing is written in the socket output stream when a *non* {@link StreamingEncoder}
     * throws an exception.
     */
  @Test public void testEncoderThrowsException() throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(bos);
    when(encoder.encode(event1)).thenThrow(new RuntimeException("Exception thrown by the Encoder"));
    appender.addDestination("localhost:10000");
    appender.start();
    appender.append(event1);
    verify(listener, async()).eventSendFailure(eq(appender), eq(event1), any());
    assertThat(bos.size()).isZero();
    assertThat(statusManager.getCopyOfStatusList()).anySatisfy((status) -> {
      assertThat(status.getLevel()).isEqualTo(Status.WARN);
      assertThat(status.getMessage()).contains("Encoder failed to encode event:");
    });
  }

  /**
     * Assert that nothing is written in the socket output stream when a {@link StreamingEncoder} throws
     * an exception after having written a few bytes.
     * 
     * Also assert that the StreamingEncoder interface is used instead of the legacy Encoder.
     */
  @Test public void testStreamingEncoderThrowsException() throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    when(socket.getOutputStream()).thenReturn(bos);
    BadStreamingEncoder badEncoder = spy(new BadStreamingEncoder());
    appender.setEncoder(badEncoder);
    appender.addDestination("localhost:10000");
    appender.start();
    appender.append(event1);
    verify(listener, async()).eventSendFailure(eq(appender), eq(event1), any());
    verify(badEncoder, times(1)).encode(eq(event1), any(OutputStream.class));
    verify(badEncoder, never()).encode(any());
    assertThat(bos.size()).isZero();
    assertThat(statusManager.getCopyOfStatusList()).anySatisfy((status) -> {
      assertThat(status.getLevel()).isEqualTo(Status.WARN);
      assertThat(status.getMessage()).contains("Encoder failed to encode event:");
    });
  }

  private static class BadStreamingEncoder extends EncoderBase<ILoggingEvent> implements StreamingEncoder<ILoggingEvent> {
    @Override public byte[] headerBytes() {
      return null;
    }

    @Override public byte[] encode(ILoggingEvent event) {
      return null;
    }

    @Override public byte[] footerBytes() {
      return null;
    }

    @Override public void encode(ILoggingEvent event, OutputStream outputStream) throws IOException {
      outputStream.write("First few bytes".getBytes());
      throw new IOException("Exception thrown after some bytes are written");
    }
  }

  /**
     * At least one valid destination must be configured.
     * The appender refuses to start in case of error.
     */
  @Test public void testDestination_None() {
    appender.start();
    assertThat(appender.isStarted()).isFalse();
    assertThat(statusManager.getCopyOfStatusList()).anySatisfy((status) -> {
      assertThat(status.getLevel()).isEqualTo(Status.ERROR);
      assertThat(status.getMessage()).contains("No destination was configured");
    });
  }

  /**
     * Appender does not start when no encoder is specified
     */
  @Test public void testEncoder_None() {
    appender.setEncoder(null);
    appender.start();
    assertThat(appender.isStarted()).isFalse();
    assertThat(statusManager.getCopyOfStatusList()).anySatisfy((status) -> {
      assertThat(status.getLevel()).isEqualTo(Status.ERROR);
      assertThat(status.getMessage()).contains("No encoder was configured");
    });
  }

  @Test public void testRoundRobin() throws Exception {
    appender.addDestination("localhost:10000");
    appender.addDestination("localhost:10001");
    RoundRobinDestinationConnectionStrategy strategy = new RoundRobinDestinationConnectionStrategy();
    strategy.setConnectionTTL(Duration.buildByMilliseconds(100));
    appender.setConnectionStrategy(strategy);
    appender.start();
    verify(encoder).start();
    appender.append(event1);
    Thread.sleep(strategy.getConnectionTTL().getMilliseconds() + 50);
    appender.append(event1);
    verify(socket, async().times(2)).connect(any(), anyInt());
    InOrder inOrder = inOrder(socket);
    inOrder.verify(socket).connect(host("localhost", 10000), anyInt());
    inOrder.verify(socket).close();
    inOrder.verify(socket).connect(host("localhost", 10001), anyInt());
  }

  @Test public void testConfigParams() {
    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> appender.setConnectionTimeout(Duration.buildByMilliseconds(-1)));
    assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> appender.setKeepAliveCharset(null));
    assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> appender.setConnectionStrategy(null));
  }

  private SocketAddress host(final String host, final int port) {
    return argThat(hasHostAndPort(host, port));
  }

  private ArgumentMatcher<SocketAddress> hasHostAndPort(final String host, final int port) {
    return new ArgumentMatcher<SocketAddress>() {
      @Override public boolean matches(SocketAddress argument) {
        InetSocketAddress sockAddr = (InetSocketAddress) argument;
        return host.equals(sockAddr.getHostName()) && port == sockAddr.getPort();
      }

      @Override public String toString() {
        return host + ":" + port;
      }
    };
  }

  private static VerificationWithTimeout async() {
    return timeout(VERIFICATION_TIMEOUT);
  }
}