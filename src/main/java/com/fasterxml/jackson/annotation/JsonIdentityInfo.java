package com.fasterxml.jackson.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER }) @Retention(value = RetentionPolicy.RUNTIME) @JacksonAnnotation public @interface JsonIdentityInfo {
  public String property() default "@id";

  public Class<? extends ObjectIdGenerator<?>> generator();

  public Class<? extends ObjectIdResolver> resolver() default SimpleObjectIdResolver.class;

  public Class<?> scope() default Object.class;
}