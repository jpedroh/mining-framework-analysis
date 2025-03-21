package com.speedment.codegen.lang.interfaces;
import java.util.Collections;
import java.util.Collection;
import java.util.List;

/**
 * A trait for models that contains code.
 * 
 * @author Emil Forslund
 * @param <T> The extending type
 */
public interface HasCode<T extends HasCode<T>> {
  /**
     * Adds the specified row of code to this model.
     * 
     * @param row  the row
     * @return     a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T add(final String row) {
    getCode().add(row);
    return (T) this;
  }

  /**
     * Adds all the specified rows to this model.
     * 
     * @param rows  the rows
     * @return      a reference to this
     */
  @SuppressWarnings(value = { "unchecked" }) default T add(String... rows) {
    Collections.addAll(getCode(), rows);
    return (T) this;
  }

  @SuppressWarnings(value = { "unchecked" }) default T addAllRows(final Collection<String> rows) {
    rows.forEach(this::add);
    return (T) this;
  }

  /**
     * Returns a list of the code rows of this model.
     * <p>
     * The list returned must be mutable for changes!
     * 
     * @return  the code rows
     */
  List<String> getCode();
}