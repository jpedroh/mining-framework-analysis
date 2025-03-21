package com.speedment.codegen.lang.models.modifiers;
import com.speedment.codegen.lang.models.modifiers.Keyword.abstract_;
import com.speedment.codegen.lang.models.modifiers.Keyword.final_;
import com.speedment.codegen.lang.models.modifiers.Keyword.private_;
import com.speedment.codegen.lang.models.modifiers.Keyword.protected_;
import com.speedment.codegen.lang.models.modifiers.Keyword.public_;
import com.speedment.codegen.lang.models.modifiers.Keyword.static_;
import com.speedment.codegen.lang.models.modifiers.Keyword.strictfp_;

/**
 *
 * @author Emil Forslund
 * @param <T>
 */
public interface ClassModifier<T extends ClassModifier<T>> extends public_<T>, protected_<T>, private_<T>, abstract_<T>, static_<T>, final_<T>, strictfp_<T> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T public_() {
    getModifiers().add(PUBLIC);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ClassModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T protected_() {
    getModifiers().add(PROTECTED);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ClassModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T private_() {
    getModifiers().add(PRIVATE);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ClassModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T abstract_() {
    getModifiers().add(ABSTRACT);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ClassModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T static_() {
    getModifiers().add(STATIC);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ClassModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T final_() {
    getModifiers().add(FINAL);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ClassModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T strictfp_() {
    getModifiers().add(STRICTFP);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/ClassModifier.java/right.java
}