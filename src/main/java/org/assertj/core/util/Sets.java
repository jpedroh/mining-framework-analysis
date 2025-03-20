package org.assertj.core.util;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Utility methods related to {@link Set}s.
 * 
 * @author alruiz
 */
public final class Sets {
  /**
   * Creates a <em>mutable</em> {@code HashSet}.
   * 
   * @param <T> the generic type of the {@code HashSet} to create.
   * @return the created {@code HashSet}.
   */
  public static <T extends java.lang.Object> HashSet<T> newHashSet() {
    return new HashSet<T>();
  }

  /**
   * Creates a <em>mutable</em> {@code HashSet} containing the given elements.
   * 
   * @param <T> the generic type of the {@code HashSet} to create.
   * @param elements the elements to store in the {@code HashSet}.
   * @return the created {@code HashSet}, or {@code null} if the given array of elements is {@code null}.
   */
  public static <T extends java.lang.Object> HashSet<T> newHashSet(Iterable<? extends T> elements) {
    if (elements == null) {
      return null;
    }
    HashSet<T> set = newHashSet();
    for (T e : elements) {
      set.add(e);
    }
    return set;
  }

  /**
   * Creates a <em>mutable</em> {@code LinkedHashSet}.
   * 
   * @param <T> the generic type of the {@code LinkedHashSet} to create.
   * @return the created {@code LinkedHashSet}.
   */
  public static <T extends java.lang.Object> LinkedHashSet<T> newLinkedHashSet() {
    return new LinkedHashSet<T>();
  }

  /**
   * Creates a <em>mutable</em> {@link LinkedHashSet} containing the given elements.
   * 
   * @param <T> the generic type of the {@code LinkedHashSet} to create.
   * @param elements the elements to store in the {@code LinkedHashSet}.
   * @return the created {@code LinkedHashSet}, or {@code null} if the given array of elements is {@code null}.
   */
  @SafeVarargs public static <T extends java.lang.Object> LinkedHashSet<T> newLinkedHashSet(T... elements) {
    if (elements == null) {
      return null;
    }
    LinkedHashSet<T> set = newLinkedHashSet();
    java.util.Collections.addAll(set, elements);
    return set;
  }

  private Sets() {
  }
}