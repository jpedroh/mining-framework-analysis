package com.fasterxml.jackson.core.json;
import com.fasterxml.jackson.core.*;

/**
 * Token reader (parser) features specific to JSON backend.
 *<p>
 * NOTE: Jackson 2.x had these mixed with non-JSON-specific features within
 * <code>JsonParser.Feature</code> enumeration.
 */public enum JsonReadFeature implements FormatFeature {
  ALLOW_JAVA_COMMENTS(false),
  ALLOW_YAML_COMMENTS(false),
  ALLOW_SINGLE_QUOTES(false),
  ALLOW_UNQUOTED_FIELD_NAMES(false),
  ALLOW_UNESCAPED_CONTROL_CHARS(false),
  ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER(false),
  ALLOW_LEADING_ZEROS_FOR_NUMBERS(false),
  ALLOW_NON_NUMERIC_NUMBERS(false),
  ALLOW_MISSING_VALUES(false),
  ALLOW_TRAILING_COMMA(false)
  ;

  final private boolean _defaultState;

  final private int _mask;

  /**
     * Method that calculates bit set (flags) of all features that
     * are enabled by default.
     */
  public static int collectDefaults() {
    int flags = 0;
    for (JsonReadFeature f : values()) {
      if (f.enabledByDefault()) {
        flags |= f.getMask();
      }
    }
    return flags;
  }

  private JsonReadFeature(boolean defaultState) {
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