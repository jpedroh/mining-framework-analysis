package com.speedment.codegen.lang.interfaces;
import com.speedment.codegen.lang.models.Field;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * A trait for models that contain {@link Field} components.
 * 
 * @author Emil Forslund
 * @param <T> The extending type
 */
public interface HasFields<T extends HasFields<T>> {
  /**
     * Adds the specified {@link Field} to this model.
     * 
     * @param field  the new child
     * @return       a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T add(final Field field) {
    getFields().add(field.copy());
    return (T) this;
  }

  /**
     * Adds all the specified {@link Field} members to this model.
     * 
     * @param fields  the new children
     * @return        a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T addAllFields(final Collection<? extends Field> fields) {

<<<<<<< /usr/src/app/output/pyknic/codegen/3e0d4067f68ebf4163e850c7031571271b4893e0/src/main/java/com/speedment/codegen/lang/interfaces/HasFields.java/left.java
    getFields().addAll(fields)
=======
    fields.forEach(this::add)
>>>>>>> /usr/src/app/output/pyknic/codegen/3e0d4067f68ebf4163e850c7031571271b4893e0/src/main/java/com/speedment/codegen/lang/interfaces/HasFields.java/right.java
    ;
    return (T) this;
  }

  /**
     * Returns a list of all the fields in this model.
     * <p>
     * The list returned must be mutable for changes!
     * 
     * @return  all the fields 
     */
  List<Field> getFields();
}