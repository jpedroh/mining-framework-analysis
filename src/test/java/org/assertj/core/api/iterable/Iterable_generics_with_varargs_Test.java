package org.assertj.core.api.iterable;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Iterable_generics_with_varargs_Test {
  @SuppressWarnings(value = { "unchecked", "rawtypes" }) @Test public void testWithoutGenerics() {
    List strings = asList("a", "b", "c");
    assertThat(strings).contains("a", "b");
  }

  @Test public void testConcreteType() {
    List<String> strings = asList("a", "b", "c");
    assertThat(strings).contains("a", "b");
  }

  @Test @Ignore public void testListAssertWithGenerics() {
  }
}