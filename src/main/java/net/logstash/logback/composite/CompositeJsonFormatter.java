package net.logstash.logback.composite;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.ref.SoftReference;
import net.logstash.logback.decorate.JsonFactoryDecorator;
import net.logstash.logback.decorate.JsonGeneratorDecorator;
import net.logstash.logback.decorate.NullJsonFactoryDecorator;
import net.logstash.logback.decorate.NullJsonGeneratorDecorator;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import ch.qos.logback.core.spi.LifeCycle;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class CompositeJsonFormatter<Event extends DeferredProcessingAware> extends ContextAwareBase implements LifeCycle {
  private final ThreadLocal<SoftReference<BufferRecycler>> recycler = new ThreadLocal<SoftReference<BufferRecycler>>() {
    protected SoftReference<BufferRecycler> initialValue() {
      final BufferRecycler bufferRecycler = new BufferRecycler();
      return new SoftReference<BufferRecycler>(bufferRecycler);
    }
  };

  private JsonFactory jsonFactory = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS).getFactory().disable(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM);

  private JsonFactoryDecorator jsonFactoryDecorator = new NullJsonFactoryDecorator();

  private JsonGeneratorDecorator jsonGeneratorDecorator = new NullJsonGeneratorDecorator();

  private JsonProviders<Event> jsonProviders = new JsonProviders<Event>();

  private JsonEncoding encoding = JsonEncoding.UTF8;

  private volatile boolean started;

  public CompositeJsonFormatter(ContextAware declaredOrigin) {
    super(declaredOrigin);
  }

  @Override public void start() {
    if (jsonProviders.getProviders().isEmpty()) {
      addError("No providers configured");
    }
    jsonFactory = createJsonFactory();
    jsonProviders.setJsonFactory(jsonFactory);
    jsonProviders.setContext(context);
    jsonProviders.start();
    started = true;
  }

  @Override public void stop() {
    jsonProviders.stop();
    started = false;
  }

  @Override public boolean isStarted() {
    return started;
  }

  public byte[] writeEventAsBytes(Event event) throws IOException {
    ByteArrayBuilder outputStream = new ByteArrayBuilder(getBufferRecycler());
    try {
      writeEventToOutputStream(event, outputStream);
      outputStream.flush();
      return outputStream.toByteArray();
    }  finally {
      outputStream.release();
    }
  }

  public void writeEventToOutputStream(Event event, OutputStream outputStream) throws IOException {
    JsonGenerator generator = createGenerator(outputStream);
    writeEventToGenerator(generator, event);
  }

  public String writeEventAsString(Event event) throws IOException {
    SegmentedStringWriter writer = new SegmentedStringWriter(getBufferRecycler());
    JsonGenerator generator = createGenerator(writer);
    writeEventToGenerator(generator, event);
    writer.flush();
    return writer.getAndClear();
  }

  protected void writeEventToGenerator(JsonGenerator generator, Event event) throws IOException {
    if (!isStarted()) {
      throw new IllegalStateException("Encoding attempted before starting.");
    }
    generator.writeStartObject();
    jsonProviders.writeTo(generator, event);
    generator.writeEndObject();
    generator.flush();
  }

  protected void prepareForDeferredProcessing(Event event) {
    event.prepareForDeferredProcessing();
    jsonProviders.prepareForDeferredProcessing(event);
  }

  private JsonGenerator createGenerator(OutputStream outputStream) throws IOException {
    return this.jsonGeneratorDecorator.decorate(jsonFactory.createGenerator(outputStream, encoding));
  }

  private JsonGenerator createGenerator(Writer writer) throws IOException {
    return this.jsonGeneratorDecorator.decorate(jsonFactory.createGenerator(writer));
  }

  private BufferRecycler getBufferRecycler() {
    SoftReference<BufferRecycler> bufferRecyclerReference = recycler.get();
    BufferRecycler bufferRecycler = bufferRecyclerReference.get();
    if (bufferRecycler == null) {
      recycler.remove();
      return getBufferRecycler();
    }
    return bufferRecycler;
  }

  public JsonFactory getJsonFactory() {
    return jsonFactory;
  }

  public JsonFactoryDecorator getJsonFactoryDecorator() {
    return jsonFactoryDecorator;
  }

  public void setJsonFactoryDecorator(JsonFactoryDecorator jsonFactoryDecorator) {
    this.jsonFactoryDecorator = jsonFactoryDecorator;
  }

  public JsonGeneratorDecorator getJsonGeneratorDecorator() {
    return jsonGeneratorDecorator;
  }

  public void setJsonGeneratorDecorator(JsonGeneratorDecorator jsonGeneratorDecorator) {
    this.jsonGeneratorDecorator = jsonGeneratorDecorator;
  }

  public JsonProviders<Event> getProviders() {
    return jsonProviders;
  }

  public String getEncoding() {
    return encoding.getJavaName();
  }

  public void setEncoding(String encodingName) {
    for (JsonEncoding encoding : JsonEncoding.values()) {
      if (encoding.getJavaName().equals(encodingName) || encoding.name().equals(encodingName)) {
        this.encoding = encoding;
        return;
      }
    }
    throw new IllegalArgumentException("Unknown encoding " + encodingName);
  }

  public void setProviders(JsonProviders<Event> jsonProviders) {
    this.jsonProviders = jsonProviders;
  }

  private boolean findAndRegisterJacksonModules = true;

  private MappingJsonFactory createJsonFactory() {
    ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    if (findAndRegisterJacksonModules) {
      objectMapper.findAndRegisterModules();
    }
    MappingJsonFactory jsonFactory = (MappingJsonFactory) objectMapper.getFactory().disable(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM);
    return this.jsonFactoryDecorator.decorate(jsonFactory);
  }

  public boolean isFindAndRegisterJacksonModules() {
    return findAndRegisterJacksonModules;
  }

  public void setFindAndRegisterJacksonModules(boolean findAndRegisterJacksonModules) {
    this.findAndRegisterJacksonModules = findAndRegisterJacksonModules;
  }
}