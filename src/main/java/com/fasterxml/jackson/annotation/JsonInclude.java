package com.fasterxml.jackson.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER }) @Retention(value = RetentionPolicy.RUNTIME) @JacksonAnnotation public @interface JsonInclude {
  public Include value() default Include.USE_DEFAULTS;

  public Include content() default Include.USE_DEFAULTS;

  public Class<?> valueFilter() default Void.class;

  public Class<?> contentFilter() default Void.class;

  public enum Include {
    ALWAYS,
    NON_NULL,
    NON_ABSENT,
    NON_EMPTY,
    NON_DEFAULT,
    CUSTOM,
    USE_DEFAULTS
  }

  public static class Value implements JacksonAnnotationValue<JsonInclude>, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    protected final static Value EMPTY = new Value(Include.USE_DEFAULTS, Include.USE_DEFAULTS, null, null);

    protected final Include _valueInclusion;

    protected final Include _contentInclusion;

    protected final Class<?> _valueFilter;

    protected final Class<?> _contentFilter;

    public Value(JsonInclude src) {
      this(src.value(), src.content(), src.valueFilter(), src.contentFilter());
    }

    protected Value(Include vi, Include ci, Class<?> valueFilter, Class<?> contentFilter) {
      _valueInclusion = (vi == null) ? Include.USE_DEFAULTS : vi;
      _contentInclusion = (ci == null) ? Include.USE_DEFAULTS : ci;
      _valueFilter = (valueFilter == Void.class) ? null : valueFilter;
      _contentFilter = (contentFilter == Void.class) ? null : contentFilter;
    }

    public static Value empty() {
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

    protected Object readResolve() {
      if ((_valueInclusion == Include.USE_DEFAULTS) && (_contentInclusion == Include.USE_DEFAULTS) && (_valueFilter == null) && (_contentFilter == null)) {
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
      Class<?> vf = overrides._valueFilter;
      Class<?> cf = overrides._contentFilter;
      boolean viDiff = (vi != _valueInclusion) && (vi != Include.USE_DEFAULTS);
      boolean ciDiff = (ci != _contentInclusion) && (ci != Include.USE_DEFAULTS);
      boolean filterDiff = (vf != _valueFilter) || (cf != _valueFilter);
      if (viDiff) {
        if (ciDiff) {
          return new Value(vi, ci, vf, cf);
        }
        return new Value(vi, _contentInclusion, vf, cf);
      } else {
        if (ciDiff) {
          return new Value(_valueInclusion, ci, vf, cf);
        } else {
          if (filterDiff) {
            return new Value(_valueInclusion, _contentInclusion, vf, cf);
          }
        }
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
      return new Value(valueIncl, contentIncl, null, null);
    }

    /**
         * Factory method to use for constructing an instance for components
         */
    public static Value construct(Include valueIncl, Include contentIncl, Class<?> valueFilter, Class<?> contentFilter) {
      if (valueFilter == Void.class) {
        valueFilter = null;
      }
      if (contentFilter == Void.class) {
        contentFilter = null;
      }
      if (((valueIncl == Include.USE_DEFAULTS) || (valueIncl == null)) && ((contentIncl == Include.USE_DEFAULTS) || (contentIncl == null)) && (valueFilter == null) && (contentFilter == null)) {
        return EMPTY;
      }
      return new Value(valueIncl, contentIncl, valueFilter, contentFilter);
    }

    /**
         * Factory method to use for constructing an instance from instance of
         * {@link JsonInclude}
         */
    public static Value from(JsonInclude src) {
      if (src == null) {
        return EMPTY;
      }
      Include vi = src.value();
      Include ci = src.content();
      if ((vi == Include.USE_DEFAULTS) && (ci == Include.USE_DEFAULTS)) {
        return EMPTY;
      }
      Class<?> vf = src.valueFilter();
      if (vf == Void.class) {
        vf = null;
      }
      Class<?> cf = src.contentFilter();
      if (cf == Void.class) {
        cf = null;
      }
      return new Value(vi, ci, vf, cf);
    }

    public Value withValueInclusion(Include incl) {
      return (incl == _valueInclusion) ? this : new Value(incl, _contentInclusion, _valueFilter, _contentFilter);
    }

    /**
         * Mutant factory that will either
         *<ul>
         * <li>Set <code>value</code> as <code>USE_DEFAULTS</code>
         * and <code>valueFilter</code> to <code>filter</code> (if filter not null);
         * or</li>
         * <li>Set <code>value</code> as <code>ALWAYS</code> (if filter null)
         *  </li>
         *  </ul>
         */
    public Value withValueFilter(Class<?> filter) {
      Include incl;
      if (filter == null || filter == Void.class) {
        incl = Include.USE_DEFAULTS;
        filter = null;
      } else {
        incl = Include.CUSTOM;
      }
      return construct(incl, _contentInclusion, filter, _contentFilter);
    }

    /**
         * Mutant factory that will either
         *<ul>
         * <li>Set <code>content</code> as <code>USE_DEFAULTS</code>
         * and <code>contentFilter</code> to <code>filter</code> (if filter not null);
         * or</li>
         * <li>Set <code>content</code> as <code>ALWAYS</code> (if filter null)
         *  </li>
         *  </ul>
         */
    public Value withContentFilter(Class<?> filter) {
      Include incl;
      if (filter == null || filter == Void.class) {
        incl = Include.USE_DEFAULTS;
        filter = null;
      } else {
        incl = Include.CUSTOM;
      }
      return construct(_valueInclusion, incl, _valueFilter, filter);
    }

    public Value withContentInclusion(Include incl) {
      return (incl == _contentInclusion) ? this : new Value(_valueInclusion, incl, _valueFilter, _contentFilter);
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

    public Class<?> getValueFilter() {
      return _valueFilter;
    }

    public Class<?> getContentFilter() {
      return _contentFilter;
    }

    @Override public String toString() {
      StringBuilder sb = new StringBuilder(80);
      sb.append("JsonInclude.Value(value=").append(_valueInclusion).append(",content=").append(_contentInclusion);
      if (_valueFilter != null) {
        sb.append(",valueFilter=").append(_valueFilter.getName()).append(".class");
      }
      if (_contentFilter != null) {
        sb.append(",contentFilter=").append(_contentFilter.getName()).append(".class");
      }
      return sb.append(')').toString();
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
      return (other._valueInclusion == _valueInclusion) && (other._contentInclusion == _contentInclusion) && (other._valueFilter == _valueFilter) && (other._contentFilter == _contentFilter);
    }
  }
}