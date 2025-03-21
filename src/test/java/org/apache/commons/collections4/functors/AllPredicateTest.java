package org.apache.commons.collections4.functors;
import org.apache.commons.collections4.Predicate;
import static org.apache.commons.collections4.functors.AllPredicate.allPredicate;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.Collection;
import java.util.Collections;

/**
 * Tests the org.apache.commons.collections.functors.AllPredicate class.
 *
 * @since 3.0
 */
@SuppressWarnings(value = { "boxing" }) public class AllPredicateTest extends AbstractAnyAllOnePredicateTest<Integer> {
  /**
     * Creates a new {@code TestAllPredicate}.
     */
  public AllPredicateTest() {
    super(42);
  }

  /**
     * {@inheritDoc}
     */
  @Override protected final Predicate<Integer> getPredicateInstance(final Predicate<? super Integer>... predicates) {
    return AllPredicate.allPredicate(predicates);
  }

  /**
     * {@inheritDoc}
     */
  @Override protected final Predicate<Integer> getPredicateInstance(final Collection<Predicate<Integer>> predicates) {
    return AllPredicate.allPredicate(predicates);
  }

  /**
     * Verifies that providing an empty predicate array evaluates to true.
     */
  @SuppressWarnings(value = { "unchecked" }) @Test public void emptyArrayToGetInstance() {
    assertTrue(getPredicateInstance(new Predicate[] {  }).evaluate(null));
  }

  /**
     * Verifies that providing an empty predicate collection evaluates to true.
     */
  @Test public void emptyCollectionToGetInstance() {
    final Predicate<Integer> allPredicate = getPredicateInstance(Collections.<Predicate<Integer>>emptyList());
    assertTrue(allPredicate.evaluate(getTestValue()));
  }

  /**
     * Tests whether a single true predicate evaluates to true.
     */
  @SuppressWarnings(value = { "unchecked" }) @Test public void oneTruePredicate() {
    final Predicate<Integer> predicate = createMockPredicate(true);
    assertTrue(allPredicate(predicate).evaluate(getTestValue()));
  }

  /**
     * Tests whether a single false predicate evaluates to true.
     */
  @SuppressWarnings(value = { "unchecked" }) @Test public void oneFalsePredicate() {
    final Predicate<Integer> predicate = createMockPredicate(false);
    assertFalse(allPredicate(predicate).evaluate(getTestValue()));
  }

  /**
     * Tests whether multiple true predicates evaluates to true.
     */
  @Test public void allTrue() {
    assertTrue(getPredicateInstance(true, true).evaluate(getTestValue()));
    assertTrue(getPredicateInstance(true, true, true).evaluate(getTestValue()));
  }

  /**
     * Tests whether combining some true and one false evalutes to false.  Also verifies that only the first
     * false predicate is actually evaluated
     */
  @Test public void trueAndFalseCombined() {
    assertFalse(getPredicateInstance(false, null).evaluate(getTestValue()));
    assertFalse(getPredicateInstance(false, null, null).evaluate(getTestValue()));
    assertFalse(getPredicateInstance(true, false, null).evaluate(getTestValue()));
    assertFalse(getPredicateInstance(true, true, false).evaluate(getTestValue()));
    assertFalse(getPredicateInstance(true, true, false, null).evaluate(getTestValue()));
  }
}