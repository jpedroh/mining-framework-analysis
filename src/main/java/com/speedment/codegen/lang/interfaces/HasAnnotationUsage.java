package com.speedment.codegen.lang.interfaces;
import com.speedment.codegen.lang.models.AnnotationUsage;
import java.util.Collection;
import java.util.List;

/**
 * A trait for models that contains {@link AnnotationUsage} components.
 * 
 * @author Emil Forslund
 * @param <T> The extending type
 */
public interface HasAnnotationUsage<T extends HasAnnotationUsage<T>> {
  /**
     * Adds the specified {@link AnnotationUsage} to this model.
     * 
     * @param annotation  the new child
     * @return            a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T add(final AnnotationUsage annotation) {
    getAnnotations().add(annotation.copy());
    return (T) this;
  }

  @SuppressWarnings(value = { "unchecked" }) default T addAllAnnotations(final Collection<? extends AnnotationUsage> annotations) {
    annotations.forEach(this::add);
    return (T) this;
  }

  /**
     * Returns a list of all the annotation usages in this model.
     * <p>
     * The list returned must be mutable for changes!
     * 
     * @return  the annotations. 
     */
  List<AnnotationUsage> getAnnotations();
}