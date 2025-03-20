package org.assertj.core.api;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Assertion methods for {@link AtomicIntegerFieldUpdater}s.
 * <p>
 * To create an instance of this class, invoke <code>{@link Assertions#assertThat(AtomicIntegerFieldUpdater)}</code>.
 * </p>
 *
 * @param <OBJECT> the type of the object holding the updatable field.
 * @author epeee
 */
public class AtomicIntegerFieldUpdaterAssert<OBJECT extends java.lang.Object> extends AbstractAtomicFieldUpdaterAssert<AtomicIntegerFieldUpdaterAssert<OBJECT>, Integer, AtomicIntegerFieldUpdater<OBJECT>, OBJECT> {
  public AtomicIntegerFieldUpdaterAssert(AtomicIntegerFieldUpdater<OBJECT> actual) {
    super(actual, AtomicIntegerFieldUpdaterAssert.class, false);
  }

  /**
   * Verifies that the actual atomic field updater contains the given value at the given object.
   * <p>
   * Example:
   * <pre><code class='java'> // person is an instance of a Person class holding a non-private volatile int field (age).
   * AtomicIntegerFieldUpdater&lt;Person&gt; ageUpdater = AtomicIntegerFieldUpdater.newUpdater(Person.class, "age");
   * 
   * // this assertion succeeds:
   * ageUpdater.set(person, 25);
   * assertThat(ageUpdater).hasValue(25, person);
   *
   * // this assertion fails:
   * fieldUpdater.set(person, 28);
   * assertThat(fieldUpdater).hasValue(25, person);</code></pre>
   *
   * @param expectedValue the expected value inside the {@code OBJECT}.
   * @param obj the object holding the updatable field.
   * @return this assertion object.
   */
  @Override public AtomicIntegerFieldUpdaterAssert<OBJECT> hasValue(Integer expectedValue, OBJECT obj) {
    return super.hasValue(expectedValue, obj);
  }

  @Override protected Integer getActualValue(OBJECT obj) {
    return actual.get(obj);
  }
}