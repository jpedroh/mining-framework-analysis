package org.broadleafcommerce.common.presentation;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.OperationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.FIELD }) public @interface AdminPresentationCollection {
  String friendlyName() default "";

  String addFriendlyName() default "";

  String securityLevel() default "";

  boolean excluded() default false;

  boolean readOnly() default false;

  boolean useServerSideInspectionCache() default true;

  AddMethodType addType() default AddMethodType.PERSIST;

  String manyToField() default "";

  int order() default 99999;

  @Deprecated String tab() default "General";

  @Deprecated int tabOrder() default 99999;

  String[] customCriteria() default {  };

  AdminPresentationOperationTypes operationTypes() default @AdminPresentationOperationTypes(addType = OperationType.BASIC, fetchType = OperationType.BASIC, inspectType = OperationType.BASIC, removeType = OperationType.BASIC, updateType = OperationType.BASIC);

  String showIfProperty() default "";

  FieldValueConfiguration[] showIfFieldEquals() default {  };

  String currencyCodeField() default "";

  String sortProperty() default "";

  boolean sortAscending() default true;

  boolean lazyFetch() default true;

  boolean manualFetch() default false;

  String group() default "";

  String selectizeVisibleField() default "";
}