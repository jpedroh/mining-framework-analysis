package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation (annotations used on other annotations)
 * used for indicating that instead of using target annotation
 * (annotation annotated with this annotation),
 * Jackson should use meta-annotations it has.
 * This can be useful in creating "combo-annotations" by having
 * a container annotation, which needs to be annotated with this
 * annotation as well as all annotations it 'contains'.
<<<<<<< /usr/src/app/output/fasterxml/jackson-annotations/7751bf6722ef91c8b8083a2700d6290bb1074b9c/src/main/java/com/fasterxml/jackson/annotation/JacksonAnnotationsInside.java/left.java
||||||| /usr/src/app/output/fasterxml/jackson-annotations/7751bf6722ef91c8b8083a2700d6290bb1074b9c/src/main/java/com/fasterxml/jackson/annotation/JacksonAnnotationsInside.java/base.java
 * 
 * @since 2.0
=======
 *
 * @since 2.0
>>>>>>> /usr/src/app/output/fasterxml/jackson-annotations/7751bf6722ef91c8b8083a2700d6290bb1074b9c/src/main/java/com/fasterxml/jackson/annotation/JacksonAnnotationsInside.java/right.java
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JacksonAnnotationsInside
{

}
