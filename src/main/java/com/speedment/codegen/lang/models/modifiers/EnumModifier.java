package com.speedment.codegen.lang.models.modifiers;
import com.speedment.codegen.lang.models.modifiers.Keyword.private_;
import com.speedment.codegen.lang.models.modifiers.Keyword.protected_;
import com.speedment.codegen.lang.models.modifiers.Keyword.public_;
import com.speedment.codegen.lang.models.modifiers.Keyword.static_;

/**
 *
 * @author Emil Forslund
 * @param <T>
 */
public interface EnumModifier<T extends EnumModifier<T>> extends public_<T>, protected_<T>, private_<T>, static_<T> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T public_() {
    getModifiers().add(PUBLIC);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/EnumModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T protected_() {
    getModifiers().add(PROTECTED);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/EnumModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T private_() {
    getModifiers().add(PRIVATE);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/EnumModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T static_() {
    getModifiers().add(STATIC);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/EnumModifier.java/right.java
}