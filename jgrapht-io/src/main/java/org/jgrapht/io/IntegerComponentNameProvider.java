package org.jgrapht.io;
import java.util.*;
import org.jgrapht.nio.IntegerIdProvider;

/**
 * Assigns a unique integer to represent each component. Each instance of provider maintains an
 * internal map between every component it has ever seen and the unique integer representing that
 * edge. As a result it is probably desirable to have a separate instance for each distinct graph.
 * 
 * @param <T> the component type
 *
 * @author Trevor Harmon
 * 
 * @deprecated Use {@link IntegerIdProvider} instead.
 */
@Deprecated public class IntegerComponentNameProvider<T extends java.lang.Object> implements ComponentNameProvider<T> {
  private static final int DEFAULT_BASE = 1;

  /**
     * The first ID to use.
     */
  private int base;

  private int nextID;

  private final Map<T, Integer> idMap = new HashMap<>();

  /**
     * Create a provider with the default base id (1).
     */
  public IntegerComponentNameProvider() {
    this(DEFAULT_BASE);
  }

  /**
     * Create a provider with a given arbitrary base
     * 
     * @param base the first Id to use.
     */
  public IntegerComponentNameProvider(int base) {
    this.base = base;
    clear();
  }

  /**
     * Clears all cached identifiers, and resets the unique identifier counter.
     */
  public void clear() {
    nextID = base;
    idMap.clear();
  }

  /**
     * Returns the string representation of a component.
     *
     * @param component the component to be named
     */
  @Override public String getName(T component) {
    Integer id = idMap.get(component);
    if (id == null) {
      id = nextID++;
      idMap.put(component, id);
    }
    return id.toString();
  }
}