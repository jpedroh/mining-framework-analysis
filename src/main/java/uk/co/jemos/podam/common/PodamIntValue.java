package uk.co.jemos.podam.common;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented @Target(value = { ElementType.FIELD, ElementType.PARAMETER }) @PodamAnnotation @Retention(value = RetentionPolicy.RUNTIME) public @interface PodamIntValue {
  int minValue() default 0;

  int maxValue() default 0;

  String comment() default "";

  String numValue() default "";
}