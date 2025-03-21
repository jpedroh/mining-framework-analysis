package com.ryantenney.metrics.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Deprecated @com.codahale.metrics.annotation.Metric @Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.FIELD }) public @interface Metric {
  String name() default "";

  boolean absolute() default false;
}