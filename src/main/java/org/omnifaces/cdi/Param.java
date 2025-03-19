package org.omnifaces.cdi;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.beans.PropertyEditor;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.annotation.PostConstruct;
import javax.enterprise.util.Nonbinding;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.validator.BeanValidator;
import javax.faces.validator.RequiredValidator;
import javax.faces.validator.Validator;
import javax.inject.Qualifier;
import org.omnifaces.cdi.param.Attribute;
import org.omnifaces.cdi.param.DynamicParamValueProducer;
import org.omnifaces.cdi.param.ParamExtension;
import org.omnifaces.cdi.param.ParamValue;
import org.omnifaces.cdi.param.RequestParameterProducer;
import org.omnifaces.util.Utils;

@Qualifier @Retention(value = RUNTIME) @Target(value = { TYPE, METHOD, FIELD, PARAMETER }) public @interface Param {
  @Nonbinding String name() default "";

  @Nonbinding String label() default "";

  @Nonbinding String converter() default "";

  @Nonbinding boolean required() default false;

  @Nonbinding String[] validators() default {  };

  @Nonbinding Class<? extends Converter> converterClass() default Converter.class;

  @Nonbinding Class<? extends Validator>[] validatorClasses() default {  };

  @Nonbinding Attribute[] converterAttributes() default {  };

  @Nonbinding Attribute[] validatorAttributes() default {  };

  @Nonbinding String converterMessage() default "";

  @Nonbinding String validatorMessage() default "";

  @Nonbinding String requiredMessage() default "";

  @Nonbinding boolean disableBeanValidation() default false;

  @Nonbinding boolean overrideGlobalBeanValidationDisabled() default false;
}