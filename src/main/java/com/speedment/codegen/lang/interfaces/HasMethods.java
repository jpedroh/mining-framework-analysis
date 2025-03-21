package com.speedment.codegen.lang.interfaces;
import com.speedment.codegen.lang.models.Method;
import java.util.Collection;
import java.util.List;

/**
 * A trait for models that contain {@link Method} components.
 * 
 * @author Emil Forslund
 * @param <T> The extending type
 */
public interface HasMethods<T extends HasMethods<T>> {
  /**
     * Adds the specified {@link Method} to this model.
     * 
     * @param meth  the new child
     * @return      a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T add(final Method meth) {
    getMethods().add(meth.copy());
    return (T) this;
  }

  @SuppressWarnings(value = { "unchecked" }) default T addAllMethods(final Collection<? extends Method> methods) {
    methods.forEach(this::add);
    return (T) this;
  }

  /**
     * Returns a list of all methods in this model.
     * <p>
     * The list returned must be mutable for changes!
     * 
     * @return  the methods
     */
  List<Method> getMethods();
}