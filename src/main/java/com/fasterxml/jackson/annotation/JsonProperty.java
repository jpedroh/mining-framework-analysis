package com.fasterxml.jackson.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER }) @Retention(value = RetentionPolicy.RUNTIME) @JacksonAnnotation public @interface JsonProperty {
  public final static String USE_DEFAULT_NAME = "";

  public final static int INDEX_UNKNOWN = -1;

  String value() default USE_DEFAULT_NAME;

  boolean required() default false;

  int index() default INDEX_UNKNOWN;

  String defaultValue() default "";

  Access access() default Access.AUTO;

  public enum Access {
    AUTO,
    READ_ONLY,
    WRITE_ONLY,
    READ_WRITE
  }
}