package com.github.dozedoff.similarImage.messaging;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.imageio.IIOException;
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
import com.github.dozedoff.similarImage.handler.ArtemisHashProducer;
import com.github.dozedoff.similarImage.image.ImageResizer;
import com.github.dozedoff.similarImage.messaging.ArtemisQueue.QueueAddress;
import com.github.dozedoff.similarImage.util.ImageUtil;
import com.github.dozedoff.similarImage.util.MessagingUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import at.dhyan.open_imaging.GifDecoder;
import at.dhyan.open_imaging.GifDecoder.GifImage;
import java.util.concurrent.atomic.AtomicLong;
import com.github.dozedoff.similarImage.io.ByteBufferInputstream;

public class ResizerNode implements MessageHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(ResizerNode.class);

  private static final String DUMMY = "";

  public static final String METRIC_NAME_RESIZE_MESSAGES = MetricRegistry.name(ResizerNode.class, "resize", "messages");

  private final ClientConsumer consumer;

  private final ClientProducer producer;

  private final ClientSession session;

  private final ImageResizer resizer;

  private MessageFactory messageFactory;

  private final Cache<String, String> pendingCache;

  private final Meter resizeRequests;

  @Deprecated public ResizerNode(ClientSession session, ImageResizer resizer) throws Exception {
    this(session, resizer, QueueAddress.RESIZE_REQUEST.toString(), QueueAddress.HASH_REQUEST.toString(), new QueryMessage(session, QueueAddress.REPOSITORY_QUERY));
  }

  public ResizerNode(ClientSession session, ImageResizer resizer, MetricRegistry metrics) throws Exception {
    this(session, resizer, QueueAddress.RESIZE_REQUEST.toString(), QueueAddress.HASH_REQUEST.toString(), new QueryMessage(session, QueueAddress.REPOSITORY_QUERY));
  }

  public ResizerNode(ClientSession session, ImageResizer resizer, String inAddress, String outAddress) throws Exception {
    this(session, resizer, inAddress, outAddress, new QueryMessage(session, QueueAddress.REPOSITORY_QUERY));
  }

  @Deprecated protected ResizerNode(ClientSession session, ImageResizer resizer, String requestAddress, String resultAddress, QueryMessage queryMessage) throws Exception {
    this(session, resizer, requestAddress, resultAddress, queryMessage, new MetricRegistry());
    this.messageBuffer = ByteBuffer.allocate(INITIAL_BUFFER_SIZE);
    preLoadCache(queryMessage);
  }

  protected ResizerNode(ClientSession session, ImageResizer resizer, String requestAddress, String resultAddress, QueryMessage queryMessage, MetricRegistry metrics) throws Exception {
    this.session = session;
    this.consumer = session.createConsumer(requestAddress);
    this.producer = session.createProducer(resultAddress);
    this.resizer = resizer;
    this.messageFactory = new MessageFactory(session);
    this.pendingCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    this.resizeRequests = metrics.meter(METRIC_NAME_RESIZE_MESSAGES);
    this.consumer.setMessageHandler(this);
    preLoadCache(queryMessage);
  }

  private void preLoadCache(QueryMessage queryMessage) throws Exception {
    List<String> pending = queryMessage.pendingImagePaths();
    LOGGER.info("Pre loading cache with {} pending paths", pending.size());
    for (String path : pending) {
      pendingCache.put(path, DUMMY);
    }
  }

  protected final void setMessageFactory(MessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  @Override public void onMessage(ClientMessage message) {
    String pathPropterty = null;
    resizeRequests.mark();
    try {
      pathPropterty = message.getStringProperty(ArtemisHashProducer.MESSAGE_PATH_PROPERTY);
      LOGGER.debug("Resize request for image {}", pathPropterty);
      if (pendingCache.getIfPresent(pathPropterty) != null) {
        LOGGER.trace("{} found in cache, skipping...", pathPropterty);
        return;
      }
      Path path = Paths.get(pathPropterty);
      checkBufferCapacity(message.getBodySize());
      messageBuffer.limit(message.getBodySize());
      messageBuffer.rewind();
      message.getBodyBuffer().readBytes(messageBuffer);
      messageBuffer.rewind();
      Path filename = path.getFileName();
      InputStream is = new ByteBufferInputstream(messageBuffer);
      if (filename != null && filename.toString().toLowerCase().endsWith(".gif")) {
        GifImage gi = GifDecoder.read(is);
        is = new ByteArrayInputStream(ImageUtil.imageToBytes(gi.getFrame(0)));
      }
      byte[] resizedImageData = resizer.resize(is);
      UUID uuid = UUID.randomUUID();
      ClientMessage trackMessage = messageFactory.trackPath(path, uuid);
      producer.send(QueueAddress.RESULT.toString(), trackMessage);
      LOGGER.trace("Sent tracking message for {} with UUID {}", pathPropterty, uuid);
      ClientMessage response = messageFactory.hashRequestMessage(resizedImageData, uuid);
      LOGGER.trace("Sending hash request with id {} instead of path {}", uuid, path);
      producer.send(response);
      pendingCache.put(pathPropterty, DUMMY);
    } catch (ActiveMQException e) {
      LOGGER.error("Failed to send message: {}", e.toString());
    } catch (IIOException | ArrayIndexOutOfBoundsException ie) {
      markImageCorrupt(pathPropterty);
    } catch (IOException e) {
      if (isImageError(e.getMessage())) {
        markImageCorrupt(pathPropterty);
      } else {
        LOGGER.error("Failed to process image: {}", e.toString());
      }
    } catch (Exception e) {
      LOGGER.error("Unhandled exception: {}", e.toString());
    }
  }

  private boolean isImageError(String message) {
    return message.startsWith("Unknown block") || message.startsWith("Invalid GIF header");
  }

  private void markImageCorrupt(String path) {
    LOGGER.warn("Unable to read image {}, marking as corrupt", path);
    try {
      sendImageErrorResponse(path);
    } catch (ActiveMQException e) {
      LOGGER.error("Failed to send corrupt image message: {}", e.toString());
    }
  }

  private void sendImageErrorResponse(String path) throws ActiveMQException {
    String corruptMessageAddress = QueueAddress.EA_UPDATE.toString();
    LOGGER.trace("Sending corrupt image message for {} to address {}", path, corruptMessageAddress);
    ClientMessage response = messageFactory.corruptMessage(Paths.get(path));
    producer.send(corruptMessageAddress, response);
  }

  public void stop() {
    LOGGER.info("Stopping {}...", this.getClass().getSimpleName());
    MessagingUtil.silentClose(consumer);
    MessagingUtil.silentClose(producer);
    MessagingUtil.silentClose(session);
  }

  private static final int INITIAL_BUFFER_SIZE = 1024 * 1024 * 5;

  private final AtomicLong resizeSensing = new AtomicLong();

  private ByteBuffer messageBuffer;

  private void checkBufferCapacity(int messageSize) {
    if (messageSize > messageBuffer.capacity()) {
      resizeSensing.getAndIncrement();
      int oldBufferCap = messageBuffer.capacity();
      allocateNewBuffer(messageSize);
      int newBufferCap = messageBuffer.capacity();
      LOGGER.debug("Message size of {} exceeds buffer capacity of {}, allocated new buffer with capactiy {}", messageSize, oldBufferCap, newBufferCap);
    }
  }

  protected void allocateNewBuffer(int messageSize) {
    messageBuffer = ByteBuffer.allocateDirect(calcNewBufferSize(messageSize));
  }

  private int calcNewBufferSize(int messageSize) {
    return messageSize * 2;
  }

  long getBufferResizes() {
    return resizeSensing.get();
  }
}