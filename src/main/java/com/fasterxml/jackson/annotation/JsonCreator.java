package com.fasterxml.jackson.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR }) @Retention(value = RetentionPolicy.RUNTIME) @JacksonAnnotation public @interface JsonCreator {
  public Mode mode() default Mode.DEFAULT;

  public enum Mode {
    DEFAULT,
    DELEGATING,
    PROPERTIES,
    DISABLED
  }
}