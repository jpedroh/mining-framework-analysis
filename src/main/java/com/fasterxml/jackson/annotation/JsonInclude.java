package com.fasterxml.jackson.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER }) @Retention(value = RetentionPolicy.RUNTIME) @JacksonAnnotation public @interface JsonInclude {
  public Include value() default Include.ALWAYS;

  public Include content() default Include.ALWAYS;

  public enum Include {
    ALWAYS,
    NON_NULL,
    NON_ABSENT,
    NON_EMPTY,
    NON_DEFAULT,
    USE_DEFAULTS
  }

  public static class Value implements JacksonAnnotationValue<JsonInclude>, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    protected final static Value EMPTY = new Value(Include.USE_DEFAULTS, Include.USE_DEFAULTS);

    protected final Include _valueInclusion;

    protected final Include _contentInclusion;

    public Value(JsonInclude src) {
      this(src.value(), src.content());
    }

    protected Value(Include vi, Include ci) {
      _valueInclusion = (vi == null) ? Include.USE_DEFAULTS : vi;
      _contentInclusion = (ci == null) ? Include.USE_DEFAULTS : ci;
    }

    public static Value empty() {
      return EMPTY;
    }

    protected Object readResolve() {
      if ((_valueInclusion == Include.USE_DEFAULTS) && (_contentInclusion == Include.USE_DEFAULTS)) {
        return EMPTY;
      }
      return this;
    }

    /**
         * Mutant factory method that merges values of this value with given override
         * values, so that any explicitly defined inclusion in overrides has precedence over
         * settings of this value instance. If no overrides exist will return <code>this</code>
         * instance; otherwise new {@link Value} with changed inclusion values.
         */
    public Value withOverrides(Value overrides) {
      if ((overrides == null) || (overrides == EMPTY)) {
        return this;
      }
      Include vi = overrides._valueInclusion;
      Include ci = overrides._contentInclusion;
      boolean viDiff = (vi != _valueInclusion) && (vi != Include.USE_DEFAULTS);
      boolean ciDiff = (ci != _valueInclusion) && (ci != Include.USE_DEFAULTS);
      if (viDiff) {
        if (ciDiff) {
          return new Value(vi, ci);
        }
        return new Value(vi, _contentInclusion);
      }
      return this;
    }

    /**
         * Factory method to use for constructing an instance for components
         */
    public static Value construct(Include valueIncl, Include contentIncl) {
      if (((valueIncl == Include.USE_DEFAULTS) || (valueIncl == null)) && ((contentIncl == Include.USE_DEFAULTS) || (contentIncl == null))) {
        return EMPTY;
      }
      return new Value(valueIncl, contentIncl);
    }

    /**
         * Factory method to use for constructing an instance from instance of
         * {@link JsonInclude}
         */
    public static Value from(JsonInclude src) {
      if (src == null) {
        return null;
      }
      Include vi = src.value();
      Include ci = src.content();
      if ((vi == Include.USE_DEFAULTS) && (ci == Include.USE_DEFAULTS)) {
        return EMPTY;
      }
      return new Value(vi, ci);
    }

    public Value withValueInclusion(Include incl) {
      return (incl == _valueInclusion) ? this : new Value(incl, _contentInclusion);
    }

    public Value withContentInclusion(Include incl) {
      return (incl == _contentInclusion) ? this : new Value(_valueInclusion, incl);
    }

    @Override public Class<JsonInclude> valueFor() {
      return JsonInclude.class;
    }

    public Include getValueInclusion() {
      return _valueInclusion;
    }

    public Include getContentInclusion() {
      return _contentInclusion;
    }

    @Override public String toString() {
      return String.format("[value=%s,content=%s]", _valueInclusion, _contentInclusion);
    }

    @Override public int hashCode() {
      return (_valueInclusion.hashCode() << 2) + _contentInclusion.hashCode();
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
      return (other._valueInclusion == _valueInclusion) && (other._contentInclusion == _contentInclusion);
    }
  }
}