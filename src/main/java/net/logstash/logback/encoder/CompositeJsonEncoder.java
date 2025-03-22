package net.logstash.logback.encoder;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import net.logstash.logback.composite.CompositeJsonFormatter;
import net.logstash.logback.composite.JsonProviders;
import net.logstash.logback.decorate.JsonFactoryDecorator;
import net.logstash.logback.decorate.JsonGeneratorDecorator;
import net.logstash.logback.util.ReusableByteBuffer;
import net.logstash.logback.util.ReusableByteBufferPool;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.EncoderBase;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import net.logstash.logback.encoder.wrapper.EncodedPayloadWrapper;
import net.logstash.logback.encoder.wrapper.LumberjackPayloadWrapper;

public abstract class CompositeJsonEncoder<Event extends DeferredProcessingAware> extends EncoderBase<Event> implements StreamingEncoder<Event> {
  private static final byte[] EMPTY_BYTES = new byte[0];

  /**
     * The minimum size of the byte array buffer used when
     * encoding events in logback versions greater than or equal to 1.2.0.
     *
     * The actual buffer size will be the {@link #minBufferSize}
     * plus the prefix, suffix, and line separators sizes.
     */
  private int minBufferSize = 1024;

  private Encoder<Event> prefix;

  /**
     * Provides reusable byte buffers (initialized when the encoder is started).
     */
  private 
<<<<<<< /usr/src/app/output/logstash/logstash-logback-encoder/ac414685756c472a01474a55a86d19173fc891b3/src/main/java/net/logstash/logback/encoder/CompositeJsonEncoder.java/left.java
  EncodedPayloadWrapper
=======
  ReusableByteBufferPool
>>>>>>> /usr/src/app/output/logstash/logstash-logback-encoder/ac414685756c472a01474a55a86d19173fc891b3/src/main/java/net/logstash/logback/encoder/CompositeJsonEncoder.java/right.java
   
<<<<<<< /usr/src/app/output/logstash/logstash-logback-encoder/ac414685756c472a01474a55a86d19173fc891b3/src/main/java/net/logstash/logback/encoder/CompositeJsonEncoder.java/left.java
  payloadWrapper
=======
  bufferPool
>>>>>>> /usr/src/app/output/logstash/logstash-logback-encoder/ac414685756c472a01474a55a86d19173fc891b3/src/main/java/net/logstash/logback/encoder/CompositeJsonEncoder.java/right.java
  ;

  private Encoder<Event> suffix;

  private final CompositeJsonFormatter<Event> formatter;

  private String lineSeparator = System.lineSeparator();

  private byte[] lineSeparatorBytes;

  private Charset charset;

  public CompositeJsonEncoder() {
    super();
    this.formatter = createFormatter();
  }

  protected abstract CompositeJsonFormatter<Event> createFormatter();

  @Override public void encode(Event event, OutputStream outputStream) throws IOException {
    if (!isStarted()) {
      throw new IllegalStateException("Encoder is not started");
    }
    encode(prefix, event, outputStream);
    formatter.writeEventToOutputStream(event, outputStream);
    encode(suffix, event, outputStream);
    outputStream.write(lineSeparatorBytes);
  }

  @Override public byte[] encode(Event event) {
    if (!isStarted()) {
      throw new IllegalStateException("Encoder is not started");
    }
    ReusableByteBuffer buffer = bufferPool.acquire();
    try {
      encode(event, buffer);
      return 
<<<<<<< /usr/src/app/output/logstash/logstash-logback-encoder/ac414685756c472a01474a55a86d19173fc891b3/src/main/java/net/logstash/logback/encoder/CompositeJsonEncoder.java/left.java
      wrapEncoded(outputStream.toByteArray())
=======
      buffer.toByteArray()
>>>>>>> /usr/src/app/output/logstash/logstash-logback-encoder/ac414685756c472a01474a55a86d19173fc891b3/src/main/java/net/logstash/logback/encoder/CompositeJsonEncoder.java/right.java
      ;
    } catch (IOException e) {
      addWarn("Error encountered while encoding log event. Event: " + event, e);
      return EMPTY_BYTES;
    } finally {
      bufferPool.release(buffer);
    }
  }

  private void encode(Encoder<Event> encoder, Event event, OutputStream outputStream) throws IOException {
    if (encoder != null) {
      byte[] data = encoder.encode(event);
      if (data != null) {
        outputStream.write(data);
      }
    }
  }

  private byte[] wrapEncoded(byte[] encoded) throws IOException {
    if (payloadWrapper != null) {
      return payloadWrapper.wrap(encoded);
    }
    return encoded;
  }

  @Override public void start() {
    if (isStarted()) {
      return;
    }
    super.start();
    this.bufferPool = new ReusableByteBufferPool(this.minBufferSize);
    formatter.setContext(getContext());
    formatter.start();
    charset = Charset.forName(formatter.getEncoding());
    lineSeparatorBytes = this.lineSeparator == null ? EMPTY_BYTES : this.lineSeparator.getBytes(charset);
    startWrapped(prefix);
    startWrapped(suffix);
  }

  @SuppressWarnings(value = { "unchecked", "rawtypes" }) private void startWrapped(Encoder<Event> wrapped) {
    if (wrapped instanceof LayoutWrappingEncoder) {
      LayoutWrappingEncoder<Event> layoutWrappedEncoder = (LayoutWrappingEncoder<Event>) wrapped;
      layoutWrappedEncoder.setCharset(charset);
      if (layoutWrappedEncoder.getLayout() instanceof PatternLayoutBase) {
        PatternLayoutBase layout = (PatternLayoutBase) layoutWrappedEncoder.getLayout();
        layout.setPostCompileProcessor(null);
        layout.start();
      }
    }
    if (wrapped != null && !wrapped.isStarted()) {
      wrapped.start();
    }
  }

  @Override public void stop() {
    if (isStarted()) {
      super.stop();
      formatter.stop();
      stopWrapped(prefix);
      stopWrapped(suffix);
    }
  }

  private void stopWrapped(Encoder<Event> wrapped) {
    if (wrapped != null && wrapped.isStarted()) {
      wrapped.stop();
    }
  }

  @Override public byte[] headerBytes() {
    return EMPTY_BYTES;
  }

  @Override public byte[] footerBytes() {
    return EMPTY_BYTES;
  }

  public JsonProviders<Event> getProviders() {
    return formatter.getProviders();
  }

  public void setProviders(JsonProviders<Event> jsonProviders) {
    formatter.setProviders(jsonProviders);
  }

  public JsonFactoryDecorator getJsonFactoryDecorator() {
    return formatter.getJsonFactoryDecorator();
  }

  public void setJsonFactoryDecorator(JsonFactoryDecorator jsonFactoryDecorator) {
    formatter.setJsonFactoryDecorator(jsonFactoryDecorator);
  }

  public JsonGeneratorDecorator getJsonGeneratorDecorator() {
    return formatter.getJsonGeneratorDecorator();
  }

  public String getEncoding() {
    return formatter.getEncoding();
  }

  /**
     * The character encoding to use (default = "<tt>UTF-8</tt>").
     * Must an encoding supported by {@link com.fasterxml.jackson.core.JsonEncoding}
     */
  public void setEncoding(String encodingName) {
    formatter.setEncoding(encodingName);
  }

  public void setFindAndRegisterJacksonModules(boolean findAndRegisterJacksonModules) {
    formatter.setFindAndRegisterJacksonModules(findAndRegisterJacksonModules);
  }

  public void setJsonGeneratorDecorator(JsonGeneratorDecorator jsonGeneratorDecorator) {
    formatter.setJsonGeneratorDecorator(jsonGeneratorDecorator);
  }

  public String getLineSeparator() {
    return lineSeparator;
  }

  /**
     * Sets which lineSeparator to use between events.
     * <p>
     *
     * The following values have special meaning:
     * <ul>
     * <li><tt>null</tt> or empty string = no new line.</li>
     * <li>"<tt>SYSTEM</tt>" = operating system new line (default).</li>
     * <li>"<tt>UNIX</tt>" = unix line ending (\n).</li>
     * <li>"<tt>WINDOWS</tt>" = windows line ending (\r\n).</li>
     * </ul>
     * <p>
     * Any other value will be used as given as the lineSeparator.
     */
  public void setLineSeparator(String lineSeparator) {
    this.lineSeparator = SeparatorParser.parseSeparator(lineSeparator);
  }

  public int getMinBufferSize() {
    return minBufferSize;
  }

  /**
     * Sets the minimum size of the byte array buffer used when
     * encoding events in logback versions greater than or equal to 1.2.0.
     *
     * The actual buffer size will be the {@link #minBufferSize}
     * plus the prefix, suffix, and line separators sizes.
     */
  public void setMinBufferSize(int minBufferSize) {
    this.minBufferSize = minBufferSize;
  }

  protected CompositeJsonFormatter<Event> getFormatter() {
    return formatter;
  }

  public Encoder<Event> getPrefix() {
    return prefix;
  }

  public void setPrefix(Encoder<Event> prefix) {
    this.prefix = prefix;
  }

  public Encoder<Event> getSuffix() {
    return suffix;
  }

  public void setSuffix(Encoder<Event> suffix) {
    this.suffix = suffix;
  }

  public EncodedPayloadWrapper getPayloadWrapper() {
    return payloadWrapper;
  }

  public void setPayloadWrapper(EncodedPayloadWrapper wrapper) {
    this.payloadWrapper = wrapper;
  }
}