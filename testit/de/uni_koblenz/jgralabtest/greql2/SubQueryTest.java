package de.uni_koblenz.jgralabtest.greql2;
import static junit.framework.Assert.fail;
import org.junit.Test;
import de.uni_koblenz.jgralab.greql2.exception.GreqlException;

public class SubQueryTest extends GenericTest {
  @Test public void testSimpleSubQuery() {
    fail("not yet implemented");
  }

  @Test public void testSubQueryWithSQsUsingOtherSQs() {
    fail("not yet implemented");
  }

  @Test(expected = GreqlException.class) public void testRecursiveSubQueryError() {
    fail("not yet implemented");
  }

  @Test(expected = GreqlException.class) public void testShadowingSubQueryError() {
  }

  @Test(expected = GreqlException.class) public void testSubQueryArgCountMismatchError1() {
    fail("not yet implemented");
  }

  @Test(expected = GreqlException.class) public void testSubQueryArgCountMismatchError2() {
    fail("not yet implemented");
  }

  @Test(expected = GreqlException.class) public void testSubQueryArgCountMismatchError3() {
    fail("not yet implemented");
  }

  @Test(expected = GreqlException.class) public void testSubQueryArgCountMismatchError4() {
    fail("not yet implemented");
  }

  @Test public void testSubQueryAdd3() {
    fail("not yet implemented");
  }
}