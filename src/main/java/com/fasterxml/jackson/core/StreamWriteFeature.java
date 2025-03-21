package com.fasterxml.jackson.core;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;

/**
 * Token writer (generator) features not-specific to any particular format backend.
 *<p>
 * NOTE: Jackson 2.x contained these along with JSON-specific features in <code>JsonGenerator.Feature</code>.
 */public enum StreamWriteFeature {
  AUTO_CLOSE_TARGET(true),
  AUTO_CLOSE_CONTENT(true),
  FLUSH_PASSED_TO_STREAM(true),

<<<<<<< /usr/src/app/output/fasterxml/jackson-core/1755de36114aad71607db1b42c58714631d470a6/src/main/java/com/fasterxml/jackson/core/StreamWriteFeature.java/left.java
  WRITE_NUMBERS_AS_STRINGS(false)
=======
>>>>>>> Unknown file: This is a bug in JDime.
  ,
  WRITE_BIGDECIMAL_AS_PLAIN(false),
  STRICT_DUPLICATE_DETECTION(false),
  IGNORE_UNKNOWN(false)
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

  public boolean enabledByDefault() {
    return _defaultState;
  }

  public boolean enabledIn(int flags) {
    return (flags & _mask) != 0;
  }

  public int getMask() {
    return _mask;
  }
}