package com.speedment.codegen.lang.interfaces;
import com.speedment.codegen.lang.models.Initalizer;
import java.util.Collection;
import java.util.List;

/**
 * A trait for models that contain {@link Initalizer} components.
 * 
 * @author Emil Forslund
 * @param <T> The extending type
 */
public interface HasInitalizers<T extends HasInitalizers<T>> {
  /**
     * Adds the specified {@link Initalizer} to this model.
     * 
     * @param init  the new child
     * @return      a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T add(
<<<<<<< /usr/src/app/output/pyknic/codegen/3e0d4067f68ebf4163e850c7031571271b4893e0/src/main/java/com/speedment/codegen/lang/interfaces/HasInitalizers.java/left.java
  final Initalizer init
=======
  final Initalizer initalizer
>>>>>>> /usr/src/app/output/pyknic/codegen/3e0d4067f68ebf4163e850c7031571271b4893e0/src/main/java/com/speedment/codegen/lang/interfaces/HasInitalizers.java/right.java
  ) {
    getInitalizers().add(
<<<<<<< /usr/src/app/output/pyknic/codegen/3e0d4067f68ebf4163e850c7031571271b4893e0/src/main/java/com/speedment/codegen/lang/interfaces/HasInitalizers.java/left.java
    init
=======
    initalizer.copy()
>>>>>>> /usr/src/app/output/pyknic/codegen/3e0d4067f68ebf4163e850c7031571271b4893e0/src/main/java/com/speedment/codegen/lang/interfaces/HasInitalizers.java/right.java
    );
    return (T) this;
  }

  @SuppressWarnings(value = { "unchecked" }) default T addAllInitalizers(final Collection<? extends Initalizer> initalizers) {
    initalizers.forEach(this::add);
    return (T) this;
  }

  /**
     * Returns a list of all intializers in this model.
     * <p>
     * The list returned must be mutable for changes!
     * 
     * @return  the initalizers
     */
  List<Initalizer> getInitalizers();
}