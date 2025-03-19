package org.neo4j.imports;
import java.util.Iterator;

/**
 * @author tbaum
 * @since 15.03.2015
 */
public class StreamUtil {
  public static <T extends java.lang.Object> Iterator<T> iteratorOf(final Supplier<T> s) {
    return new Iterator<T>() {
      T next = nextValue();

      @Override public boolean hasNext() {
        return next != null;
      }

      @Override public T next() {
        T result = next;
        next = nextValue();
        return result;
      }

      public void remove() {
      }

      T nextValue() {
        try {
          return s.get();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  static <T extends java.lang.Object> Iterator<T> emptyIterator() {
    return new Iterator<T>() {
      @Override public boolean hasNext() {
        return false;
      }

      @Override public T next() {
        return null;
      }

      public void remove() {
      }
    };
  }

  interface Supplier<T extends java.lang.Object> {
    T get() throws Exception;
  }
}