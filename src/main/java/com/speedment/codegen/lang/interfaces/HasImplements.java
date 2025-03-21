package com.speedment.codegen.lang.interfaces;
import com.speedment.codegen.lang.models.Type;
import java.util.Collection;
import java.util.List;

/**
 * A trait for models that have interfaces as supertypes.
 * 
 * @author Emil Forslund
 * @param <T> The extending type
 */
public interface HasImplements<T extends HasImplements<T>> {
  /**
     * Adds the specified supertype to this model. The type should represent
     * an interface.
     * 
     * @param interf  the new child
     * @return        a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T add(final Type interf) {
    getInterfaces().add(interf.copy());
    return (T) this;
  }

  @SuppressWarnings(value = { "unchecked" }) default T addAllImplements(final Collection<? extends Type> interf) {
    interf.forEach(this::add);
    return (T) this;
  }

  /**
     * Returns a list of all the interfaces implemented by this model.
     * <p>
     * The list returned must be mutable for changes!
     * 
     * @return  the interfaces
     */
  List<Type> getInterfaces();
}