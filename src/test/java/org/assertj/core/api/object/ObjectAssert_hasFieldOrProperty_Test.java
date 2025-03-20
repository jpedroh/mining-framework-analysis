package org.assertj.core.api.object;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ObjectAssertBaseTest;
import org.assertj.core.test.Jedi;
import org.junit.Test;

/**
 * Tests for <code>{@link ObjectAssert#hasFieldOrProperty(String)}</code>.
 * 
 * @author Libor Ondrusek
 */
public class ObjectAssert_hasFieldOrProperty_Test extends ObjectAssertBaseTest {
  public static final String FIELD_NAME = "name";

  @Override protected ObjectAssert<Jedi> invoke_api_method() {
    return assertions.hasFieldOrProperty(FIELD_NAME);
  }

  @Override protected void verify_internal_effects() {
    verify(objects).assertHasFieldOrProperty(getInfo(assertions), getActual(assertions), FIELD_NAME);
  }

  @Test public void shoud_fail_if_field_or_property_does_not_exists() {
    thrown.expectAssertionError("%nExpecting%n  <Yoda the Jedi>%nto have a property or a field named <\"not_exists_in_jedi_object\">");
    Jedi jedi = new Jedi("Yoda", "Blue");
    assertThat(jedi).hasFieldOrProperty("not_exists_in_jedi_object");
  }

  @Test public void shoud_fail_if_given_field_or_property_name_is_null() {
    thrown.expectIllegalArgumentException("The name of the property/field to read should not be null");
    Jedi jedi = new Jedi("Yoda", "Blue");
    assertThat(jedi).hasFieldOrProperty(null);
  }
}