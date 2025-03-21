package com.speedment.codegen.lang.models.modifiers;
import com.speedment.codegen.lang.models.modifiers.Keyword.public_;

/**
 *
 * @author Duncan
 * @param <T>
 */
public interface AnnotationModifier<T extends AnnotationModifier<T>> extends public_<T> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T public_() {
    getModifiers().add(PUBLIC);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/AnnotationModifier.java/right.java
}