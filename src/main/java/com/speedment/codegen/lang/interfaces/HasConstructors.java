package com.speedment.codegen.lang.interfaces;
import com.speedment.codegen.lang.models.Constructor;
import java.util.Collection;
import java.util.List;

/**
 * A trait for models that contain {@link Constructor} components.
 * 
 * @author Emil Forslund
 * @param <T> The extending type
 */
public interface HasConstructors<T extends HasConstructors<T>> {
  /**
     * Adds the specified {@link Constructor} to this model.
     * 
     * @param constr  the new child
     * @return        a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T add(final Constructor constr) {
    getConstructors().add(constr.copy());
    return (T) this;
  }

  @SuppressWarnings(value = { "unchecked" }) default T addAllConstructors(final Collection<Constructor> constr) {
    constr.forEach(this::add);
    return (T) this;
  }

  /**
     * Returns a list of all the constructors of this model.
     * <p>
     * The list returned must be mutable for changes!
     * 
     * @return  the constructors 
     */
  List<Constructor> getConstructors();
}