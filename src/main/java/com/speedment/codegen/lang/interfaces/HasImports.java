package com.speedment.codegen.lang.interfaces;
import com.speedment.codegen.lang.models.Import;
import java.util.Collection;
import java.util.List;

/**
 * A trait for models that contain {@link Import} components.
 * 
 * @author Emil Forslund
 * @param <T> The extending type
 */
public interface HasImports<T extends HasImports<T>> {
  /**
     * Adds the specified {@link Import} to this model.
     * 
     * @param dep  the new child
     * @return     a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T add(final Import dep) {
    getImports().add(dep.copy());
    return (T) this;
  }

  @SuppressWarnings(value = { "unchecked" }) default T addAllImports(final Collection<? extends Import> imports) {
    imports.forEach(this::add);
    return (T) this;
  }

  /**
     * Returns a list of all imports in this model.
     * <p>
     * The list returned must be mutable for changes!
     * 
     * @return  the imports
     */
  List<Import> getImports();
}