package com.cronutils.model.field.expression;
import com.google.common.base.MoreObjects;

/**
 * Represents a star (*) value on cron expression field
 */
public class Always extends FieldExpression {
  @SuppressWarnings(value = { "deprecation" }) static final Always INSTANCE = new Always();

  /**
     * Should be package private and not be instantiated elsewhere. Class should become package private too.
     * @deprecated rather use {@link FieldExpression#always()}
     */
  @Deprecated public Always() {
  }

  @Override public String asString() {
    return "*";
  }

  @Override public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }
}