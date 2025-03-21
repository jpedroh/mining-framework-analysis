package ninja.validation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@WithValidator(value = Validators.RequiredValidator.class) @Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.PARAMETER }) @Deprecated public @interface Required {
  String key() default "validation.required.violation";

  String message() default "{0} is required";

  String fieldKey() default "";
}