package com.fasterxml.jackson.core.json;
import com.fasterxml.jackson.core.*;

/**
 * Token writer features specific to JSON backend.
 */public enum JsonWriteFeature implements FormatFeature {
  QUOTE_FIELD_NAMES(true),
  WRITE_NAN_AS_STRINGS(true),
  @SuppressWarnings(value = { "deprecation" }) WRITE_NUMBERS_AS_STRINGS(false, JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS),
  ESCAPE_NON_ASCII(false)
  ;

  final private boolean _defaultState;

  final private int _mask;

  /**
     * Method that calculates bit set (flags) of all features that
     * are enabled by default.
     */
  public static int collectDefaults() {
    int flags = 0;
    for (JsonWriteFeature f : values()) {
      if (f.enabledByDefault()) {
        flags |= f.getMask();
      }
    }
    return flags;
  }

  private JsonWriteFeature(boolean defaultState) {
    _defaultState = defaultState;
    _mask = (1 << ordinal());
  }

  @Override public boolean enabledByDefault() {
    return _defaultState;
  }

  @Override public int getMask() {
    return _mask;
  }

  @Override public boolean enabledIn(int flags) {
    return (flags & _mask) != 0;
  }
}