package com.premiumminds.billy.core.util;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented @Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.METHOD }) public @interface NotOnUpdate {
  String message() default "Enitity is marked as not new, can\'t update this field!";
}