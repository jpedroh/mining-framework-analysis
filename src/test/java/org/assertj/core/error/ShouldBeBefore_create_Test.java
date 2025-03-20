package org.assertj.core.error;
import static junit.framework.Assert.assertEquals;
import static org.assertj.core.error.ShouldBeBefore.shouldBeBefore;
import static org.assertj.core.util.Dates.parse;
import org.assertj.core.description.Description;
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link ShouldBeBefore#create(Description, org.assertj.core.presentation.Representation)}</code>.
 * 
 * @author Joel Costigliola
 */
public class ShouldBeBefore_create_Test {
  private ErrorMessageFactory factory;

  @Before public void setUp() {
    factory = shouldBeBefore(parse("2011-01-01"), parse("2012-01-01"));
  }

  @Test public void should_create_error_message() {
    String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
    assertEquals("[Test] \nExpecting:\n  <2011-01-01T00:00:00>\nto be strictly before:\n  <2012-01-01T00:00:00>", message);
  }
}