package com.fasterxml.jackson.annotation;
import java.lang.annotation.*;
import java.util.Locale;
import java.util.TimeZone;

@Target(value = { ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE }) @Retention(value = RetentionPolicy.RUNTIME) @JacksonAnnotation public @interface JsonFormat {
  public final static String DEFAULT_LOCALE = "##default";

  public final static String DEFAULT_TIMEZONE = "##default";

  public String pattern() default "";

  public Shape shape() default Shape.ANY;

  public String locale() default DEFAULT_LOCALE;

  public String timezone() default DEFAULT_TIMEZONE;

  public OptBoolean lenient() default OptBoolean.DEFAULT;

  public JsonFormat.Feature[] with() default {  };

  public JsonFormat.Feature[] without() default {  };

  public enum Shape {
    BOOLEAN,
    ANY,
    NUMBER,
    NATURAL,
    NUMBER_FLOAT,
    SCALAR,
    NUMBER_INT,
    ARRAY,
    STRING,
    OBJECT,
    POJO,
    BINARY
    ;

    public boolean isNumeric() {
      return (this == NUMBER) || (this == NUMBER_INT) || (this == NUMBER_FLOAT);
    }

    /** @since 3.0 */
    public static boolean isNumeric(Shape shapeOrNull) {
      return (shapeOrNull != null) && shapeOrNull.isNumeric();
    }

    public boolean isStructured() {
      return (this == OBJECT) || (this == ARRAY) || (this == POJO);
    }

    /** @since 3.0 */
    public static boolean isStructured(Shape shapeOrNull) {
      return (shapeOrNull != null) && shapeOrNull.isStructured();
    }
  }

  public enum Feature {
    ACCEPT_SINGLE_VALUE_AS_ARRAY,
    ACCEPT_CASE_INSENSITIVE_PROPERTIES,
    ACCEPT_CASE_INSENSITIVE_VALUES,
    WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
    WRITE_DATES_WITH_ZONE_ID,
    WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED,
    WRITE_SORTED_MAP_ENTRIES,
    ADJUST_DATES_TO_CONTEXT_TIME_ZONE
  }

  public static class Features {
    private final int _enabled, _disabled;

    private final static Features EMPTY = new Features(0, 0);

    private Features(int e, int d) {
      _enabled = e;
      _disabled = d;
    }

    public static Features empty() {
      return EMPTY;
    }

    public static Features construct(JsonFormat f) {
      return construct(f.with(), f.without());
    }

    public static Features construct(Feature[] enabled, Feature[] disabled) {
      int e = 0;
      for (Feature f : enabled) {
        e |= (1 << f.ordinal());
      }
      int d = 0;
      for (Feature f : disabled) {
        d |= (1 << f.ordinal());
      }
      return new Features(e, d);
    }

    public Features withOverrides(Features overrides) {
      if (overrides == null) {
        return this;
      }
      int overrideD = overrides._disabled;
      int overrideE = overrides._enabled;
      if ((overrideD == 0) && (overrideE == 0)) {
        return this;
      }
      if ((_enabled == 0) && (_disabled == 0)) {
        return overrides;
      }
      int newE = (_enabled & ~overrideD) | overrideE;
      int newD = (_disabled & ~overrideE) | overrideD;
      if ((newE == _enabled) && (newD == _disabled)) {
        return this;
      }
      return new Features(newE, newD);
    }

    public Features with(Feature... features) {
      int e = _enabled;
      for (Feature f : features) {
        e |= (1 << f.ordinal());
      }
      return (e == _enabled) ? this : new Features(e, _disabled);
    }

    public Features without(Feature... features) {
      int d = _disabled;
      for (Feature f : features) {
        d |= (1 << f.ordinal());
      }
      return (d == _disabled) ? this : new Features(_enabled, d);
    }

    public Boolean get(Feature f) {
      int mask = (1 << f.ordinal());
      if ((_disabled & mask) != 0) {
        return Boolean.FALSE;
      }
      if ((_enabled & mask) != 0) {
        return Boolean.TRUE;
      }
      return null;
    }

    @Override public String toString() {
      if (this == EMPTY) {
        return "EMPTY";
      }
      return String.format("(enabled=0x%x,disabled=0x%x)", _enabled, _disabled);
    }

    @Override public int hashCode() {
      return _disabled + _enabled;
    }

    @Override public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o == null) {
        return false;
      }
      if (o.getClass() != getClass()) {
        return false;
      }
      Features other = (Features) o;
      return (other._enabled == _enabled) && (other._disabled == _disabled);
    }
  }

  public static class Value implements JacksonAnnotationValue<JsonFormat>, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final static Value EMPTY = new Value();

    private final String _pattern;

    private final Shape _shape;

    private final Locale _locale;

    private final String _timezoneStr;

    private final Boolean _lenient;

    private final Features _features;

    private transient TimeZone _timezone;

    public Value() {
      this("", Shape.ANY, "", "", Features.empty(), null);
    }

    public Value(JsonFormat ann) {
      this(ann.pattern(), ann.shape(), ann.locale(), ann.timezone(), Features.construct(ann), ann.lenient().asBoolean());
    }

    public Value(String p, Shape sh, String localeStr, String tzStr, Features f, Boolean lenient) {
      this(p, sh, (localeStr == null || localeStr.length() == 0 || DEFAULT_LOCALE.equals(localeStr)) ? null : new Locale(localeStr), (tzStr == null || tzStr.length() == 0 || DEFAULT_TIMEZONE.equals(tzStr)) ? null : tzStr, null, f, lenient);
    }

    public Value(String p, Shape sh, Locale l, TimeZone tz, Features f, Boolean lenient) {
      _pattern = p;
      _shape = (sh == null) ? Shape.ANY : sh;
      _locale = l;
      _timezone = tz;
      _timezoneStr = null;
      _features = (f == null) ? Features.empty() : f;
      _lenient = lenient;
    }

    public Value(String p, Shape sh, Locale l, String tzStr, TimeZone tz, Features f, Boolean lenient) {
      _pattern = p;
      _shape = (sh == null) ? Shape.ANY : sh;
      _locale = l;
      _timezone = tz;
      _timezoneStr = tzStr;
      _features = (f == null) ? Features.empty() : f;
      _lenient = lenient;
    }

    public final static Value empty() {
      return EMPTY;
    }

    /**
         * Helper method that will try to combine values from two {@link Value}
         * instances, using one as base settings, and the other as overrides
         * to use instead of base values when defined; base values are only
         * use if override does not specify a value (matching value is null
         * or logically missing).
         * Note that one or both of value instances may be `null`, directly;
         * if both are `null`, result will also be `null`; otherwise never null.
         */
    public static Value merge(Value base, Value overrides) {
      return (base == null) ? overrides : base.withOverrides(overrides);
    }

    public static Value mergeAll(Value... values) {
      Value result = null;
      for (Value curr : values) {
        if (curr != null) {
          result = (result == null) ? curr : result.withOverrides(curr);
        }
      }
      return result;
    }

    public final static Value from(JsonFormat ann) {
      return (ann == null) ? EMPTY : new Value(ann);
    }

    public final Value withOverrides(Value overrides) {
      if ((overrides == null) || (overrides == EMPTY) || (overrides == this)) {
        return this;
      }
      if (this == EMPTY) {
        return overrides;
      }
      String p = overrides._pattern;
      if ((p == null) || p.isEmpty()) {
        p = _pattern;
      }
      Shape sh = overrides._shape;
      if (sh == Shape.ANY) {
        sh = _shape;
      }
      Locale l = overrides._locale;
      if (l == null) {
        l = _locale;
      }
      Features f = _features;
      if (f == null) {
        f = overrides._features;
      } else {
        f = f.withOverrides(overrides._features);
      }
      Boolean lenient = overrides._lenient;
      if (lenient == null) {
        lenient = _lenient;
      }
      String tzStr = overrides._timezoneStr;
      TimeZone tz;
      if ((tzStr == null) || tzStr.isEmpty()) {
        tzStr = _timezoneStr;
        tz = _timezone;
      } else {
        tz = overrides._timezone;
      }
      return new Value(p, sh, l, tzStr, tz, f, lenient);
    }

    public static Value forPattern(String p) {
      return new Value(p, null, null, null, null, Features.empty(), null);
    }

    public static Value forShape(Shape sh) {
      return new Value(null, sh, null, null, null, Features.empty(), null);
    }

    public static Value forLeniency(boolean lenient) {
      return new Value(null, null, null, null, null, Features.empty(), Boolean.valueOf(lenient));
    }

    public Value withPattern(String p) {
      return new Value(p, _shape, _locale, _timezoneStr, _timezone, _features, _lenient);
    }

    public Value withShape(Shape s) {
      if (s == _shape) {
        return this;
      }
      return new Value(_pattern, s, _locale, _timezoneStr, _timezone, _features, _lenient);
    }

    public Value withLocale(Locale l) {
      return new Value(_pattern, _shape, l, _timezoneStr, _timezone, _features, _lenient);
    }

    public Value withTimeZone(TimeZone tz) {
      return new Value(_pattern, _shape, _locale, null, tz, _features, _lenient);
    }

    public Value withLenient(Boolean lenient) {
      if (lenient == _lenient) {
        return this;
      }
      return new Value(_pattern, _shape, _locale, _timezoneStr, _timezone, _features, lenient);
    }

    public Value withFeature(JsonFormat.Feature f) {
      Features newFeats = _features.with(f);
      return (newFeats == _features) ? this : new Value(_pattern, _shape, _locale, _timezoneStr, _timezone, newFeats, _lenient);
    }

    public Value withoutFeature(JsonFormat.Feature f) {
      Features newFeats = _features.without(f);
      return (newFeats == _features) ? this : new Value(_pattern, _shape, _locale, _timezoneStr, _timezone, newFeats, _lenient);
    }

    @Override public Class<JsonFormat> valueFor() {
      return JsonFormat.class;
    }

    public String getPattern() {
      return _pattern;
    }

    public Shape getShape() {
      return _shape;
    }

    public Locale getLocale() {
      return _locale;
    }

    /**
         * @return {@code Boolean.TRUE} if explicitly set to true; {@code Boolean.FALSE}
         *   if explicit set to false; or {@code null} if not set either way (assuming
         *   "default leniency" for the context)
         */
    public Boolean getLenient() {
      return _lenient;
    }

    /**
         * Convenience method equivalent to
         *<pre>
         *   Boolean.TRUE.equals(getLenient())
         *</pre>
         * that is, returns {@code true} if (and only if) leniency has been explicitly
         * set to {code true}; but not if it is undefined.
         */
    public boolean isLenient() {
      return Boolean.TRUE.equals(_lenient);
    }

    /**
         * Alternate access (compared to {@link #getTimeZone()}) which is useful
         * when caller just wants time zone id to convert, but not as JDK
         * provided {@link TimeZone}
         */
    public String timeZoneAsString() {
      if (_timezone != null) {
        return _timezone.getID();
      }
      return _timezoneStr;
    }

    public TimeZone getTimeZone() {
      TimeZone tz = _timezone;
      if (tz == null) {
        if (_timezoneStr == null) {
          return null;
        }
        tz = TimeZone.getTimeZone(_timezoneStr);
        _timezone = tz;
      }
      return tz;
    }

    public boolean hasShape() {
      return _shape != Shape.ANY;
    }

    public boolean hasPattern() {
      return (_pattern != null) && (_pattern.length() > 0);
    }

    public boolean hasLocale() {
      return _locale != null;
    }

    public boolean hasTimeZone() {
      return (_timezone != null) || (_timezoneStr != null && !_timezoneStr.isEmpty());
    }

    /**
         * Accessor for checking whether there is a setting for leniency.
         * NOTE: does NOT mean that `lenient` is `true` necessarily; just that
         * it has been set.
         */
    public boolean hasLenient() {
      return _lenient != null;
    }

    /**
         * Accessor for checking whether this format value has specific setting for
         * given feature. Result is 3-valued with either `null`, {@link Boolean#TRUE} or
         * {@link Boolean#FALSE}, indicating 'yes/no/dunno' choices, where `null` ("dunno")
         * indicates that the default handling should be used based on global defaults,
         * and there is no format override.
         */
    public Boolean getFeature(JsonFormat.Feature f) {
      return _features.get(f);
    }

    /**
         * Accessor for getting full set of features enabled/disabled.
         */
    public Features getFeatures() {
      return _features;
    }

    @Override public String toString() {
      return String.format("JsonFormat.Value(pattern=%s,shape=%s,lenient=%s,locale=%s,timezone=%s,features=%s)", _pattern, _shape, _lenient, _locale, _timezoneStr, _features);
    }

    @Override public int hashCode() {
      int hash = (_timezoneStr == null) ? 1 : _timezoneStr.hashCode();
      if (_pattern != null) {
        hash ^= _pattern.hashCode();
      }
      hash += _shape.hashCode();
      if (_lenient != null) {
        hash ^= _lenient.hashCode();
      }
      if (_locale != null) {
        hash += _locale.hashCode();
      }
      hash ^= _features.hashCode();
      return hash;
    }

    @Override public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o == null) {
        return false;
      }
      if (o.getClass() != getClass()) {
        return false;
      }
      Value other = (Value) o;
      if ((_shape != other._shape) || !_features.equals(other._features)) {
        return false;
      }
      return _equal(_lenient, other._lenient) && _equal(_timezoneStr, other._timezoneStr) && _equal(_pattern, other._pattern) && _equal(_timezone, other._timezone) && _equal(_locale, other._locale);
    }

    private static <T extends java.lang.Object> boolean _equal(T value1, T value2) {
      if (value1 == null) {
        return (value2 == null);
      }
      if (value2 == null) {
        return false;
      }
      return value1.equals(value2);
    }
  }
}