package org.assertj.core.api;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Sets.newLinkedHashSet;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import java.util.Iterator;
import org.junit.Test;

/**
 * Tests for <code>{@link Assertions#assertThat(Iterator)}</code>.
 * 
 * @author Julien Meddah
 * @author Joel Costigliola
 * @author Mikhail Mazursky
 */
public class Assertions_assertThat_with_Iterator_Test {
  @Test public void should_create_Assert() {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    AbstractIterableAssert<?, ? extends Iterable<? extends Object>, Object> assertions = Assertions.assertThat(newLinkedHashSet());
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/e48c141d6c44ba4ea76f65400de6a8a19a5722cc/src/test/java/org/assertj/core/api/Assertions_assertThat_with_Iterator_Test.java/right.java

    assertNotNull(Assertions.assertThat(newLinkedHashSet()));
  }

  @Test public void should_initialise_actual() {
    Iterator<String> names = asList("Luke", "Leia").iterator();

<<<<<<< Unknown file: This is a bug in JDime.
=======
    AbstractIterableAssert<?, ? extends Iterable<? extends String>, String> assertions = assertThat(names);
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/e48c141d6c44ba4ea76f65400de6a8a19a5722cc/src/test/java/org/assertj/core/api/Assertions_assertThat_with_Iterator_Test.java/right.java


<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/e48c141d6c44ba4ea76f65400de6a8a19a5722cc/src/test/java/org/assertj/core/api/Assertions_assertThat_with_Iterator_Test.java/left.java
    assertThat(assertThat(names).actual, hasItems("Leia", "Luke"))
=======
    assertThat(assertions.actual).containsOnly("Leia", "Luke")
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/e48c141d6c44ba4ea76f65400de6a8a19a5722cc/src/test/java/org/assertj/core/api/Assertions_assertThat_with_Iterator_Test.java/right.java
    ;
  }

  @Test public void should_allow_null() {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    AbstractIterableAssert<?, ? extends Iterable<? extends String>, String> assertions = assertThat((Iterator<String>) null);
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/e48c141d6c44ba4ea76f65400de6a8a19a5722cc/src/test/java/org/assertj/core/api/Assertions_assertThat_with_Iterator_Test.java/right.java

    assertThat(assertThat((Iterator<String>) null).actual).isNull();
  }

  @Test public void should_not_consume_iterator_when_asserting_non_null() throws Exception {
    Iterator<?> iterator = mock(Iterator.class);
    assertThat(iterator).isNotNull();
    verifyZeroInteractions(iterator);
  }

  @Test public void iterator_can_be_asserted_twice_even_though_it_can_be_iterated_only_once() throws Exception {
    Iterator<String> names = asList("Luke", "Leia").iterator();
    assertThat(names).containsExactly("Luke", "Leia").containsExactly("Luke", "Leia");
  }
}