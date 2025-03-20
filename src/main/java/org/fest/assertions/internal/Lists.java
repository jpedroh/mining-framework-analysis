package org.fest.assertions.internal;
import static org.fest.assertions.error.ShouldBeSorted.*;
import static org.fest.assertions.error.ShouldContainAtIndex.shouldContainAtIndex;
import static org.fest.assertions.error.ShouldHaveAtIndex.shouldHaveAtIndex;
import static org.fest.assertions.error.ShouldNotContainAtIndex.shouldNotContainAtIndex;
import static org.fest.assertions.internal.CommonValidations.checkIndexValueIsValid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.fest.assertions.core.AssertionInfo;
import org.fest.assertions.core.Condition;
import org.fest.assertions.data.Index;
import org.fest.util.ComparatorBasedComparisonStrategy;
import org.fest.util.ComparisonStrategy;
import org.fest.util.StandardComparisonStrategy;
import org.fest.util.VisibleForTesting;

/**
 * Reusable assertions for <code>{@link List}</code>s.
 * 
 * @author Alex Ruiz
 * @author Yvonne Wang
 * @author Joel Costigliola
 */
public class Lists {
  private static final Lists INSTANCE = new Lists();

  /**
   * Returns the singleton instance of this class.
   * @return the singleton instance of this class.
   */
  public static Lists instance() {
    return INSTANCE;
  }

  private ComparisonStrategy comparisonStrategy;

  @VisibleForTesting Failures failures = Failures.instance();

  @VisibleForTesting Lists() {
    this(StandardComparisonStrategy.instance());
  }

  public Lists(ComparisonStrategy comparisonStrategy) {
    this.comparisonStrategy = comparisonStrategy;
  }

  @VisibleForTesting public Comparator<?> getComparator() {
    if (comparisonStrategy instanceof ComparatorBasedComparisonStrategy) {
      return ((ComparatorBasedComparisonStrategy) comparisonStrategy).getComparator();
    }
    return null;
  }

  /**
   * Verifies that the given {@code List} contains the given object at the given index.
   * @param info contains information about the assertion.
   * @param actual the given {@code List}.
   * @param value the object to look for.
   * @param index the index where the object should be stored in the given {@code List}.
   * @throws AssertionError if the given {@code List} is {@code null} or empty.
   * @throws NullPointerException if the given {@code Index} is {@code null}.
   * @throws IndexOutOfBoundsException if the value of the given {@code Index} is equal to or greater than the size of the given
   *           {@code List}.
   * @throws AssertionError if the given {@code List} does not contain the given object at the given index.
   */
  public void assertContains(AssertionInfo info, List<?> actual, Object value, Index index) {
    assertNotNull(info, actual);
    Iterables.instance().assertNotEmpty(info, actual);
    checkIndexValueIsValid(index, actual.size() - 1);
    Object actualElement = actual.get(index.value);
    if (areEqual(actualElement, value)) {
      return;
    }
    throw failures.failure(info, shouldContainAtIndex(actual, value, index, actual.get(index.value), comparisonStrategy));
  }

  /**
   * Verifies that the given {@code List} does not contain the given object at the given index.
   * @param info contains information about the assertion.
   * @param actual the given {@code List}.
   * @param value the object to look for.
   * @param index the index where the object should be stored in the given {@code List}.
   * @throws AssertionError if the given {@code List} is {@code null}.
   * @throws NullPointerException if the given {@code Index} is {@code null}.
   * @throws AssertionError if the given {@code List} contains the given object at the given index.
   */
  public void assertDoesNotContain(AssertionInfo info, List<?> actual, Object value, Index index) {
    assertNotNull(info, actual);
    checkIndexValueIsValid(index, Integer.MAX_VALUE);
    int indexValue = index.value;
    if (indexValue >= actual.size()) {
      return;
    }
    Object actualElement = actual.get(index.value);
    if (!areEqual(actualElement, value)) {
      return;
    }
    throw failures.failure(info, shouldNotContainAtIndex(actual, value, index, comparisonStrategy));
  }

  /**
   * Verifies that the actual list is sorted into ascending order according to the natural ordering of its elements.
   * <p>
   * All list elements must implement the {@link Comparable} interface and must be mutually comparable (that is, e1.compareTo(e2)
   * must not throw a ClassCastException for any elements e1 and e2 in the list), examples :
   * <ul>
   * <li>a list composed of {"a1", "a2", "a3"} is ok because the element type (String) is Comparable</li>
   * <li>a list composed of Rectangle {r1, r2, r3} is <b>NOT ok</b> because Rectangle is not Comparable</li>
   * <li>a list composed of {True, "abc", False} is <b>NOT ok</b> because elements are not mutually comparable</li>
   * </ul>
   * Empty lists are considered sorted.</br> Unique element lists are considered sorted unless the element type is not Comparable.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code List}.
   * 
   * @throws AssertionError if the actual list is not sorted into ascending order according to the natural ordering of its
   *           elements.
   * @throws AssertionError if the actual list is <code>null</code>.
   * @throws AssertionError if the actual list element type does not implement {@link Comparable}.
   * @throws AssertionError if the actual list elements are not mutually {@link Comparable}.
   */
  public void assertIsSorted(AssertionInfo info, List<?> actual) {
    assertNotNull(info, actual);
    if (comparisonStrategy instanceof ComparatorBasedComparisonStrategy) {
      Comparator<?> comparator = ((ComparatorBasedComparisonStrategy) comparisonStrategy).getComparator();
      assertIsSortedAccordingToComparator(info, actual, comparator);
      return;
    }
    try {
      List<Comparable<Object>> comparableList = listOfComparableElements(actual);
      if (comparableList.size() <= 1) {
        return;
      }
      for (int i = 0; i < comparableList.size() - 1; i++) {
        if (comparableList.get(i).compareTo(comparableList.get(i + 1)) > 0) {
          throw failures.failure(info, shouldBeSorted(i, actual));
        }
      }
    } catch (ClassCastException e) {
      throw failures.failure(info, shouldHaveMutuallyComparableElements(actual));
    }
  }

  /**
   * Verifies that the actual list is sorted according to the given comparator.</br> Empty lists are considered sorted whatever
   * the comparator is.</br> One element lists are considered sorted if element is compatible with comparator.
   * 
   * @param info contains information about the assertion.
   * @param actual the given {@code List}.
   * @param comparator the {@link Comparator} used to compare list elements
   * 
   * @throws AssertionError if the actual list is not sorted according to the given comparator.
   * @throws AssertionError if the actual list is <code>null</code>.
   * @throws NullPointerException if the given comparator is <code>null</code>.
   * @throws AssertionError if the actual list elements are not mutually comparabe according to given Comparator.
   */
  @SuppressWarnings(value = { "rawtypes", "unchecked" }) public void assertIsSortedAccordingToComparator(AssertionInfo info, List<?> actual, Comparator<? extends Object> comparator) {
    assertNotNull(info, actual);
    if (comparator == null) {
      throw new NullPointerException("The given comparator should not be null");
    }
    try {
      if (actual.size() == 0) {
        return;
      }
      Comparator rawComparator = comparator;
      if (actual.size() == 1) {
        rawComparator.compare(actual.get(0), actual.get(0));
        return;
      }
      for (int i = 0; i < actual.size() - 1; i++) {
        if (rawComparator.compare(actual.get(i), actual.get(i + 1)) > 0) {
          throw failures.failure(info, shouldBeSortedAccordingToGivenComparator(i, actual, comparator));
        }
      }
    } catch (ClassCastException e) {
      throw failures.failure(info, shouldHaveComparableElementsAccordingToGivenComparator(actual, comparator));
    }
  }

  /**
   * Verifies that the given {@code List} satisfies the given <code>{@link Condition}</code> at the given index.
   * @param <T>       the type of the actual value and the type of values that given {@code Condition} takes.
   * @param info      contains information about the assertion.
   * @param actual    the given {@code List}.
   * @param condition the given {@code Condition}.
   * @param index     the index where the object should be stored in the given {@code List}.
   * @throws AssertionError            if the given {@code List} is {@code null} or empty.
   * @throws NullPointerException      if the given {@code Index} is {@code null}.
   * @throws IndexOutOfBoundsException if the value of the given {@code Index} is equal to or greater than the size of
   *                                   the given {@code List}.
   * @throws NullPointerException      if the given {@code Condition} is {@code null}.
   * @throws AssertionError            if the value in the given {@code List} at the given index does not satisfy the given {@code Condition}.
   */
  public <T extends java.lang.Object> void assertHas(AssertionInfo info, List<T> actual, Condition<? super T> condition, Index index) {
    assertNotNull(info, actual);
    assertNotNull(condition);
    Iterables.instance().assertNotEmpty(info, actual);
    checkIndexValueIsValid(index, actual.size() - 1);
    int indexValue = index.value;
    if (indexValue >= actual.size()) {
      return;
    }
    T actualElement = actual.get(index.value);
    if (condition.matches(actualElement)) {
      return;
    }
    throw failures.failure(info, shouldHaveAtIndex(actual, condition, index, actualElement));
  }

  @SuppressWarnings(value = { "unchecked" }) private static List<Comparable<Object>> listOfComparableElements(List<?> collection) {
    List<Comparable<Object>> listOfComparableElements = new ArrayList<Comparable<Object>>();
    for (Object object : collection) {
      listOfComparableElements.add((Comparable<Object>) object);
    }
    return listOfComparableElements;
  }

  private void assertNotNull(AssertionInfo info, List<?> actual) {
    Objects.instance().assertNotNull(info, actual);
  }

  private void assertNotNull(Condition<?> condition) {
    Conditions.instance().assertIsNotNull(condition);
  }

  /**
   * Delegates to {@link ComparisonStrategy#areEqual(Object, Object)}
   */
  private boolean areEqual(Object actual, Object other) {
    return comparisonStrategy.areEqual(actual, other);
  }
}