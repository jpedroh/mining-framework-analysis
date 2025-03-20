package org.assertj.core.api.object;
import static java.util.Collections.EMPTY_MAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.internal.ObjectsBaseTest.defaultTypeComparators;
import static org.assertj.core.test.AlwaysEqualStringComparator.ALWAY_EQUALS;
import static org.mockito.Mockito.verify;
import java.util.Comparator;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ObjectAssertBaseTest;
import org.assertj.core.test.Jedi;
import org.junit.Test;

/**
 * Tests for <code>{@link ObjectAssert#isEqualToIgnoringGivenFields(Object, String...)}</code>.
 * 
 * @author Nicolas François
 * @author Mikhail Mazursky
 */
public class ObjectAssert_isEqualToIgnoringGivenFields_Test extends ObjectAssertBaseTest {
  private Jedi other = new Jedi("Yoda", "Blue");

  @Override protected ObjectAssert<Jedi> invoke_api_method() {
    return assertions.isEqualToIgnoringGivenFields(other, "lightSaberColor");
  }

  @Override @SuppressWarnings(value = { "unchecked" }) protected void verify_internal_effects() {
    verify(objects).assertIsEqualToIgnoringGivenFields(getInfo(assertions), getActual(assertions), other, EMPTY_MAP, defaultTypeComparators(), "lightSaberColor");
  }

  @Test public void should_be_able_to_use_a_comparator_for_specified_fields() {
    Jedi actual = new Jedi("Yoda", "Green");
    Jedi other = new Jedi("Luke", "Blue");
    assertThat(actual).usingComparatorForFields(ALWAY_EQUALS, "name").isEqualToIgnoringGivenFields(other, "lightSaberColor");
  }

  @Test public void comparators_for_fields_should_have_precedence_over_comparators_for_types() {
    Comparator<String> comparator = new Comparator<String>() {
      public int compare(String o1, String o2) {
        return o1.compareTo(o2);
      }
    };
    Jedi actual = new Jedi("Yoda", "green");
    Jedi other = new Jedi("Luke", "green");
    assertThat(actual).usingComparatorForFields(ALWAY_EQUALS, "name").usingComparatorForType(comparator, String.class).isEqualToIgnoringGivenFields(other, "lightSaberColor");
  }

  @Test public void should_be_able_to_use_a_comparator_for_specified_type() {
    Jedi actual = new Jedi("Yoda", "green");
    Jedi other = new Jedi("Luke", "blue");
    assertThat(actual).usingComparatorForType(ALWAY_EQUALS, String.class).isEqualToIgnoringGivenFields(other, "lightSaberColor");
  }
}