package net.named_data.jndn.tests.unit_tests;
import net.named_data.jndn.Name;
import net.named_data.jndn.util.Common;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TestNameComponentMethods {
  @Test public void testUnicode() {
    Name.Component comp1 = new Name.Component("entr\u00e9e");
    String expected = "entr%C3%A9e";
    assertEquals("Unicode URI not decoded correctly", expected, comp1.toEscapedString());
  }

  @Test public void testHashCode() {
    Name.Component foo1 = new Name.Component("foo");
    Name.Component foo2 = new Name.Component("foo");
    assertEquals("Hash codes for same strings are not equal", foo1.hashCode(), foo2.hashCode());
    Name.Component bar = new Name.Component("bar");
    assertTrue("Hash codes for different strings are not different", foo1.hashCode() != bar.hashCode());
  }

  @Test public void testCompare() {
    Name.Component c7f = new Name("/%7F").get(0);
    Name.Component c80 = new Name("/%80").get(0);
    Name.Component c81 = new Name("/%81").get(0);
    assertTrue("%81 should be greater than %80", c81.compare(c80) > 0);
    assertTrue("%80 should be greater than %7f", c80.compare(c7f) > 0);
  }

  private static Common dummyCommon_ = new Common();
}