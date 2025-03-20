package org.assertj.core.api.object;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.test.ExpectedException.none;
import static org.assertj.core.util.BigDecimalComparator.BIG_DECIMAL_COMPARATOR;
import java.math.BigDecimal;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.test.Employee;
import org.assertj.core.test.ExpectedException;
import org.assertj.core.test.Name;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for <code>{@link ObjectAssert#extracting(String[])}</code>.
 */
public class ObjectAssert_extracting_Test {
  @Rule public ExpectedException thrown = none();

  private Employee luke;

  @Before public void setup() {
    luke = new Employee(2L, new Name("Luke", "Skywalker"), 26);
  }

  @Test public void should_allow_assertions_on_array_of_properties_extracted_from_given_object_by_name() {
    assertThat(luke).extracting("id", "name").hasSize(2).doesNotContainNull();
    assertThat(luke).extracting("name.first", "name.last").hasSize(2).containsExactly("Luke", "Skywalker");
  }

  @Test public void should_allow_assertions_on_array_of_properties_extracted_from_given_object_with_lambdas() {
    assertThat(luke).extracting(Employee::getName, Employee::getAge).hasSize(2).doesNotContainNull();
    assertThat(luke).extracting((employee) -> employee.getName().first, (employee) -> employee.getName().getLast()).hasSize(2).containsExactly("Luke", "Skywalker");
  }

  @Test public void should_use_property_field_names_as_description_when_extracting_tuples_list() {
    Employee luke = new Employee(2L, new Name("Luke", "Skywalker"), 26);
    thrown.expectAssertionErrorWithMessageContaining("[Extracted: name.first, name.last]");
    assertThat(luke).extracting("name.first", "name.last").isEmpty();
  }

  @Test public void should_keep_existing_description_if_set_when_extracting_tuples_list() {
    Employee luke = new Employee(2L, new Name("Luke", "Skywalker"), 26);
    thrown.expectAssertionErrorWithMessageContaining("[check luke first name]");
    assertThat(luke).as("check luke first name").extracting("name.first").isEmpty();
  }

  @Test public void should_allow_to_specify_type_comparator_after_using_extracting_on_object() {
    Person obiwan = new Person("Obi-Wan");
    obiwan.setHeight(new BigDecimal("1.820"));
    assertThat(obiwan).extracting("name", "height").usingComparatorForType(BIG_DECIMAL_COMPARATOR, BigDecimal.class).containsExactly("Obi-Wan", new BigDecimal("1.82"));
  }

  @SuppressWarnings(value = { "unused" }) private static class Person {
    private final String name;

    private BigDecimal height;

    public Person(String name) {
      this.name = name;
    }

    public void setHeight(BigDecimal height) {
      this.height = height;
    }
  }
}