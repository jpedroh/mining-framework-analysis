package org.restheart.plugins;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME) public @interface RegisterPlugin {
  String name();

  String description();

  int priority() default 10;

  boolean enabledByDefault() default true;

  String defaultURI() default "";

  MATCH_POLICY uriMatchPolicy() default MATCH_POLICY.PREFIX;

  public enum MATCH_POLICY {
    EXACT,
    PREFIX
  }



  InterceptPoint interceptPoint() default InterceptPoint.REQUEST_AFTER_AUTH;

  InitPoint initPoint() default InitPoint.AFTER_STARTUP;

  boolean requiresContent() default false;

  InterceptPoint[] dontIntercept() default {  };
}