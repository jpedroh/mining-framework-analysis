package org.assertj.core.api;
import static org.assertj.core.util.Sets.newLinkedHashSet;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import org.junit.Test;

/**
 * Tests for <code>{@link Assertions#assertThat(Iterable)}</code>.
 * 
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Assertions_assertThat_with_Iterable_Test {
  @Test public void should_create_Assert() {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    AbstractIterableAssert<?, ? extends Iterable<?>, Object> assertions = Assertions.assertThat(newLinkedHashSet());
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/e48c141d6c44ba4ea76f65400de6a8a19a5722cc/src/test/java/org/assertj/core/api/Assertions_assertThat_with_Iterable_Test.java/right.java

    assertNotNull(Assertions.assertThat(newLinkedHashSet()));
  }

  @Test public void should_pass_actual() {
    Iterable<String> names = newLinkedHashSet("Luke");

<<<<<<< Unknown file: This is a bug in JDime.
=======
    AbstractIterableAssert<?, ? extends Iterable<? extends String>, String> assertions = Assertions.assertThat(names);
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/e48c141d6c44ba4ea76f65400de6a8a19a5722cc/src/test/java/org/assertj/core/api/Assertions_assertThat_with_Iterable_Test.java/right.java

    assertSame(names, Assertions.assertThat(names).actual);
  }
}