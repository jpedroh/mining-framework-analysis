package com.speedment.codegen.lang.models.modifiers;
import com.speedment.codegen.lang.models.modifiers.Keyword.private_;
import com.speedment.codegen.lang.models.modifiers.Keyword.protected_;
import com.speedment.codegen.lang.models.modifiers.Keyword.public_;

/**
 *
 * @author Emil Forslund
 * @param <T>
 */
public interface ConstructorModifier<T extends ConstructorModifier<T>> extends public_<T>, protected_<T>, private_<T> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T public_() {
    getModifiers().add(PUBLIC);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ConstructorModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T protected_() {
    getModifiers().add(PROTECTED);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ConstructorModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T private_() {
    getModifiers().add(PRIVATE);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ConstructorModifier.java/right.java
}