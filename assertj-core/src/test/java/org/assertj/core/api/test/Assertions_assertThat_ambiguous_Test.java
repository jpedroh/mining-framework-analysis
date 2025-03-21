package org.assertj.core.api.test;
import static org.assertj.core.api.Assertions.assertThatIterator;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.PredicateAssert.assertThatPredicate;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class Assertions_solving_assertThat_ambiguous_Test {
  @Test void should_resolve_ambiguous_assertThat_for_interfaces_and_use_assertThat_for_classes() {
    IteratorPredicate<String> iteratorPredicate = new IteratorPredicate<>();
    assertThatPredicate(iteratorPredicate).rejects("foo");
    assertThatIterator(iteratorPredicate).isExhausted();
    assertThat("").isEmpty();
    assertThat(2L).isPositive();
  }

  @Test void should_resolve_ambiguous_assertThat_for_SqlException() {
    SQLException sqlException = new SQLException("test");
    Assertions.assertThat(sqlException).hasMessage("test");
  }

  static class IteratorPredicate<T extends java.lang.Object> implements Iterator<T>, Predicate<T> {
    @Override public boolean test(T t) {
      return false;
    }

    @Override public boolean hasNext() {
      return false;
    }

    @Override public T next() {
      return null;
    }
  }
}