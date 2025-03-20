package com.premiumminds.billy.core.util;
import java.util.Collection;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.commons.lang3.Validate;

public class BillyValidator extends Validate {
  private static BillyValidator instance = new BillyValidator();

  private Localizer localizer;

  private ValidatorFactory factory;

  private Validator validator;

  private BillyValidator() {
    this.localizer = new Localizer("com/premiumminds/billy/core/i18n/Validation");
    this.factory = Validation.buildDefaultValidatorFactory();
    this.validator = this.factory.getValidator();
  }

  public static void validateBeans(Object... objects) throws ValidationException {
    StringBuilder builder = new StringBuilder();
    boolean valid = true;
    for (Object o : objects) {
      Set<ConstraintViolation<Object>> violations = BillyValidator.instance.validator.validate(o);
      if (!violations.isEmpty()) {
        valid = false;
        builder.append("There was an exception while validating instance of ");
        builder.append(o.getClass().getCanonicalName());
        builder.append('\n');
        for (ConstraintViolation<Object> v : violations) {
          builder.append('\n');
          builder.append(v.getPropertyPath() + " - " + v.getMessage());
        }
      }
    }
    if (!valid) {
      throw new ValidationException(builder.toString());
    }
  }

  public static <T extends java.lang.Object> T mandatory(T o, String fieldName) {
    Validate.notNull(o, BillyValidator.instance.localizer.getString("invalid.mandatory", fieldName), o);
    return o;
  }

  public static <T extends CharSequence> T mandatory(T o, String fieldName) {
    Validate.notNull(o, BillyValidator.instance.localizer.getString("invalid.mandatory", fieldName), o);
    Validate.notBlank(o, BillyValidator.instance.localizer.getString("invalid.mandatory", fieldName), o);
    return o;
  }

  public static <T extends java.lang.Object> T notNull(T object, String fieldName) {
    Validate.notNull(object, BillyValidator.instance.localizer.getString("invalid.null", fieldName), fieldName);
    return object;
  }

  public static <T extends CharSequence> T notBlank(T object, String fieldName) {
    Validate.notBlank(object, BillyValidator.instance.localizer.getString("invalid.blank", fieldName), fieldName);
    return object;
  }

  public static <T extends CharSequence> T notBlankButNull(T object, String fieldName) {
    if (null != object) {
      Validate.notBlank(object, BillyValidator.instance.localizer.getString("invalid.blank", fieldName), fieldName);
    }
    return object;
  }

  public static <T extends Collection<?>> T notEmpty(T object, String fieldName) {
    Validate.notEmpty(object, BillyValidator.instance.localizer.getString("invalid.empty", fieldName), fieldName);
    return object;
  }

  public static <T extends java.lang.Object> T found(T o, String fieldName) {
    Validate.notNull(o, BillyValidator.instance.localizer.getString("invalid.not_found", fieldName), o);
    return o;
  }
}