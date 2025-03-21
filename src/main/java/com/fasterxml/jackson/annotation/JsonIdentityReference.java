package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation that can be used for customizing details of a reference
 * to Objects for which "Object Identity" is enabled (see {@link JsonIdentityInfo}).
 * The main use case is that of enforcing use of Object Id even for the first
 * time an Object is referenced, instead of first instance being serialized
 * as full POJO.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE,
    ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIdentityReference
{
    /**
     * Marker to indicate whether all referenced values are to
     * be serialized as ids (true); or by serializing the
     * first encountered reference as POJO and only then as id (false).
     *<p>
     * Note that if value of 'true' is used, deserialization may require
     * additional contextual information, and possibly using a custom
     * id resolver -- the default handling may not be sufficient.
<<<<<<< /usr/src/app/output/fasterxml/jackson-annotations/7751bf6722ef91c8b8083a2700d6290bb1074b9c/src/main/java/com/fasterxml/jackson/annotation/JsonIdentityReference.java/left.java
||||||| /usr/src/app/output/fasterxml/jackson-annotations/7751bf6722ef91c8b8083a2700d6290bb1074b9c/src/main/java/com/fasterxml/jackson/annotation/JsonIdentityReference.java/base.java
     * 
     * @since 2.1
=======
     *
     * @since 2.1
>>>>>>> /usr/src/app/output/fasterxml/jackson-annotations/7751bf6722ef91c8b8083a2700d6290bb1074b9c/src/main/java/com/fasterxml/jackson/annotation/JsonIdentityReference.java/right.java
     */
    public boolean alwaysAsId() default false;
}
