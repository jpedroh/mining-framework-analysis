package com.github.dozedoff.similarImage.messaging;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.github.dozedoff.commonj.hash.ImagePHash;
import com.github.dozedoff.similarImage.util.MessagingUtil;
import java.util.UUID;
import javax.imageio.ImageIO;
import com.github.dozedoff.similarImage.io.ByteBufferInputstream;

public class HasherNode implements MessageHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(HasherNode.class);

  private final ClientConsumer consumer;

  private final ClientProducer producer;

  private final ClientSession session;

  private final ImagePHash hasher;

  private MessageFactory messageFactory;

  private final Meter hashRequests;

  public static final String METRIC_NAME_HASH_MESSAGES = MetricRegistry.name(HasherNode.class, "hash", "messages");

  public HasherNode(ClientSession session, ImagePHash hasher, String requestAddress, String resultAddress, MetricRegistry metrics) throws ActiveMQException {
    this.hasher = hasher;
    this.session = session;
    this.consumer = session.createConsumer(requestAddress);
    this.producer = session.createProducer(resultAddress);
    this.consumer.setMessageHandler(this);
    this.messageFactory = new MessageFactory(session);
    this.hashRequests = metrics.meter(METRIC_NAME_HASH_MESSAGES);
  }

  @Deprecated public HasherNode(ClientSession session, ImagePHash hasher, String requestAddress, String resultAddress) throws ActiveMQException {
    this(session, hasher, requestAddress, resultAddress, new MetricRegistry());
    this.buffer = ByteBuffer.allocate(INITIAL_BUFFER_SIZE);
  }

  protected final void setMessageFactory(MessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  public void stop() {
    LOGGER.info("Stopping {}...", this.getClass().getSimpleName());
    MessagingUtil.silentClose(consumer);
    MessagingUtil.silentClose(producer);
    MessagingUtil.silentClose(session);
  }

  @Override public void onMessage(ClientMessage message) {
    hashRequests.mark();
    try {
      long most = message.getBodyBuffer().readLong();
      long least = message.getBodyBuffer().readLong();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Got hash request with UUID {}, size {}", new UUID(most, least), message.getBodySize());
      }
      checkBufferCapacity(message.getBodySize());
      buffer.limit(message.getBodySize());
      buffer.rewind();
      message.getBodyBuffer().readBytes(buffer);
      buffer.rewind();
      long hash = doHash(ImageIO.read(new ByteBufferInputstream(buffer)));
      ClientMessage response = messageFactory.resultMessage(hash, most, least);
      producer.send(response);
    } catch (ActiveMQException e) {
      LOGGER.error("Failed to process message: {}", e.toString());
    } catch (Exception e) {
      LOGGER.error("Failed to process image: {}", e.toString());
    }
  }

  private long doHash(BufferedImage image) throws Exception {
    long hash = hasher.getLongHashScaledImage(image);
    return hash;
  }

  private static final int INITIAL_BUFFER_SIZE = 4096;

  private ByteBuffer buffer;

  private void checkBufferCapacity(int messageSize) {
    if (messageSize > buffer.capacity()) {
      LOGGER.debug("Buffer size {} is too small for message size {}, allocating new buffer...", buffer.capacity(), messageSize);
      buffer = ByteBuffer.allocate(messageSize);
    }
  }
}