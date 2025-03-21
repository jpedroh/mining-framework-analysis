package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can be used to define a non-static,
 * no-argument method to be an "any getter"; accessor for getting
 * a set of key/value pairs, to be serialized as part of containing POJO
 * (similar to unwrapping) along with regular property values it has.
 * This typically serves as a counterpart
 * to "any setter" mutators (see {@link JsonAnySetter}).
 * Note that the return type of annotated methods <b>must</b> be
 * {@link java.util.Map}).
 *<p>
 * As with {@link JsonAnySetter}, only one property should be annotated
 * with this annotation; if multiple methods are annotated, an exception
 * may be thrown.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAnyGetter
{
    /**
     * Optional argument that defines whether this annotation is active
     * or not. The only use for value 'false' if for overriding purposes.
     * Overriding may be necessary when used
     * with "mix-in annotations" (aka "annotation overrides").
     * For most cases, however, default value of "true" is just fine
     * and should be omitted.
<<<<<<< /usr/src/app/output/fasterxml/jackson-annotations/ea0e95f247f31092c4593d9604ba55fb7f085e0f/src/main/java/com/fasterxml/jackson/annotation/JsonAnyGetter.java/left.java
||||||| /usr/src/app/output/fasterxml/jackson-annotations/ea0e95f247f31092c4593d9604ba55fb7f085e0f/src/main/java/com/fasterxml/jackson/annotation/JsonAnyGetter.java/base.java
     *
     * @since 2.9
=======
     *
     * @return True if annotation is enabled (normal case); false if it is to
     *   be ignored (only useful for mix-in annotations to "mask" annotation
     *
     * @since 2.9
>>>>>>> /usr/src/app/output/fasterxml/jackson-annotations/ea0e95f247f31092c4593d9604ba55fb7f085e0f/src/main/java/com/fasterxml/jackson/annotation/JsonAnyGetter.java/right.java
     */
    boolean enabled() default true;
}
