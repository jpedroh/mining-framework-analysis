package com.github.dozedoff.similarImage.messaging;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.imageio.IIOException;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import com.codahale.metrics.MetricRegistry;
import com.github.dozedoff.similarImage.db.PendingHashImage;
import com.github.dozedoff.similarImage.db.repository.PendingHashImageRepository;
import com.github.dozedoff.similarImage.handler.ArtemisHashProducer;
import com.github.dozedoff.similarImage.image.ImageResizer;
import com.github.dozedoff.similarImage.messaging.MessageFactory.MessageProperty;
import com.github.dozedoff.similarImage.messaging.MessageFactory.TaskType;
import java.nio.file.Paths;

@RunWith(value = MockitoJUnitRunner.class) public class ResizerNodeTest extends MessagingBaseTest {
  private static final String REQUEST_ADDRESS = "request";

  private static final String RESULT_ADDRESS = "result";

  private static final String PATH = "bar";

  @Mock private ImageResizer resizer;

  @Mock private PendingHashImageRepository pendingRepo;

  @Mock private QueryMessage queryMessage;

  private ResizerNode cut;

  private MockMessageBuilder messageBuilder;

  private MetricRegistry metrics;

  @Before public void setUp() throws Exception {
    when(pendingRepo.store(any(PendingHashImage.class))).thenReturn(true);
    when(pendingRepo.exists(any(PendingHashImage.class))).thenReturn(false);
    when(resizer.resize(any(InputStream.class))).thenReturn(new byte[0]);
    when(queryMessage.pendingImagePaths()).thenReturn(Arrays.asList(PATH));
    metrics = new MetricRegistry();
    cut = new ResizerNode(session, resizer, REQUEST_ADDRESS, RESULT_ADDRESS, queryMessage, metrics);
    messageBuilder = new MockMessageBuilder();
  }

  @Test public void testValidImageSent() throws Exception {
    message = messageBuilder.configureResizeMessage().build();
    cut.onMessage(message);
    verify(producer).send(eq(sessionMessage));
  }

  @Test public void testValidImageHasId() throws Exception {
    message = messageBuilder.configureResizeMessage().build();
    cut.onMessage(message);
    assertThat(sessionMessage.getBodyBuffer().readLong(), is(not(0L)));
    assertThat(sessionMessage.getBodyBuffer().readLong(), is(not(0L)));
  }

  @Test public void testValidImageNotCorrupt() throws Exception {
    message = messageBuilder.configureResizeMessage().build();
    when(session.createMessage(any(Boolean.class))).thenReturn(Mockito.mock(ClientMessage.class), sessionMessage);
    cut.onMessage(message);
    assertThat(sessionMessage.containsProperty(ArtemisHashProducer.MESSAGE_TASK_PROPERTY), is(false));
  }

  @Test public void testValidImageTrackSent() throws Exception {
    message = messageBuilder.configureResizeMessage().build();
    when(session.createMessage(any(Boolean.class))).thenReturn(sessionMessage, Mockito.mock(ClientMessage.class));
    cut.onMessage(message);
    assertThat(sessionMessage.getStringProperty(MessageProperty.task.toString()), is(TaskType.track.toString()));
  }

  @Test public void testInvalidImageDataResponseMessageSent() throws Exception {
    message = messageBuilder.configureResizeMessage().build();
    cut.onMessage(message);
    verify(producer).send(sessionMessage);
  }

  @Test public void testCorruptImageDataTaskProperty() throws Exception {
    message = messageBuilder.configureResizeMessage().build();
    when(resizer.resize(any(InputStream.class))).thenThrow(new IIOException(""));
    cut.onMessage(message);
    assertThat(sessionMessage.getStringProperty(ArtemisHashProducer.MESSAGE_TASK_PROPERTY), is(ArtemisHashProducer.MESSAGE_TASK_VALUE_CORRUPT));
  }

  @Test public void testCorruptImageDataPathProperty() throws Exception {
    message = messageBuilder.configureResizeMessage().build();
    when(resizer.resize(any(InputStream.class))).thenThrow(new IIOException(""));
    cut.onMessage(message);
    assertThat(sessionMessage.getStringProperty(ArtemisHashProducer.MESSAGE_PATH_PROPERTY), is("foo"));
  }

  @Test public void testImageReadError() throws Exception {
    message = messageBuilder.configureResizeMessage().build();
    when(resizer.resize(any(InputStream.class))).thenThrow(new IOException("testing"));
    cut.onMessage(message);
    verify(producer, never()).send(any(ClientMessage.class));
  }

  @Test public void testGIFerrorUnknownBlock() throws Exception {
    message = messageBuilder.configureResizeMessage().build();
    when(resizer.resize(any(InputStream.class))).thenThrow(new IOException("Unknown block"));
    cut.onMessage(message);
    assertThat(sessionMessage.getStringProperty(ArtemisHashProducer.MESSAGE_TASK_PROPERTY), is(ArtemisHashProducer.MESSAGE_TASK_VALUE_CORRUPT));
  }

  @Test public void testGIFerrorInvalidHeader() throws Exception {
    message = messageBuilder.configureResizeMessage().build();
    when(resizer.resize(any(InputStream.class))).thenThrow(new IOException("Invalid GIF header"));
    cut.onMessage(message);
    assertThat(sessionMessage.getStringProperty(ArtemisHashProducer.MESSAGE_TASK_PROPERTY), is(ArtemisHashProducer.MESSAGE_TASK_VALUE_CORRUPT));
  }

  @Test public void testDuplicatePreLoaded() throws Exception {
    message = messageBuilder.configureResizeMessage().addProperty(MessageProperty.path.toString(), PATH).build();
    cut.onMessage(message);
    verify(producer, never()).send(any(ClientMessage.class));
  }

  @Test public void testMetricsResizeRequest() throws Exception {
    message = messageBuilder.configureResizeMessage().build();
    when(session.createMessage(any(Boolean.class))).thenReturn(Mockito.mock(ClientMessage.class), sessionMessage);
    cut.onMessage(message);
    assertThat(metrics.getMeters().get(ResizerNode.METRIC_NAME_RESIZE_MESSAGES).getCount(), is(1L));
  }

  private static final int BUFFER_TEST_DATA_SIZE = 100;

  @Test public void testBufferResize() throws Exception {
    message = new MessageFactory(session).resizeRequest(Paths.get("foo"), null);
    for (byte i = 0; i < BUFFER_TEST_DATA_SIZE; i++) {
      message.getBodyBuffer().writeByte(i);
    }
    cut.allocateNewBuffer(1);
    cut.onMessage(message);
    assertThat(cut.getBufferResizes(), is(1L));
  }
}