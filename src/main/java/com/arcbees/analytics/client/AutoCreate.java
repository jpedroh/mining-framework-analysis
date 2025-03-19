package com.arcbees.analytics.client;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import com.google.inject.BindingAnnotation;

@BindingAnnotation @Target(value = { PARAMETER }) @Retention(value = RUNTIME) @interface AutoCreate {
}