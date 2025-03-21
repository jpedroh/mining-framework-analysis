package com.ryantenney.metrics.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Deprecated @com.codahale.metrics.annotation.Counted @Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.METHOD }) public @interface Counted {
  String name() default "";

  boolean absolute() default false;

  boolean monotonic() default false;
}