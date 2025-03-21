package com.speedment.codegen.lang.interfaces;
import com.speedment.codegen.lang.models.Type;
import java.util.Collection;
import java.util.Set;

/**
 * A trait for models that can throw exceptions.
 * 
 * @author Emil Forslund
 * @param <T> The extending type
 */
public interface HasThrows<T extends HasThrows<T>> {
  /**
     * Adds the specified exception reference to the <code>throws</code>-clause 
     * of this model.
     * 
     * @param exception  the new exception
     * @return           a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T add(final Type exception) {
    getExceptions().add(exception.copy());
    return (T) this;
  }

  @SuppressWarnings(value = { "unchecked" }) default T addAllExceptions(final Collection<? extends Type> exceptions) {
    exceptions.forEach(this::add);
    return (T) this;
  }

  /**
     * Returns a <code>Set</code> with all the exceptions that can be throwed by
     * this model.
     * <p>
     * The set returned must be mutable for changes!
     * 
     * @return  all exceptions
     */
  Set<Type> getExceptions();
}