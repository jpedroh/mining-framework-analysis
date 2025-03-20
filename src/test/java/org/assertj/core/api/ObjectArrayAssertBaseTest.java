package org.assertj.core.api;
import static org.assertj.core.test.ExpectedException.none;
import static org.assertj.core.test.ObjectArrays.emptyArray;
import static org.mockito.Mockito.mock;
import org.assertj.core.internal.Iterables;
import org.assertj.core.internal.ObjectArrays;
import org.assertj.core.test.ExpectedException;
import org.junit.Rule;

/**
 * Base class for {@link ObjectArrayAssert} tests.
 * 
 * @author Olivier Michallat
 */
public abstract class ObjectArrayAssertBaseTest extends BaseTestTemplate<ObjectArrayAssert<Object>, Object[]> {
  protected ObjectArrays arrays;

  protected @Rule public 
<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/api/ObjectArrayAssertBaseTest.java/left.java
  Iterables
=======
  ExpectedException
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/api/ObjectArrayAssertBaseTest.java/right.java
   
<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/api/ObjectArrayAssertBaseTest.java/left.java
  iterables
=======
  thrown = none()
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/api/ObjectArrayAssertBaseTest.java/right.java
  ;

  @Override protected ObjectArrayAssert<Object> create_assertions() {
    return new ObjectArrayAssert<>(emptyArray());
  }

  @Override protected void inject_internal_objects() {
    super.inject_internal_objects();
    arrays = mock(ObjectArrays.class);
    iterables = mock(Iterables.class);
    assertions.arrays = arrays;
    assertions.iterables = iterables;
  }

  protected ObjectArrays getArrays(ObjectArrayAssert<Object> someAssertions) {
    return someAssertions.arrays;
  }
}