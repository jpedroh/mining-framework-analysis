package com.fasterxml.jackson.annotation;
import java.lang.annotation.*;

@Target(value = { ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER }) @Retention(value = RetentionPolicy.RUNTIME) @JacksonAnnotation public @interface JsonSetter {
  String value() default "";

  Nulls nulls() default Nulls.DEFAULT;

  Nulls contentNulls() default Nulls.DEFAULT;

  public static class Value implements JacksonAnnotationValue<JsonSetter>, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final Nulls _nulls;

    private final Nulls _contentNulls;

    /**
         * Default instance used in place of "default settings".
         */
    protected final static Value EMPTY = new Value(Nulls.DEFAULT, Nulls.DEFAULT);

    protected Value(Nulls nulls, Nulls contentNulls) {
      _nulls = nulls;
      _contentNulls = contentNulls;
    }

    @Override public Class<JsonSetter> valueFor() {
      return JsonSetter.class;
    }

    protected Object readResolve() {
      if (_empty(_nulls, _contentNulls)) {
        return EMPTY;
      }
      return this;
    }

    public static Value from(JsonSetter src) {
      if (src == null) {
        return EMPTY;
      }
      return construct(src.nulls(), src.contentNulls());
    }

    /**
         * Factory method that may be used (although is NOT the recommended way)
         * to construct an instance from a full set of properties. Most users would
         * be better off starting by {@link #empty()} instance and using `withXxx`/`withoutXxx`
         * methods, as this factory method may need to be changed if new properties
         * are added in {@link JsonIgnoreProperties} annotation.
         */
    public static Value construct(Nulls nulls, Nulls contentNulls) {
      if (nulls == null) {
        nulls = Nulls.DEFAULT;
      }
      if (contentNulls == null) {
        contentNulls = Nulls.DEFAULT;
      }
      if (_empty(nulls, contentNulls)) {
        return EMPTY;
      }
      return new Value(nulls, contentNulls);
    }

    /**
         * Accessor for default instances which has "empty" settings; that is:
         *<ul>
         * <li>Null handling using global defaults, {@link Nulls#DEFAULT}.
         *  </li>
         * </ul>
         */
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

    public static Value forValueNulls(Nulls nulls) {
      return construct(nulls, Nulls.DEFAULT);
    }

    public static Value forValueNulls(Nulls nulls, Nulls contentNulls) {
      return construct(nulls, contentNulls);
    }

    public static Value forContentNulls(Nulls nulls) {
      return construct(Nulls.DEFAULT, nulls);
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
      Nulls nulls = overrides._nulls;
      Nulls contentNulls = overrides._contentNulls;
      if (nulls == Nulls.DEFAULT) {
        nulls = _nulls;
      }
      if (contentNulls == Nulls.DEFAULT) {
        contentNulls = _contentNulls;
      }
      if ((nulls == _nulls) && (contentNulls == _contentNulls)) {
        return this;
      }
      return construct(nulls, contentNulls);
    }

    public Value withValueNulls(Nulls nulls) {
      if (nulls == null) {
        nulls = Nulls.DEFAULT;
      }
      if (nulls == _nulls) {
        return this;
      }
      return construct(nulls, _contentNulls);
    }

    public Value withValueNulls(Nulls valueNulls, Nulls contentNulls) {
      if (valueNulls == null) {
        valueNulls = Nulls.DEFAULT;
      }
      if (contentNulls == null) {
        contentNulls = Nulls.DEFAULT;
      }
      if ((valueNulls == _nulls) && (contentNulls == _contentNulls)) {
        return this;
      }
      return construct(valueNulls, contentNulls);
    }

    public Value withContentNulls(Nulls nulls) {
      if (nulls == null) {
        nulls = Nulls.DEFAULT;
      }
      if (nulls == _contentNulls) {
        return this;
      }
      return construct(_nulls, nulls);
    }

    public Nulls getValueNulls() {
      return _nulls;
    }

    public Nulls getContentNulls() {
      return _contentNulls;
    }

    /**
         * Returns same as {@link #getValueNulls()} unless value would be
         * {@link Nulls#DEFAULT} in which case `null` is returned.
         */
    public Nulls nonDefaultValueNulls() {
      return (_nulls == Nulls.DEFAULT) ? null : _nulls;
    }

    /**
         * Returns same as {@link #getContentNulls()} unless value would be
         * {@link Nulls#DEFAULT} in which case `null` is returned.
         */
    public Nulls nonDefaultContentNulls() {
      return (_contentNulls == Nulls.DEFAULT) ? null : _contentNulls;
    }

    @Override public String toString() {
      return String.format("JsonSetter.Value(valueNulls=%s,contentNulls=%s)", _nulls, _contentNulls);
    }

    @Override public int hashCode() {
      return _nulls.ordinal() + (_contentNulls.ordinal() << 2);
    }

    @Override public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o == null) {
        return false;
      }
      if (o.getClass() == getClass()) {
        Value other = (Value) o;
        return (other._nulls == _nulls) && (other._contentNulls == _contentNulls);
      }
      return false;
    }

    private static boolean _empty(Nulls nulls, Nulls contentNulls) {
      return (nulls == Nulls.DEFAULT) && (contentNulls == Nulls.DEFAULT);
    }
  }
}