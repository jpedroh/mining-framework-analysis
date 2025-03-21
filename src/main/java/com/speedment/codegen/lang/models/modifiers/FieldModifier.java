package com.speedment.codegen.lang.models.modifiers;
import com.speedment.codegen.lang.models.modifiers.Keyword.final_;
import com.speedment.codegen.lang.models.modifiers.Keyword.private_;
import com.speedment.codegen.lang.models.modifiers.Keyword.protected_;
import com.speedment.codegen.lang.models.modifiers.Keyword.public_;
import com.speedment.codegen.lang.models.modifiers.Keyword.static_;
import com.speedment.codegen.lang.models.modifiers.Keyword.synchronized_;
import com.speedment.codegen.lang.models.modifiers.Keyword.transient_;
import com.speedment.codegen.lang.models.modifiers.Keyword.volatile_;

/**
 *
 * @author Emil Forslund
 * @param <T>
 */
public interface FieldModifier<T extends FieldModifier<T>> extends public_<T>, protected_<T>, private_<T>, static_<T>, final_<T>, synchronized_<T>, transient_<T>, volatile_<T> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T public_() {
    getModifiers().add(PUBLIC);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/FieldModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T protected_() {
    getModifiers().add(PROTECTED);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/FieldModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T private_() {
    getModifiers().add(PRIVATE);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/FieldModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T static_() {
    getModifiers().add(STATIC);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/FieldModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T final_() {
    getModifiers().add(FINAL);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/FieldModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T synchronized_() {
    getModifiers().add(SYNCHRONIZED);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/FieldModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T transient_() {
    getModifiers().add(TRANSIENT);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/FieldModifier.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) default T volatile_() {
    getModifiers().add(VOLATILE);
    return (T) this;
  }
>>>>>>> /usr/src/app/output/pyknic/codegen/e303235d2e0f90a3ce6a72974a8de27ec144599a/src/main/java/com/speedment/codegen/lang/models/modifiers/FieldModifier.java/right.java
}