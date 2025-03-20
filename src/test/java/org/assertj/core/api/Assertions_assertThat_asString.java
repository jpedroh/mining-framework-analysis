package org.assertj.core.api;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.test.ExpectedException.none;
import org.assertj.core.test.ExpectedException;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for Assert.asString() methods
 */
public class Assertions_assertThat_asString {
  @Rule public ExpectedException thrown = none();

  @Test public void should_pass_string_asserts_on_string_objects_with_asString() {
    Object stringAsObject = "hello world";
    assertThat(stringAsObject).asString().contains("hello");
  }

  @Test public void should_fail_string_asserts_on_non_string_objects_even_with_asString() {
    Object nonString = new Object();
    thrown.expectAssertionError("an instance of:\n <java.lang.String>\nbut was instance of:\n <java.lang.Object>");
    assertThat(nonString).asString().contains("hello");
  }
}