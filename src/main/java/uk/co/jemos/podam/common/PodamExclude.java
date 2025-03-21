package uk.co.jemos.podam.common;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented @Target(value = { ElementType.FIELD }) @PodamAnnotation @Retention(value = RetentionPolicy.RUNTIME) public @interface PodamExclude {
  String comment() default "";
}