package com.fasterxml.jackson.core;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.util.JacksonFeature;

/**
 * Token writer (generator) features not-specific to any particular format backend.
 *<p>
 * NOTE: Jackson 2.x contained these along with JSON-specific features in <code>JsonGenerator.Feature</code>.
 */public enum StreamWriteFeature implements JacksonFeature {
  AUTO_CLOSE_TARGET(true),
  AUTO_CLOSE_CONTENT(true),
  FLUSH_PASSED_TO_STREAM(true),
  WRITE_BIGDECIMAL_AS_PLAIN(false),
  STRICT_DUPLICATE_DETECTION(false),
  IGNORE_UNKNOWN(false),
  @SuppressWarnings(value = { "deprecation" }) USE_FAST_DOUBLE_WRITER(JsonGenerator.Feature.USE_FAST_DOUBLE_WRITER)
  ;

  /**
     * Whether feature is enabled or disabled by default.
     */
  private final boolean _defaultState;

  private final int _mask;

  private StreamWriteFeature(boolean defaultState) {
    _mask = (1 << ordinal());
    _defaultState = defaultState;
  }

  /**
     * Method that calculates bit set (flags) of all features that
     * are enabled by default.
     *
     * @return Bit mask of all features that are enabled by default
     */
  public static int collectDefaults() {
    int flags = 0;
    for (StreamWriteFeature f : values()) {
      if (f.enabledByDefault()) {
        flags |= f.getMask();
      }
    }
    return flags;
  }

  @Override public boolean enabledByDefault() {
    return _defaultState;
  }

  @Override public boolean enabledIn(int flags) {
    return (flags & _mask) != 0;
  }

  @Override public int getMask() {
    return _mask;
  }
}