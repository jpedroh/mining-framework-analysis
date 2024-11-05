package com.fasterxml.jackson.core;
import java.io.*;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.core.exc.WrappedIOException;
import com.fasterxml.jackson.core.io.*;
import com.fasterxml.jackson.core.json.JsonFactory;
import com.fasterxml.jackson.core.sym.PropertyNameMatcher;
import com.fasterxml.jackson.core.sym.SimpleNameMatcher;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.BufferRecyclers;
import com.fasterxml.jackson.core.util.JacksonFeature;
import com.fasterxml.jackson.core.util.Named;
import com.fasterxml.jackson.core.util.Snapshottable;

public abstract class TokenStreamFactory implements Versioned, java.io.Serializable, Snapshottable<TokenStreamFactory> {
  private static final long serialVersionUID = 3L;

  public final static ObjectWriteContext EMPTY_WRITE_CONTEXT = new ObjectWriteContext.Base();

  public enum Feature implements JacksonFeature {
    INTERN_PROPERTY_NAMES(false),
    CANONICALIZE_PROPERTY_NAMES(true),
    FAIL_ON_SYMBOL_HASH_OVERFLOW(true),
    USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING(true)
    ;

    private final boolean _defaultState;

    public static int collectDefaults() {
      int flags = 0;
      for (Feature f : values()) {
        if (f.enabledByDefault()) {
          flags |= f.getMask();
        }
      }
      return flags;
    }

    private Feature(boolean defaultState) {
      _defaultState = defaultState;
    }

    @Override public boolean enabledByDefault() {
      return _defaultState;
    }

    @Override public boolean enabledIn(int flags) {
      return (flags & getMask()) != 0;
    }

    @Override public int getMask() {
      return (1 << ordinal());
    }
  }

  public abstract static class TSFBuilder<F extends TokenStreamFactory, B extends TSFBuilder<F, B>> {
    protected int _factoryFeatures;

    protected int _streamReadFeatures;

    protected int _streamWriteFeatures;

    protected int _formatReadFeatures;

    protected int _formatWriteFeatures;

    protected TSFBuilder(int formatReadF, int formatWriteF) {
      _factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
      _streamReadFeatures = DEFAULT_STREAM_READ_FEATURE_FLAGS;
      _streamWriteFeatures = DEFAULT_STREAM_WRITE_FEATURE_FLAGS;
      _formatReadFeatures = formatReadF;
      _formatWriteFeatures = formatWriteF;
    }

    protected TSFBuilder(TokenStreamFactory base) {
      this(base._factoryFeatures, base._streamReadFeatures, base._streamWriteFeatures, base._formatReadFeatures, base._formatWriteFeatures);
    }

    protected TSFBuilder(int factoryFeatures, int streamReadFeatures, int streamWriteFeatures, int formatReadFeatures, int formatWriteFeatures) {
      _factoryFeatures = factoryFeatures;
      _streamReadFeatures = streamReadFeatures;
      _streamWriteFeatures = streamWriteFeatures;
      _formatReadFeatures = formatReadFeatures;
      _formatWriteFeatures = formatWriteFeatures;
    }

    public int factoryFeaturesMask() {
      return _factoryFeatures;
    }

    public int streamReadFeaturesMask() {
      return _streamReadFeatures;
    }

    public int streamWriteFeaturesMask() {
      return _streamWriteFeatures;
    }

    public int formatReadFeaturesMask() {
      return _formatReadFeatures;
    }

    public int formatWriteFeaturesMask() {
      return _formatWriteFeatures;
    }

    public B enable(TokenStreamFactory.Feature f) {
      _factoryFeatures |= f.getMask();
      return _this();
    }

    public B disable(TokenStreamFactory.Feature f) {
      _factoryFeatures &= ~f.getMask();
      return _this();
    }

    public B configure(TokenStreamFactory.Feature f, boolean state) {
      return state ? enable(f) : disable(f);
    }

    public B enable(StreamReadFeature f) {
      _streamReadFeatures |= f.getMask();
      return _this();
    }

    public B enable(StreamReadFeature first, StreamReadFeature... other) {
      _streamReadFeatures |= first.getMask();
      for (StreamReadFeature f : other) {
        _streamReadFeatures |= f.getMask();
      }
      return _this();
    }

    public B disable(StreamReadFeature f) {
      _streamReadFeatures &= ~f.getMask();
      return _this();
    }

    public B disable(StreamReadFeature first, StreamReadFeature... other) {
      _streamReadFeatures &= ~first.getMask();
      for (StreamReadFeature f : other) {
        _streamReadFeatures &= ~f.getMask();
      }
      return _this();
    }

    public B configure(StreamReadFeature f, boolean state) {
      return state ? enable(f) : disable(f);
    }

    public B enable(StreamWriteFeature f) {
      _streamWriteFeatures |= f.getMask();
      return _this();
    }

    public B enable(StreamWriteFeature first, StreamWriteFeature... other) {
      _streamWriteFeatures |= first.getMask();
      for (StreamWriteFeature f : other) {
        _streamWriteFeatures |= f.getMask();
      }
      return _this();
    }

    public B disable(StreamWriteFeature f) {
      _streamWriteFeatures &= ~f.getMask();
      return _this();
    }

    public B disable(StreamWriteFeature first, StreamWriteFeature... other) {
      _streamWriteFeatures &= ~first.getMask();
      for (StreamWriteFeature f : other) {
        _streamWriteFeatures &= ~f.getMask();
      }
      return _this();
    }

    public B configure(StreamWriteFeature f, boolean state) {
      return state ? enable(f) : disable(f);
    }

    public abstract F build();

    @SuppressWarnings(value = { "unchecked" }) protected final B _this() {
      return (B) this;
    }
  }

  protected final static int DEFAULT_FACTORY_FEATURE_FLAGS = TokenStreamFactory.Feature.collectDefaults();

  protected final static int DEFAULT_STREAM_READ_FEATURE_FLAGS = StreamReadFeature.collectDefaults();

  protected final static int DEFAULT_STREAM_WRITE_FEATURE_FLAGS = StreamWriteFeature.collectDefaults();

  protected final int _factoryFeatures;

  protected final int _streamReadFeatures;

  protected final int _streamWriteFeatures;

  protected final int _formatReadFeatures;

  protected final int _formatWriteFeatures;

  protected TokenStreamFactory(int formatReadFeatures, int formatWriteFeatures) {
    _factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
    _streamReadFeatures = DEFAULT_STREAM_READ_FEATURE_FLAGS;
    _streamWriteFeatures = DEFAULT_STREAM_WRITE_FEATURE_FLAGS;
    _formatReadFeatures = formatReadFeatures;
    _formatWriteFeatures = formatWriteFeatures;
  }

  protected TokenStreamFactory(TSFBuilder<?, ?> baseBuilder) {
    _factoryFeatures = baseBuilder.factoryFeaturesMask();
    _streamReadFeatures = baseBuilder.streamReadFeaturesMask();
    _streamWriteFeatures = baseBuilder.streamWriteFeaturesMask();
    _formatReadFeatures = baseBuilder.formatReadFeaturesMask();
    _formatWriteFeatures = baseBuilder.formatWriteFeaturesMask();
  }

  protected TokenStreamFactory(TokenStreamFactory src) {
    _factoryFeatures = src._factoryFeatures;
    _streamReadFeatures = src._streamReadFeatures;
    _streamWriteFeatures = src._streamWriteFeatures;
    _formatReadFeatures = src._formatReadFeatures;
    _formatWriteFeatures = src._formatWriteFeatures;
  }

  public abstract TokenStreamFactory copy();

  @Override public abstract TokenStreamFactory snapshot();

  public abstract TSFBuilder<?, ?> rebuild();

  public boolean requiresPropertyOrdering() {
    return false;
  }

  public abstract boolean canHandleBinaryNatively();

  public abstract boolean canParseAsync();

  public Class<? extends FormatFeature> getFormatReadFeatureType() {
    return null;
  }

  public Class<? extends FormatFeature> getFormatWriteFeatureType() {
    return null;
  }

  public abstract boolean canUseSchema(FormatSchema schema);

  public abstract String getFormatName();

  @Override public abstract Version version();

  public final boolean isEnabled(TokenStreamFactory.Feature f) {
    return (_factoryFeatures & f.getMask()) != 0;
  }

  public final boolean isEnabled(StreamReadFeature f) {
    return (_streamReadFeatures & f.getMask()) != 0;
  }

  public final boolean isEnabled(StreamWriteFeature f) {
    return (_streamWriteFeatures & f.getMask()) != 0;
  }

  public final int getStreamReadFeatures() {
    return _streamReadFeatures;
  }

  public final int getStreamWriteFeatures() {
    return _streamWriteFeatures;
  }

  public int getFormatReadFeatures() {
    return _formatReadFeatures;
  }

  public int getFormatWriteFeatures() {
    return _formatWriteFeatures;
  }

  public PropertyNameMatcher constructNameMatcher(List<Named> matches, boolean alreadyInterned) {
    return SimpleNameMatcher.constructFrom(null, matches, alreadyInterned);
  }

  public PropertyNameMatcher constructCINameMatcher(List<Named> matches, boolean alreadyInterned, Locale locale) {
    return SimpleNameMatcher.constructCaseInsensitive(locale, matches, alreadyInterned);
  }

  public abstract JsonParser createParser(ObjectReadContext readCtxt, File f) throws JacksonException;

  public abstract JsonParser createParser(ObjectReadContext readCtxt, Path p) throws JacksonException;

  public abstract JsonParser createParser(ObjectReadContext readCtxt, URL url) throws JacksonException;

  public abstract JsonParser createParser(ObjectReadContext readCtxt, InputStream in) throws JacksonException;

  public abstract JsonParser createParser(ObjectReadContext readCtxt, Reader r) throws JacksonException;

  public JsonParser createParser(ObjectReadContext readCtxt, byte[] data) throws JacksonException {
    return createParser(readCtxt, data, 0, data.length);
  }

  public abstract JsonParser createParser(ObjectReadContext readCtxt, byte[] content, int offset, int len) throws JacksonException;

  public abstract JsonParser createParser(ObjectReadContext readCtxt, String content) throws JacksonException;

  public JsonParser createParser(ObjectReadContext readCtxt, char[] content) throws JacksonException {
    return createParser(readCtxt, content, 0, content.length);
  }

  public abstract JsonParser createParser(ObjectReadContext readCtxt, char[] content, int offset, int len) throws JacksonException;

  @Deprecated public JsonParser createParser(File f) throws JacksonException {
    return createParser(ObjectReadContext.empty(), f);
  }

  @Deprecated public JsonParser createParser(URL src) throws JacksonException {
    return createParser(ObjectReadContext.empty(), src);
  }

  @Deprecated public JsonParser createParser(InputStream in) throws JacksonException {
    return createParser(ObjectReadContext.empty(), in);
  }

  @Deprecated public JsonParser createParser(Reader r) throws JacksonException {
    return createParser(ObjectReadContext.empty(), r);
  }

  @Deprecated public JsonParser createParser(byte[] content) throws JacksonException {
    return createParser(ObjectReadContext.empty(), content, 0, content.length);
  }

  @Deprecated public JsonParser createParser(byte[] content, int offset, int len) throws JacksonException {
    return createParser(ObjectReadContext.empty(), content, offset, len);
  }

  @Deprecated public JsonParser createParser(String content) throws JacksonException {
    return createParser(ObjectReadContext.empty(), content);
  }

  @Deprecated public JsonParser createParser(char[] content) throws JacksonException {
    return createParser(ObjectReadContext.empty(), content, 0, content.length);
  }

  @Deprecated public JsonParser createParser(char[] content, int offset, int len) throws JacksonException {
    return createParser(ObjectReadContext.empty(), content, offset, len);
  }

  public abstract JsonParser createParser(ObjectReadContext readCtxt, DataInput in) throws JacksonException;

  public <P extends JsonParser & ByteArrayFeeder> P createNonBlockingByteArrayParser(ObjectReadContext readCtxt) throws JacksonException {
    return _unsupported("Non-blocking source not (yet?) support for this format (" + getFormatName() + ")");
  }

  public JsonGenerator createGenerator(ObjectWriteContext writeCtxt, OutputStream out) throws JacksonException {
    return createGenerator(writeCtxt, out, JsonEncoding.UTF8);
  }

  public abstract JsonGenerator createGenerator(ObjectWriteContext writeCtxt, OutputStream out, JsonEncoding enc) throws JacksonException;

  public abstract JsonGenerator createGenerator(ObjectWriteContext writeCtxt, Writer w) throws JacksonException;

  public abstract JsonGenerator createGenerator(ObjectWriteContext writeCtxt, File f, JsonEncoding enc) throws JacksonException;

  public abstract JsonGenerator createGenerator(ObjectWriteContext writeCtxt, Path p, JsonEncoding enc) throws JacksonException;

  public JsonGenerator createGenerator(ObjectWriteContext writeCtxt, DataOutput out) throws JacksonException {
    return createGenerator(writeCtxt, _createDataOutputWrapper(out));
  }

  @Deprecated public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws JacksonException {
    return createGenerator(ObjectWriteContext.empty(), out, enc);
  }

  @Deprecated public JsonGenerator createGenerator(OutputStream out) throws JacksonException {
    return createGenerator(ObjectWriteContext.empty(), out, JsonEncoding.UTF8);
  }

  @Deprecated public JsonGenerator createGenerator(Writer w) throws JacksonException {
    return createGenerator(ObjectWriteContext.empty(), w);
  }

  public BufferRecycler _getBufferRecycler() {
    if (Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING.enabledIn(_factoryFeatures)) {
      return BufferRecyclers.getBufferRecycler();
    }
    return new BufferRecycler();
  }

  protected IOContext _createContext(ContentReference contentRef, boolean resourceManaged) {
    return new IOContext(_getBufferRecycler(), contentRef, resourceManaged, null);
  }

  protected IOContext _createContext(ContentReference contentRef, boolean resourceManaged, JsonEncoding enc) {
    return new IOContext(_getBufferRecycler(), contentRef, resourceManaged, enc);
  }

  protected abstract ContentReference _createContentReference(Object contentRef);

  protected abstract ContentReference _createContentReference(Object contentRef, int offset, int length);

  protected OutputStream _createDataOutputWrapper(DataOutput out) {
    return new DataOutputAsStream(out);
  }

  protected InputStream _optimizedStreamFromURL(URL url) throws JacksonException {
    if ("file".equals(url.getProtocol())) {
      String host = url.getHost();
      if (host == null || host.length() == 0) {
        String path = url.getPath();
        if (path.indexOf('%') < 0) {
          try {
            return new FileInputStream(url.getPath());
          } catch (IOException e) {
            throw _wrapIOFailure(e);
          }
        }
      }
    }
    try {
      return url.openStream();
    } catch (IOException e) {
      throw _wrapIOFailure(e);
    }
  }

  protected InputStream _fileInputStream(File f) throws JacksonException {
    try {
      return new FileInputStream(f);
    } catch (IOException e) {
      throw _wrapIOFailure(e);
    }
  }

  protected InputStream _pathInputStream(Path p) throws JacksonException {
    try {
      return Files.newInputStream(p);
    } catch (IOException e) {
      throw _wrapIOFailure(e);
    }
  }

  protected OutputStream _fileOutputStream(File f) throws JacksonException {
    try {
      return new FileOutputStream(f);
    } catch (IOException e) {
      throw _wrapIOFailure(e);
    }
  }

  protected OutputStream _pathOutputStream(Path p) throws JacksonException {
    try {
      return Files.newOutputStream(p);
    } catch (IOException e) {
      throw _wrapIOFailure(e);
    }
  }

  protected JacksonException _wrapIOFailure(IOException e) {
    return WrappedIOException.construct(e, this);
  }

  protected <T extends java.lang.Object> T _unsupported() {
    return _unsupported("Operation not supported for this format (%s)", getFormatName());
  }

  protected <T extends java.lang.Object> T _unsupported(String str, Object... args) {
    throw new UnsupportedOperationException(String.format(str, args));
  }

  protected InputStream _streamFromFile(File f) throws IOException {
    return new FileInputStream(f);
  }
}