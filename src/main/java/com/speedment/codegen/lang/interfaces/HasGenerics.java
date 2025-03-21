package com.speedment.codegen.lang.interfaces;
import com.speedment.codegen.lang.models.Generic;
import java.util.Collection;
import java.util.List;

/**
 * A trait for models that contain {@link Generic} components.
 * 
 * @author Emil Forslund
 * @param <T> The extending type
 */
public interface HasGenerics<T extends HasGenerics<T>> {
  /**
     * Adds the specified {@link Generic} to this model.
     * 
     * @param generic  the new child
     * @return         a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T add(final Generic generic) {
    getGenerics().add(generic.copy());
    return (T) this;
  }

  @SuppressWarnings(value = { "unchecked" }) default T addAllGenerics(final Collection<? extends Generic> generics) {
    generics.forEach(this::add);
    return (T) this;
  }

  /**
     * Returns a list of all the generics in this model.
     * <p>
     * The list returned must be mutable for changes!
     * 
     * @return  the generics 
     */
  List<Generic> getGenerics();
}